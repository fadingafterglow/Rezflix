package ua.edu.ukma.springers.rezflix.services.rendering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.logging.Markers;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class RenderingService {

    private final Job renderingJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    private final FilmEpisodeRepository filmEpisodeRepository;

    private final Path unrenderedEpisodesDir;
    private final int maxRenderingAttempts;

    public RenderingService(Job renderingJob, JobLauncher jobLauncher, JobExplorer jobExplorer, JobOperator jobOperator,
                            FilmEpisodeRepository filmEpisodeRepository,
                            @Value("${storage.unrendered-episodes.path}") Path unrenderedEpisodesDir,
                            @Value("${rendering.max-attempts}") int maxRenderingAttempts
    ) {
        this.renderingJob = renderingJob;
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.filmEpisodeRepository = filmEpisodeRepository;
        this.unrenderedEpisodesDir = unrenderedEpisodesDir;
        this.maxRenderingAttempts = maxRenderingAttempts;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void launchRenderingJob(UUID episodeId, Path episodePath) {
        JobParameters jobParameters = jobParametersBuilder(episodeId)
                .addString(RenderingJobConfiguration.UNRENDERED_PATH_PARAM, episodePath.toAbsolutePath().toString(), false)
                .toJobParameters();
        try {
            jobLauncher.run(renderingJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(Markers.EXCEPTION, "Failed to launch rendering job for {}", episodeId, e);
        }
    }

    public void cleanupRenderingFiles(UUID episodeId) {
        JobInstance instance = jobExplorer.getJobInstance(renderingJob.getName(), jobParametersBuilder(episodeId).toJobParameters());
        if (instance == null) return;
        for (JobExecution execution : jobExplorer.getJobExecutions(instance)) {
            try {
                jobOperator.stop(execution.getId());
            } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
                // ignore
            } finally {
                cleanupRenderingFiles(episodeId, execution);
            }
        }
    }

    @Scheduled(initialDelayString = "5S", fixedRateString = "${rendering.repair-interval}")
    public void repairRenders() throws IOException {
        log.info("Started repairing renders");
        try (var files = Files.walk(unrenderedEpisodesDir, 1)) {
            files
                .filter(Files::isRegularFile)
                .map(p -> {
                    try {
                        UUID episodeId = UUID.fromString(p.getFileName().toString());
                        return Pair.of(episodeId, p);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(p -> repairRender(p.getFirst(), p.getSecond()));
        }
        log.info("Finished repairing renders");
    }

    private void repairRender(UUID episodeId, Path episodePath) {
        JobInstance instance = jobExplorer.getJobInstance(renderingJob.getName(), jobParametersBuilder(episodeId).toJobParameters());
        if (instance == null) {
            log.info("No job instances found for {}. Launching execution", episodeId);
            launchRenderingJob(episodeId, episodePath);
            return;
        }

        List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
        if (executions.isEmpty()) {
            log.info("No job executions found for {}. Launching execution", episodeId);
            launchRenderingJob(episodeId, episodePath);
            return;
        }

        if (hasRunningExecution(executions))
            return;

        JobExecution abortedExecution = findAbortedExecution(executions);
        if (abortedExecution != null) {
            log.info("Found aborted execution for {}. Cleaning up", episodeId);
            filmEpisodeRepository.updateEpisodeStatus(episodeId, FilmEpisodeStatus.RENDERING_FAILED);
            cleanupRenderingFiles(episodeId, abortedExecution);
            return;
        }

        if (executions.size() < maxRenderingAttempts) {
            log.info("Restarting failed rendering job for {}", episodeId);
            launchRenderingJob(episodeId, episodePath);
        }
        else {
            log.info("Maximum rendering attempts reached for {}. Cleaning up.", episodeId);
            filmEpisodeRepository.updateEpisodeStatus(episodeId, FilmEpisodeStatus.RENDERING_FAILED);
            executions.forEach(e -> cleanupRenderingFiles(episodeId, e));
        }
    }

    private boolean hasRunningExecution(List<JobExecution> executions) {
        for (JobExecution execution : executions) {
            if (execution.isRunning()) {
                return true;
            }
        }
        return false;
    }

    private JobExecution findAbortedExecution(List<JobExecution> executions) {
        for (JobExecution execution : executions) {
            BatchStatus batchStatus = execution.getStatus();
            if (batchStatus == BatchStatus.STOPPED || batchStatus == BatchStatus.ABANDONED || batchStatus == BatchStatus.UNKNOWN) {
                return execution;
            }
        }
        return null;
    }

    private void cleanupRenderingFiles(UUID episodeId, JobExecution execution) {
        try {
            String unrenderedPath = execution.getJobParameters().getString(RenderingJobConfiguration.UNRENDERED_PATH_PARAM);
            if (unrenderedPath != null)
                Files.deleteIfExists(Path.of(unrenderedPath));
            ExecutionContext executionContext = execution.getExecutionContext();
            if (executionContext.containsKey(RenderingJobConfiguration.RENDERED_PATH_PARAM)) {
                String renderedPath = executionContext.getString(RenderingJobConfiguration.RENDERED_PATH_PARAM);
                FileSystemUtils.deleteRecursively(Path.of(renderedPath));
            }
        }
        catch (IOException e) {
            log.error(Markers.EXCEPTION, "Failed to cleanup rendering files for {}", episodeId, e);
        }
    }

    private JobParametersBuilder jobParametersBuilder(UUID episodeId) {
        return new JobParametersBuilder()
                .addJobParameter(RenderingJobConfiguration.EPISODE_ID_PARAM, episodeId, UUID.class);
    }
}
