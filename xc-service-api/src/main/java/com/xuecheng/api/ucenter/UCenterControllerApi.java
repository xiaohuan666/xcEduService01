package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户中心")
public interface UCenterControllerApi {

    @ApiOperation("根据用户账号从数据库查询用户对象")
    XcUserExt getUserExt(String username);
}
