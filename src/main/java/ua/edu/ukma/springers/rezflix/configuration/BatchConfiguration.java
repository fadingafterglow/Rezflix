package ua.edu.ukma.springers.rezflix.configuration;

import org.springframework.boot.autoconfigure.batch.BatchTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration(proxyBeanMethods = false)
public class BatchConfiguration {

    @Bean(defaultCandidate = false)
    @BatchTaskExecutor
    public TaskExecutor batchTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setThreadNamePrefix("batch-");
        executor.setTaskTerminationTimeout(30_000);
        return executor;
    }
}
