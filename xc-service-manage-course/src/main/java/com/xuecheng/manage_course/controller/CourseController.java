package com.xuecheng.manage_course.controller;


import com.alibaba.fastjson.JSON;
import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublichResult;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;

    @PreAuthorize("hasAuthority('course_find_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    @ResponseBody
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {

        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authorization = request.getHeader("Authorization");
        System.out.println(authorization);

        return courseService.findTeachplanList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    @ResponseBody
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        System.out.println(teachplan);
        return courseService.addTeachplan(teachplan);
    }

//    @PreAuthorize("hasAuthority('course_find_list')")
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    @ResponseBody
    public QueryResponseResult findCourseList(@PathVariable("page") int page,@PathVariable("size") int size, CourseListRequest courseListRequest){
//    public CoursePageResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size){
//        CoursePageResult courseList = courseService.findCourseList(page, size);
        //先使用静态数据

        /*XcOauth2Util.UserJwt userJwtFromHeader = xcOauth2Util.getUserJwtFromHeader(request);
        String companyId1 = userJwtFromHeader.getCompanyId();*/
        String authorization = request.getHeader("Authorization").substring(7);


        Jwt decode = JwtHelper.decode(authorization);
        String claims = decode.getClaims();

        XcUserExt xcUserExt = JSON.parseObject(claims, XcUserExt.class);
        String companyId = xcUserExt.getCompanyId();
//        String companyId = "1";
        return new QueryResponseResult(CommonCode.SUCCESS,courseService.findCourseList(companyId,page,size,courseListRequest));
    }

    @Override
    @PostMapping("/coursebase/add")
    @ResponseBody
    public ResponseResult addCourseBase(@RequestBody CourseBase courseBase) {
        System.out.println(courseBase);
        return courseService.addCourseBase(courseBase);
    }

    @ResponseBody
    @GetMapping("/findById/{courseid}")
    public  CourseBase findCourseById(@PathVariable("courseid") String courseid){
        return courseService.findCourseById(courseid);
    }


    @Override
    @ResponseBody
    @PostMapping("/updateCoursebase/{courseid}")
    public AddCourseResult updateCoursebase(@PathVariable("courseid") String courseid,@RequestBody CourseBase courseBase){

        AddCourseResult addCourseResult =   courseService.updateCoursebase(courseid,courseBase);
        return addCourseResult;
    }

    @Override
    @GetMapping("/courseview/{courseId}")
    @ResponseBody
    public CourseView courseview(@PathVariable("courseId") String id) {
        return courseService.getCourseView(id);
    }



    @Override
    @PostMapping("/coursePreview/{courseId}")
    @ResponseBody  //返回一个url点击可以查看预览视图
    public CoursePublichResult coursePreview(@PathVariable("courseId") String courseId){
        return courseService.coursePreview(courseId);
    }

//    课程发布
    @Override
    @PostMapping("/publish/{courseId}")
    @ResponseBody
    public CoursePublichResult coursePublish(@PathVariable("courseId") String courseId) {
       return courseService.coursePublish(courseId);
    }

    @Override
    @PostMapping("/savemedia")
    @ResponseBody
    public ResponseResult saveMedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.saveMedia(teachplanMedia);
    }

}
