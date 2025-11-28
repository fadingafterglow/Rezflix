package ua.edu.ukma.springers.rezflix.services.rendering;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.FileSystemUtils;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class RenderingJobConfiguration {

    public static final String EPISODE_ID_PARAM = "episodeId";
    public static final String UNRENDERED_PATH_PARAM = "unrenderedPath";
    public static final String RENDERED_PATH_PARAM = "renderedPath";

    public static final String RENDER_COMPLETED_STATUS = ExitStatus.COMPLETED.getExitCode();
    public static final String RENDER_FAILED_STATUS = "COMPLETED WITH FFMPEG ERRORS";

    @Bean
    public Job renderingJob(Step storagePreparationStep,
                            Step ffmpegStep,
                            Step recordSuccessfulRenderStep,
                            Step recordFailedRenderStep,
                            Step cleanupAfterSuccessfulRenderStep,
                            Step cleanupAfterFailedRenderStep,
                            JobRepository jobRepository) {
        return new JobBuilder("renderingJob", jobRepository)
                .start(storagePreparationStep)
                .next(ffmpegStep)
                    .on(RENDER_COMPLETED_STATUS)
                    .to(recordSuccessfulRenderStep)
                    .next(cleanupAfterSuccessfulRenderStep)
                .from(ffmpegStep)
                    .on(RENDER_FAILED_STATUS)
                    .to(recordFailedRenderStep)
                    .next(cleanupAfterFailedRenderStep)
                .from(ffmpegStep)
                    .on("*")
                    .fail()
                .end()
                .build();
    }

    @Bean
    public Step storagePreparationStep(Tasklet storagePreparationTasklet, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("storagePreparationStep", jobRepository)
                .tasklet(storagePreparationTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet storagePreparationTasklet(@Value("${storage.rendered-episodes.path}") Path renderedEpisodesDir) {
        return (contribution, context) -> {
            String episodeId = context.getStepContext().getJobParameters().get(EPISODE_ID_PARAM).toString();
            Path episodeDir = renderedEpisodesDir.resolve(episodeId);
            Files.createDirectories(episodeDir);
            context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put(RENDERED_PATH_PARAM, episodeDir.toAbsolutePath().toString());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step ffmpegStep(SystemCommandTasklet ffmpegTasklet, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("ffmpegStep", jobRepository)
                .tasklet(ffmpegTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public SystemCommandTasklet ffmpegTasklet(@Value("#{jobParameters['" + UNRENDERED_PATH_PARAM + "']}") String unrenderedPath,
                                 @Value("#{jobExecutionContext['" + RENDERED_PATH_PARAM + "']}") String renderedPath,
                                 @Value("${rendering.timeout}") Duration timeout,
                                 @BatchTaskExecutor TaskExecutor taskExecutor, JobExplorer jobExplorer) {
        SystemCommandTasklet tasklet = new SystemCommandTasklet();
        tasklet.setCommand(
            "ffmpeg",
            "-loglevel", "error",
            "-i", unrenderedPath,
            "-filter_complex", "[0:v]split=3[v360][v720][v1080]",
            "-map", "[v360]", "-map", "0:a", "-c:v:0", "h264", "-s:v:0", "640x360", "-b:v:0", "700k", "-profile:v:0", "baseline",
            "-map", "[v720]", "-map", "0:a", "-c:v:1", "h264", "-s:v:1", "1280x720", "-b:v:1", "3000k", "-profile:v:1", "main",
            "-map", "[v1080]", "-map", "0:a", "-c:v:2", "h264", "-s:v:2", "1920x1080", "-b:v:2", "5500k", "-profile:v:2", "high",
            "-c:a", "aac", "-b:a", "128k",
            "-f", "hls",
            "-hls_time", "4",
            "-hls_playlist_type", "vod",
            "-hls_segment_filename", "v%v/seg_%04d.m4s",
            "-hls_segment_type", "fmp4",
            "-master_pl_name", "master.m3u8",
            "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2",
            "v%v/index.m3u8"
        );
        tasklet.setWorkingDirectory(renderedPath);
        tasklet.setTimeout(timeout.toMillis());
        tasklet.setInterruptOnCancel(true);
        tasklet.setTaskExecutor(taskExecutor);
        tasklet.setJobExplorer(jobExplorer);
        tasklet.setSystemProcessExitCodeMapper(c -> new ExitStatus(c == 0 ? RENDER_COMPLETED_STATUS : RENDER_FAILED_STATUS));
        return tasklet;
    }

    @Bean
    public Step recordSuccessfulRenderStep(FilmEpisodeRepository repository, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("recordSuccessfulRenderStep", jobRepository)
                .tasklet(updateEpisodeStatusTasklet(repository, FilmEpisodeStatus.RENDERED), transactionManager)
                .build();
    }

    @Bean
    public Step recordFailedRenderStep(FilmEpisodeRepository repository, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("recordFailedRenderStep", jobRepository)
                .tasklet(updateEpisodeStatusTasklet(repository, FilmEpisodeStatus.RENDERING_FAILED), transactionManager)
                .build();
    }

    private Tasklet updateEpisodeStatusTasklet(FilmEpisodeRepository repository, FilmEpisodeStatus status) {
        return (contribution, context) -> {
            UUID episodeId = (UUID) context.getStepContext().getJobParameters().get(EPISODE_ID_PARAM);
            repository.updateEpisodeStatus(episodeId, status);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step cleanupAfterSuccessfulRenderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("cleanupAfterSuccessfulRenderStep", jobRepository)
                .tasklet(
                        (contribution, context) -> {
                            String unrenderedPath = (String) context.getStepContext().getJobParameters().get(UNRENDERED_PATH_PARAM);
                            Files.deleteIfExists(Path.of(unrenderedPath));
                            return RepeatStatus.FINISHED;
                        }, transactionManager)
                .build();
    }

    @Bean
    public Step cleanupAfterFailedRenderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("cleanupAfterFailedRenderStep", jobRepository)
                .tasklet(
                        (contribution, context) -> {
                            String unrenderedPath = (String) context.getStepContext().getJobParameters().get(UNRENDERED_PATH_PARAM);
                            Files.deleteIfExists(Path.of(unrenderedPath));
                            String renderedPath = (String) context.getStepContext().getJobExecutionContext().get(RENDERED_PATH_PARAM);
                            FileSystemUtils.deleteRecursively(Path.of(renderedPath));
                            return RepeatStatus.FINISHED;
                        }, transactionManager)
                .build();
    }
}
