package faang.school.accountservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor.threads.count}")
    private int threadsCount;

    @Bean
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(threadsCount);
    }
}