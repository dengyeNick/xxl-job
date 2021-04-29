package com.xxl.job.executor.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class KnimeUtil {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * 生成执行knime脚本的命令行，并写入与脚本文件同一路径下的bat.cmd文件中
	 * @param path 需要执行的knime脚本路径
	 * @param knimePath knime的jar包安装路径
	 */
	public void executeKnime(String path, String knimePath) throws Exception {
		// 先转一遍反斜杠
		path = path.replaceAll("\\/", "\\\\");
		// workflowFile
		String workFlowFile = " -workflowFile=\"" + path + "\"";
		String command = knimePath + " -application org.knime.product.KNIME_BATCH_APPLICATION -reset"
				+ workFlowFile;
		try {
			
			log.info("-----cmd执行中-----");
			log.info("cmd路径：" + command);
			Process process = Runtime.getRuntime().exec(command);
			
			// 输入流
			InputStream inputStream = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "gb2312"));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			log.info("-----cmd执行完毕-----");
		} catch (Exception e) { 
			e.printStackTrace();
			throw new RuntimeException("执行knime时出现未知异常");
		}
		log.info("-----cmd命令执行结束-----");
	}

	
	public static void main(String[] args) throws Exception {
		KnimeUtil a = new KnimeUtil();
		String knimePath = "cmd /c D:\\knime_4.3.1.win32.win32.x86_64\\knime_4.3.1\\plugins\\org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar";
		a.executeKnime("C:\\Users\\kuangjintian\\Desktop\\aa.knwf", knimePath);
	}
}
