package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublichResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseService {
    public TeachplanNode findTeachplanList(String courseId);

    public ResponseResult addTeachplan(Teachplan teachplan);

//    CoursePageResult findCourseList(int page, int size);

    ResponseResult addCourseBase(CourseBase courseBase);

    CourseBase findCourseById(String courseid);

    AddCourseResult updateCoursebase(String courseid, CourseBase courseBase);

    CourseView getCourseView(String id);



    CoursePublichResult coursePreview(String id);

    CoursePublichResult coursePublish(String courseId);

    ResponseResult saveMedia(TeachplanMedia teachplanMedia);

    QueryResult findCourseList(String companyId, int page, int size, CourseListRequest courseListRequest);
}
