package com.mmall.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FtpUtil {

    private static String IP = PropertiesUtil.getProperties("ftp.server.ip");
    private static Integer PORT = 21;
    private static String USER = PropertiesUtil.getProperties("ftp.user");
    private static String PASSWORD = PropertiesUtil.getProperties("ftp.pass");
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    private FTPClient ftpClient = new FTPClient();

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FtpUtil ftpUtil = new FtpUtil();
        logger.info("开始上传文件到FTP服务器");
        boolean res = ftpUtil.uploadFile(fileList, "image");
        logger.info("上传文件的结果为:{}", res);
        return res;
    }

    private boolean uploadFile(List<File> fileList, String path) throws IOException {
        FileInputStream inputStream = null;
        if (connect2Server(IP, USER, PASSWORD)) {
            try {
                ftpClient.changeWorkingDirectory(path);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    inputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), inputStream);
                }

            } catch(Exception e) {
                logger.error("上传文件发生错误", e);
                return false;
            } finally {
                inputStream.close();
                ftpClient.disconnect();
            }
        } else {
            logger.error("登录FTP服务器发生错误");
            return false;
        }
        return true;
    }

    private boolean connect2Server(String ip, String user, String password) {
        boolean isSuccess = false;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, password);
        } catch (Exception e) {
            logger.error("登录FTP服务器发生错误", e);
        }
        return isSuccess;
    }
}
