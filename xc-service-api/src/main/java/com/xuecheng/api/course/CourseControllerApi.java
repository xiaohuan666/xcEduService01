package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublichResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Administrator.
 */

@Api(value = "课程管理接口", description = "课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    @ApiImplicitParam(name = "courseId", value = "课程id", paramType = "path")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);


    //    public CoursePageResult findCourseList(int page, int size);
    @ApiOperation("我的课程分页查询")
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("课程添加")
    public ResponseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("课程修改")
    public AddCourseResult updateCoursebase(String courseid, CourseBase courseBase);

    @ApiOperation("课程视图查询")
    CourseView courseview(String id);

    @ApiOperation("课程预览接口")
    public CoursePublichResult coursePreview(String courseId);

    @ApiOperation("课程发布接口")
    public ResponseResult coursePublish(String courseId);

    @ApiOperation("保存课程计划与媒资文件关联")
    ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
