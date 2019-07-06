package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api("数据字典管理页面")
public interface SystemDictionaryControllerApi {
    @ApiOperation(value = "数据字典查询")
    @ApiImplicitParam(name = "dType",value = "数据类型",paramType = "path",required = true)
    SysDictionary getDictionary(String dType);
}
