package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(value = "媒资管理接口")
public interface MediaUploadControllerApi {

    //文件上传之前的准备工作,校验工作
    @ApiOperation("文件上传注册")
    ResponseResult register(String fileMd5,
                             String fileName,
                             long fileSize,
                             String mimeType,
                             String fileExt);

    @ApiOperation("校验分块是否存在")
    CheckChunkResult checkChunk(String fileMd5,
                                Integer chunk,
                                Integer chunkSize);
    @ApiOperation("上传文件")
    ResponseResult uploadChunk(MultipartFile file,
                               String fileMd5,
                               Integer chunk) throws IOException;

    @ApiOperation("合并分块")
    ResponseResult mergeChunks(String fileMd5,
                               String fileName,
                               long fileSize,
                               String mimeType,
                               String fileExt) throws IOException;
}
