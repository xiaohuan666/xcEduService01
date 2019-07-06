package com.xuecheng.manage_media_process.mq;


import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {
    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.video-location}")
    String video_location;

    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;

    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",
            containerFactory = "containerFactory")
    public void reciveMediaProcessTask(String msg) {
        //解析消息内容得到mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");

        Optional<MediaFile> byId = mediaFileRepository.findById(mediaId);
        //如果不存在
        if (!byId.isPresent()) {
            return;
        }

        MediaFile mediaFile = byId.get();
        String fileType = mediaFile.getFileType();
        if (!"avi".equalsIgnoreCase(fileType)) {
            mediaFile.setProcessStatus("303004");
            mediaFileRepository.save(mediaFile);
            return;
        } else {
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }
        //使用工具类将avi转换成MP4
        String video_path = video_location + mediaFile.getFilePath() + mediaFile.getFileName();
//        String x =mediaFile.getFileName();
//        String mp4_name = mediaFile.getFileName().split(".")[0] + ".mp4";
        String mp4_name = mediaFile.getFileId() + ".mp4";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, video_location + mediaFile.getFilePath());
        String s = mp4VideoUtil.generateMp4();
        if (s == null || !"success".equals(s)) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            mediaFileRepository.save(mediaFile);
            return;
        }
        //使用工具类将MP3转换成m3u8

        String video_path1 = video_location + mediaFile.getFilePath() + mp4_name;
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        String m3u8_path = video_location + mediaFile.getFilePath() + "/hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, video_path1, m3u8_name, m3u8_path);

        String resultTs = hlsVideoUtil.generateM3u8();
        if (resultTs == null || !"success".equals(resultTs)) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            mediaFileRepository.save(mediaFile);
            return;
        }
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        mediaFile.setProcessStatus("303002");
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        String fileUrl = mediaFile.getFilePath() + "hls/" + m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        mediaFileRepository.save(mediaFile);
    }
}
