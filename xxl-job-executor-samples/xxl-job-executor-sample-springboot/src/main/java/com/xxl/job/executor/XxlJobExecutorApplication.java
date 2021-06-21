package com.xxl.job.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobExecutorApplication {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(XxlJobExecutorApplication.class, args);
//        File logFile=new File("\\data\\applogs\\xxl-job\\jobhandler\\2021-05-10\\14010.log");
//		LineNumberReader reader = null;
//		reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
//		String line = null;
//		int toLineNum = 0;
//		while ((line = reader.readLine())!=null) {
//			System.out.println(line);
//		}
	}

}