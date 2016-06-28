package com;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.config.AppConfig;
import com.exception.ExitException;
import com.service.FileReaderService;
import com.thread.ProduceConsume;
import com.thread.fileread.Reader;

@SpringBootApplication
public class FileIoApplication implements CommandLineRunner {

	private static ApplicationContext ctx;
	
	@Autowired
	private FileReaderService frService;
	
	@Override
	public void run(String... args) {
		System.out.println(this.frService.getMsg());
		if (args.length > 0 && args[0].equals("exitcode")) {
			throw new ExitException();
		}
	}
	
	public static void main(String[] args) {
		ctx = SpringApplication.run(FileIoApplication.class, args);
		//readerWriterWithQueue();
		readerWriterNIO();
	}
	
	public static void readerWriterNIO() {
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) ctx.getBean("taskExecutor");
		
		AppConfig appConfig = (AppConfig) ctx.getBean("appConfig");
		int numOfCopies = appConfig.getFileCopyLatchCount();
		
		long startTime = System.nanoTime();
		for(int i = 0; i < numOfCopies; i++) {
			ProduceConsume produceConsume = (ProduceConsume) ctx.getBean("produceConsume");
			produceConsume.setName("copy" + i);
			taskExecutor.execute(produceConsume);
			
		}
		
		try {
			appConfig.fileCopyLatch().await();
			long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
			for (;;) {
				int count = taskExecutor.getActiveCount();
				System.out.println("Active Threads : " + count);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (count == 0) {
					taskExecutor.shutdown();
					break;
				}
			}
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}
	
	public static void readerWriterWithQueue() {
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) ctx.getBean("taskExecutor");
		 
		Reader readerTask1 = (Reader) ctx.getBean("reader");
	    readerTask1.setName("Thread 1");
	    taskExecutor.execute(readerTask1);

	    Reader readerTask2 = (Reader) ctx.getBean("reader");
	    readerTask2.setName("Thread 2");
	    taskExecutor.execute(readerTask2);

	    Reader readerTask3 = (Reader) ctx.getBean("reader");
	    readerTask3.setName("Thread 3");
	    taskExecutor.execute(readerTask3);
	    
	    AppConfig appConfig = (AppConfig) ctx.getBean("appConfig");
	    BlockingQueue queue = (BlockingQueue) appConfig.blockingQueue();
	    System.out.println(queue.size());

		for (;;) {
			int count = taskExecutor.getActiveCount();
			System.out.println("Active Threads : " + count);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count == 0) {
				taskExecutor.shutdown();
				break;
			}
		}
	}
}
