package com.thread.fileread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.config.AppConfig;

@Component
@Scope("prototype")
@Import({AppConfig.class})
public class Reader implements Runnable{

	@Autowired
    private AppConfig appConfig;
	
	String name;
		
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public void run() {
		
		System.out.println(name + " is running");
		
		long startTime = System.nanoTime();
		
        Path file = Paths.get(appConfig.fileName);
        try
        {
            //Java 8: Stream class
            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
            
            for( String line : (Iterable<String>) lines::iterator )
            {
               //System.out.println(line);
            	try {
        			appConfig.blockingQueue().put(line);
        			appConfig.blockingQueue().put(line);
        			appConfig.blockingQueue().put(line);
        			//Thread.sleep(5000);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
            }
        
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
 
        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
		
		
		
		System.out.println(name + " is finished");

	}

	
	
}
