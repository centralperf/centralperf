package org.centralperf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableJpaRepositories("org.centralperf.repository")
public class ApplicationConfig {

    @Bean
    public ThreadPoolTaskScheduler scheduledRunThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("CentralPerfScheduledRun-");
        return threadPoolTaskScheduler;
    }

}
