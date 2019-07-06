package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.MD5Util;
import com.xuecheng.manage_media.config.RabbitMqConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {
    @Resource
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    String uploadFolder;
    @Autowired
    RabbitTemplate rabbitTemplate;
@Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkey_media_video;
    //文件上传之前的注册
    public ResponseResult register(String fileMd5, String fileName, long fileSize, String mimeType, String fileExt) {

        //得到文件所属路径
        String fileFolderPath = getFileFolderPath(fileMd5);
        //文件路径
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        boolean exists = file.exists();

        //检查MongoDB中文件是否存在
        Optional<MediaFile> byId = mediaFileRepository.findById(fileMd5);
        if (exists && byId.isPresent()) {
            //文件存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //文件不存在的话,检查文件目录是否存在如果不存在就创建
        File file1 = new File(fileFolderPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //检查块文件是否存在
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        File file = new File(getChunkFileFolderPath(fileMd5) + chunk);
        if (file.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);

        } else {
            return new CheckChunkResult(CommonCode.SUCCESS, false);
        }


    }


    //上传文件
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) throws IOException {
        File file1 = new File(getChunkFileFolderPath(fileMd5));

        if (!file1.exists()) {
            file1.mkdirs();
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = file.getInputStream();
            File file2 = new File(getChunkFileFolderPath(fileMd5) + chunk);
            fileOutputStream = new FileOutputStream(file2);
            file2.createNewFile();
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            fileOutputStream.close();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }


    //合并分块并存入MongoDB
    public ResponseResult mergeChunks(String fileMd5, String fileName, long fileSize, String mimeType, String fileExt) throws IOException {
        //合并所有分块

        File merge = merge(fileMd5, fileExt);
        //如果合并后文件为空,说明合并出错
        if (merge == null) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //md5校验
        String fileMD5String = MD5Util.getFileMD5String(merge);
        if (!fileMD5String.equalsIgnoreCase(fileMd5)) {
            return new ResponseResult(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //存入mongodb
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        //文件路径保存相对路径
        String filePath1 = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(filePath1);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimeType);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理消息

        return sendProcessVideoMsg(mediaFile.getFileId());
    }

    public ResponseResult sendProcessVideoMsg(String mediaId) {
        HashMap<Object, Object> msg = new HashMap<>();
        msg.put("mediaId", mediaId);
        String s = JSON.toJSONString(msg);
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EX_MEDIA_PROCESSTASK, routingkey_media_video, s);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并所有分块
    private File merge(String fileMd5, String fileExt) throws IOException {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        File file = new File(chunkFileFolderPath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            }
        });
        //合并之后目标文件
        File file2 = new File(getFilePath(fileMd5, fileExt));
        boolean newFile = file2.createNewFile();
        //目标文件的写入流
        byte[] bytes = new byte[1024];
        RandomAccessFile rw = new RandomAccessFile(file2, "rw");
        for (File file1 : files) {
            RandomAccessFile r = new RandomAccessFile(file1, "r");
            int len = -1;
            while ((len = r.read(bytes)) != -1) {
                rw.write(bytes, 0, len);
            }
            r.close();
        }
        rw.close();
        return file2;
    }


    //根据md5码获得文件存储路径
    private String getFileFolderPath(String fileMd5) {
        return uploadFolder + fileMd5.toCharArray()[0] + "/" + fileMd5.toCharArray()[1] + "/" + fileMd5 + "/";
    }

    //根据md5和扩展名获得文件路径
    private String getFilePath(String fileMd5, String fileExt) {
        return uploadFolder + fileMd5.toCharArray()[0] + "/" + fileMd5.toCharArray()[1] + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }

    //根据md5码获得块文件存储路径

    private String getChunkFileFolderPath(String fileMd5) {
        return uploadFolder + fileMd5.toCharArray()[0] + "/" + fileMd5.toCharArray()[1] + "/" + fileMd5 + "/chunk/";
    }

}


