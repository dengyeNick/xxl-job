package com.xxl.job.executor.util;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
public class MinioClientUtils {

    private static Log logger = LogFactory.getLog(MinioClientUtils.class);

    private static String miniopath = null;

    private static String minioname = null;

    private static String miniopass = null;

    private static MinioClient minioClient;

    public static String rootpath = null;

    private static String imgRootpath = null;

    public String getMiniopath() {
        return miniopath;
    }

    @Value("${minio.url.miniopath}")
    public void setMiniopath(String miniopath) {
        MinioClientUtils.miniopath = miniopath;
    }

    public String getMinioname() {
        return minioname;
    }

    @Value("${minio.name.minioname}")
    public void setMinioname(String minioname) {
        MinioClientUtils.minioname = minioname;
    }

    public String getMiniopass() {
        return miniopass;
    }

    @Value("${minio.pass.miniopass}")
    public void setMiniopass(String miniopass) {
        MinioClientUtils.miniopass = miniopass;
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public void setMinioClient(MinioClient minioClient) {
        MinioClientUtils.minioClient = minioClient;
    }

    public String getRootpath() {
        return rootpath;
    }

    @Value("${minio.root.rootpath}")
    public void setRootpath(String rootpath) {
        MinioClientUtils.rootpath = rootpath;
    }

    public String getImgRootpath() {
        return imgRootpath;
    }

    @Value("${minio.imgRoot.imgRootpath}")
    public void setImgRootpath(String imgRootpath) {
        MinioClientUtils.imgRootpath = imgRootpath;
    }

    /**
     * @param userbucket ??????
     * @param fileRoot ?????? "File/" ??? "img/"
     * @param fileName ?????????
     * @param inputStream
     * @param contentType ???????????????
     */
    public static void Uploaders(String userbucket, String fileRoot, String fileName, InputStream inputStream,
                                 String contentType) {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret
            // key.
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("    \"Statement\": [\n");
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:GetBucketLocation\",\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::" + userbucket + "\"\n");
            builder.append("        },\n");
            builder.append("        {\n");
            builder.append("            \"Action\": \"s3:GetObject\",\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::" + userbucket + "/*\"\n");
            builder.append("        }\n");
            builder.append("    ],\n");
            builder.append("    \"Version\": \"2012-10-17\"\n");
            builder.append("}\n");

            // Check if the bucket already exists.
            boolean isExist = minioClient.bucketExists(userbucket);
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket(userbucket);
                logger.info("???????????????");
                System.out.println("???????????????");
                minioClient.setBucketPolicy(userbucket, builder.toString());
                System.out.println("?????????????????????");
                logger.info("?????????????????????");
            }

            // Upload the zip file to the bucket with putObject
            minioClient.putObject(userbucket, fileRoot + fileName, inputStream, inputStream.available(), contentType);
            System.out.println("successfully uploaded as " + fileName + " to `asiatrip` bucket.");

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param userbucket (??????)
     * @param fileName (?????????)
     * @param inputStream
     * fileUploader(????????????????????????)
     */
    public static void fileUploader(String userbucket, String fileName, InputStream inputStream) {
        try {
            Uploaders(userbucket, rootpath, fileName, inputStream, "application/octet-stream");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * ??????tender????????????
     * @param userbucket
     * @param fileName
     * @param inputStream
     */
    public static void tenderUploader(String userbucket, String fileName, InputStream inputStream) {
        try {
            Uploaders(userbucket, "tender/", fileName, inputStream, "application/octet-stream");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     * @param userbucket
     * @param fileName ?????????
     * @param inputStream
     */
    public static void imgUploader(String userbucket, String fileName, InputStream inputStream) {
        try {
            Uploaders(userbucket, imgRootpath, fileName, inputStream, "image/jpeg");
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
    
    public static void deleteObject(String bucketName, String objectName) throws InvalidEndpointException, InvalidPortException, InvalidKeyException, InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException, InvalidResponseException, IOException, XmlPullParserException {
        if (minioClient == null) {
            minioClient = new MinioClient(miniopath, minioname, miniopass);
        }
//        minioClient = new MinioClient(miniopath, minioname, miniopass);
    	minioClient.removeObject(bucketName, objectName);
    }

    /**
     * ??????????????? url
     * @param bucketName ???????????????
     * @param objectName
     * @return
     */
    public static String getFileUrl(String bucketName, String objectName) {
        try {
//            minioClient = new MinioClient(miniopath, minioname, miniopass);
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
            return (minioClient.presignedGetObject(bucketName, rootpath + objectName));
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
        return "error";
    }

    /**
     * ???????????????url
     * @param bucketName
     * @param objectName
     * @return
     */
    public static String getImgUrl(String bucketName, String objectName) {
        InputStream inputStream=null;
        try {
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
//            minioClient = new MinioClient(miniopath, minioname, miniopass);

            //???????????????????????? start
//            inputStream=minioClient.getObject(bucketName,imgRootpath+objectName);
            //end

            String presignedGetObject = minioClient.presignedGetObject(bucketName, imgRootpath + objectName);
            String imgurl = presignedGetObject.substring(0, presignedGetObject.indexOf("?"));
            imgurl = imgurl.replaceAll(miniopath, "");
            logger.info("????????????url??????" + imgurl);
            return "/minio" + imgurl;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return "error";
    }

    /**
     * ????????????????????????
     * @param bucketName ??????
     * @param objectRoot
     * @param objectName ?????????
     * @return
     */
    public static InputStream getFile(String bucketName, String objectRoot, String objectName) {
        return Downloader(bucketName, objectRoot, objectName);
    }

    /**
     * ????????????????????????
     * @param bucketName
     * @param objectName ?????????
     * @return
     */
    public static InputStream getImg(String bucketName, String objectName) {
        return Downloader(bucketName, imgRootpath, objectName);
    }

    /**
     * @return
     * bucketList (????????????????????????????????????)
     */
    public static String bucketList() {
        try {
            // List buckets that have read access.
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
//            minioClient = new MinioClient(miniopath, minioname, miniopass);
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                System.out.println(bucket.creationDate() + ", " + bucket.name());
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
        return "error";
    }

    /**
     * ??????????????????????????????
     * @param bucketName ??????
     * @param fileRoot
     * @param objectName ???????????????
     * @return
     */
    public static InputStream Downloader(String bucketName, String fileRoot, String objectName) {
        try {
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
//            minioClient = new MinioClient(miniopath, minioname, miniopass);
            return minioClient.getObject(bucketName, fileRoot + objectName);
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
        return null;
    }

    /**
     * ?????????????????????????????????
     *
     * @param bucketName
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Iterable<Result<Item>> listFile(String bucketName)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException {
        try {
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
//            minioClient = new MinioClient(miniopath, minioname, miniopass);
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                Iterable<Result<Item>> myObjects = minioClient.listObjects(bucketName);
                return myObjects;

            } else {
                System.out.println(bucketName + " does not exist");
            }

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }

        return null;
    }

    /**
     * Downloader(????????????????????????)
     * @param bucketName ?????????????????????
     * @param objectName
     * @param fileName
     */
    public static void localDownloader(String bucketName, String objectName, String fileName) {
        try {
            if (minioClient == null) {
                minioClient = new MinioClient(miniopath, minioname, miniopass);
            }
//            minioClient = new MinioClient(miniopath, minioname, miniopass);
            minioClient.getObject(bucketName, rootpath + objectName, fileName);
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }

    /**
     * ?????????
     * @param bucketName
     */
    public static void removeBucket(String bucketName) {
        try {
            // Check if my-bucket exists before removing it.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // Remove bucket my-bucketname. This operation will succeed only if the bucket
                // is empty.
                minioClient.removeBucket(bucketName);
                System.out.println(bucketName + " is removed successfully");
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }

    public static void main(String[] args)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException,
            InvalidBucketNameException, InsufficientDataException, NoResponseException, ErrorResponseException,
            InternalException, InvalidArgumentException, InvalidResponseException, TransformerConfigurationException {
        // System.out.println(bucketList());
        // Downloader("admins","0afc2fe55b1b4da88e69f2bd593a431a-09.
        // ?????????????????????-??????????????????-??????-?????????1.doc","F://a.doc");
        // FileInputStream file = new FileInputStream(new File("F:\\ii.png"));
        // imgUploader("1231","?????????",file);
        // System.out.println(getImgUrl("admins","?????????"));
        // minioClient.putObject("admin", "1b6f6228c14d4385b287953c74ab1f41-temp
        // (4).doc",
        // "C:\\Users\\caiwei\\Downloads\\1b6f6228c14d4385b287953c74ab1f41-temp
        // (4).doc");
//		InputStream inputStream = minioClient.getObject("admin", "1b6f6228c14d4385b287953c74ab1f41-temp (4).doc");
//		String textString=ReadFileContext.readFileToHtml(inputStream);
//		System.out.println(textString);
//		byte[] buf = new byte[16384];
//	      int bytesRead;
//	      while ((bytesRead = inputStream.read(buf, 0, buf.length)) >= 0) {
//	        System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
//	      }
//
//	      // Close the input stream.
//	      inputStream.close();

        // System.out.println(MinioClientUtils.getImgUrl("img",
        // "9955a461-f6c2-4035-ae7b-9edb2efa97a9-???????????????.jpg"));
//		InputStream inputStream =MinioClientUtils.getFile("webcontroller", "file/","40288ba86d62d0a6016d6b42a28a6e48-?????????????????????????????????????????????.pdf");
//		String textString=ReadFileContext.readFileToHtml(inputStream);
//		System.out.println(textString);
//		System.out
//				.println(minioClient.getObjectUrl("1245623", "img/3987433d215f4e76a76cd28d1dde8a3f-1567051282(1).jpg"));
//
//		minioClient.getObject("1245623", "img/3987433d215f4e76a76cd28d1dde8a3f-1567051282(1).jpg",
//				"3987433d215f4e76a76cd28d1dde8a3f-1567051282(1).jpg");
//		File file =  new File("C:\\Users\\caiwei\\Desktop\\t20191021_13152905-????????????????????????????????????????????????-?????????.rar");
//		FileInputStream fileInputStream = new FileInputStream(file);
//		fileUploader("13911002299",file.getName(),fileInputStream);
//		FileInputStream file = new FileInputStream(new File("F://f448949d-7cef-4018-acb6-1838ed081467-?????????????????????.png"));
//		imgUploader("minio-img","f448949d-7cef-4018-acb6-1838ed081467-?????????????????????.png",file);

        // System.out.println(getImgUrl("minio-img","f448949d-7cef-4018-acb6-1838ed081467-?????????????????????.png"));

        InputStream inputStream = MinioClientUtils.getFile("article", "file/??????", "https??????????????????.docx");
        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = sb.toString();
        System.out.println(context);

    }

}

