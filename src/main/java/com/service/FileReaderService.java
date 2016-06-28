package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileReaderService {
	
	@Value("${fileName:hello}")
	private String fileName;

	public String getMsg() {
		return "Hello " + this.fileName;
	}

}
