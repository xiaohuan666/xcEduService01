package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachPlanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


public interface EsCourseService {
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;

    Map<String, CoursePub> getall(String courseId) throws IOException, InvocationTargetException, IllegalAccessException;

    TeachPlanMediaPub getmedia(String teachplanId) throws IOException;
}
