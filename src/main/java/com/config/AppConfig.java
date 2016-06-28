package com.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan(basePackages = "com.thread")
public class AppConfig {

	public static String programPath = "E:/dummy_data";
	public static String fileName = "E:/dummy_data/big.txt";
	
	@Value("${fileCopyLatchCount:100}")
	private int fileCopyLatchCount;
	
	@Value("${fileCopyBarrierCount:100}")
	private int fileCopyBarrierCount;
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}
	
	@Bean
	public BlockingQueue<String> blockingQueue() {
		return new LinkedBlockingQueue<String>();
	}
	
	@Bean
	public CountDownLatch fileCopyLatch() {
		return new CountDownLatch(fileCopyLatchCount);
	}
	
	@Bean
	public CyclicBarrier fileCopyCyclicBarrier() {
		return new CyclicBarrier(fileCopyBarrierCount);
	}

	public int getFileCopyLatchCount() {
		return fileCopyLatchCount;
	}

	public int getFileCopyBarrierCount() {
		return fileCopyBarrierCount;
	}
	
}
