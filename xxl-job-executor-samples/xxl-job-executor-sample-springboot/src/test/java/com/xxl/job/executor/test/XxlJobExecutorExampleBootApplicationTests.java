package com.xxl.job.executor.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void test() throws Exception {
		String startDate="2022-01-01";
		Date startTime = sdf.parse(startDate);
		te();
		System.out.println("成功");

	}

	public void te() throws Exception {
		try {
			int s=Integer.valueOf("sdf");
		}catch (Exception e){
			throw new RuntimeException("sdfsdf");
		}finally {
//			throw new RuntimeException("sdfsdf");
		}
	}

}