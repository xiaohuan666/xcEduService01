package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachPlanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Api(value = "课程搜索接口",tags = "课程搜索")
public interface EsCourseControllerApi {
    //搜索课程信息
    @ApiOperation("课程综合搜索")
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;


    @ApiOperation("根据课程id搜索课程信息")
    Map<String, CoursePub> getall(String courseId) throws IOException, InvocationTargetException, IllegalAccessException;

    @ApiOperation("根据课程计划查询媒资信息")
    TeachPlanMediaPub getmedia(String teachplanId) throws IOException;
}
