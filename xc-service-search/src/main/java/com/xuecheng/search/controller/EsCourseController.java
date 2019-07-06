package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachPlanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Controller
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;
    @Override
    @GetMapping("/list/{page}/{size}")
    @ResponseBody
    public QueryResponseResult list(@PathVariable("page") int page,@PathVariable("size") int size, CourseSearchParam courseSearchParam) throws IOException {


        return esCourseService.list(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{courseId}")
@ResponseBody
    public Map<String, CoursePub> getall(@PathVariable("courseId") String courseId) throws IOException, InvocationTargetException, IllegalAccessException {


        return esCourseService.getall(courseId);
    }

    @Override
    @GetMapping("/getmedia/{teachplanId}")
    @ResponseBody
    public TeachPlanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId) throws IOException {


        return esCourseService.getmedia(teachplanId);
    }
}
