package ua.edu.ukma.springers.rezflix.services.rendering;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import ua.edu.ukma.springers.rezflix.logging.Markers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RenderingService {

    private final Job renderingJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

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
                try {
                    String unrenderedPath = execution.getJobParameters().getString(RenderingJobConfiguration.UNRENDERED_PATH_PARAM);
                    if (unrenderedPath != null)
                        Files.deleteIfExists(Paths.get(unrenderedPath));
                    ExecutionContext executionContext = execution.getExecutionContext();
                    if (executionContext.containsKey(RenderingJobConfiguration.RENDERED_PATH_PARAM)) {
                        String renderedPath = executionContext.getString(RenderingJobConfiguration.RENDERED_PATH_PARAM);
                        FileSystemUtils.deleteRecursively(Paths.get(renderedPath));
                    }
                }
                catch (IOException e) {
                    log.error(Markers.EXCEPTION, "Failed to cleanup rendering files for {}", episodeId, e);
                }
            }
        }
    }

    private JobParametersBuilder jobParametersBuilder(UUID episodeId) {
        return new JobParametersBuilder()
                .addJobParameter(RenderingJobConfiguration.EPISODE_ID_PARAM, episodeId, UUID.class);
    }
}
