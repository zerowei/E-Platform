package com.mmall.service.Impl;

import com.mmall.service.FileService;
import com.mmall.utils.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service("FileService")
@Slf4j
public class FileServiceImpl implements FileService {

    public String uploadFile(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String res = UUID.randomUUID().toString() + extension;

        log.info("开始上传文件，原文件名：{}， 上传路径：{}， 新文件名：{}", fileName, path, res);
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File finalFile = new File(fileDir, res);

        try {
            file.transferTo(finalFile);
            FtpUtil.uploadFile(new ArrayList<>(Arrays.asList(finalFile)));
            finalFile.delete();
        } catch (IOException e) {
            log.error("上传文件到指定目录失败", e);
            return null;
        }
        return finalFile.getName();
    }
}
