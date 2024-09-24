package com.qkj.project.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 线程池配置
 */
@Data
@ToString
@Configuration
@ConfigurationProperties(prefix ="task.pool")
public class TaskThreadPoolConfig {
    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 线程空闲时间
     */
    private int keepAliveSeconds;

    /**
     * 任务队列容量（阻塞队列）
     */
    private int queueCapacity;

    @Bean("threadPool")
    public Executor threadPool(TaskThreadPoolConfig cfg) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(cfg.getCorePoolSize());
        //最大线程数
        executor.setMaxPoolSize(cfg.getMaxPoolSize());
        //队列容量
        executor.setQueueCapacity(cfg.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds(cfg.getKeepAliveSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix("shaex-thread-");
        /*
        当poolSize已达到maxPoolSize，如何处理新任务（是拒绝还是交由其它线程处理）
        CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
