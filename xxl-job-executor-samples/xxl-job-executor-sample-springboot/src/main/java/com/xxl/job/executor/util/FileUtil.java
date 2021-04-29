package com.xxl.job.executor.util;

import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {
    public static void inputStreamToFile(InputStream is, String path) throws Exception {
        if (is==null){
            System.out.println("空");
        }
        FileOutputStream fos = new FileOutputStream(path);
        byte[] b = new byte[1024];
        while ((is.read(b)) != -1) {
            fos.write(b);// 写入数据
        }
        is.close();
        fos.close();// 保存数据
    }
}
