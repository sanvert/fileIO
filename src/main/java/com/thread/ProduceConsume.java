package com.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.config.AppConfig;

@Component
@Scope("prototype")
@Import({AppConfig.class})
public class ProduceConsume implements Runnable {
	
	private static final int BUFFER = 8192;
	
	@Autowired
    private AppConfig appConfig;
	
	String name;
		
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public void run() {
		boolean sleepToTest = true;
        String copyToFileName = appConfig.programPath + "/dummy_write/" + "_" + this.name;
        nioTransferCopy(new File(appConfig.fileName), new File(copyToFileName), appConfig.fileCopyLatch(), sleepToTest);
        //nioBufferCopy(new File(appConfig.fileName), new File(copyToFileName), appConfig.fileCopyLatch(), sleepToTest);

	}
	
	private static void nioBufferCopy(File source, File target, CountDownLatch latch, boolean sleepToTest) {
		
        try (FileChannel in = new FileInputStream(source).getChannel();
             FileChannel out = new FileOutputStream(target).getChannel()){

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER);
            while (in.read(buffer) != -1) {
                buffer.flip();

                while(buffer.hasRemaining()){
                    out.write(buffer);
                }

                buffer.clear();
            }
            
            if(sleepToTest)
            	Thread.sleep(5000);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
        	ie.printStackTrace();
        } finally {
        	if(latch != null)
        		latch.countDown();
        }
    }

    private static void nioTransferCopy(File source, File target, CountDownLatch latch, boolean sleepToTest) {

        try (FileChannel in = new FileInputStream(source).getChannel();
           	 FileChannel out = new FileOutputStream(target).getChannel()){

            long size = in.size();
            long transferred = in.transferTo(0, size, out);

            while(transferred != size){
                transferred += in.transferTo(transferred, size - transferred, out);
            }
            
            if(sleepToTest)
            	Thread.sleep(5000);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
        	ie.printStackTrace();
        } finally {
        	if(latch != null)
        		latch.countDown();
        }
    }
}

