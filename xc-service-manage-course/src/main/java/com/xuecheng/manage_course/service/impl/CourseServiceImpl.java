package com.xuecheng.manage_course.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublichResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Value("${course-publish.siteId}")
    private String siteId;
    @Value("${course-publish.templateId}")
    private String templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;
    @Value("${course-publish.pageWebPath}")
    private String pageWebPath;
    @Value("${course-publish.pagePhysicalPath}")
    private String pagePhysicalPath;
    @Value("${course-publish.dataUrlPre}")
    private String dataUrlPre;

    @Override
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanList = teachplanMapper.findTeachplanList(courseId);
        System.out.println(teachplanList);
        return teachplanMapper.findTeachplanList(courseId);
    }

    @Override
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan.getParentid().isEmpty()) {
            teachplan.setParentid("1");
            teachplan.setGrade("2");
        } else {
            teachplan.setGrade("3");
        }
        try {
            Teachplan save = teachplanRepository.save(teachplan);
        } catch (Exception e) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public QueryResult findCourseList(String companyId, int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        //企业id
        courseListRequest.setCompanyId(companyId);
        //将companyId传给dao
//        courseListRequest.setCompanyId(companyId);
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return courseIncfoQueryResult;
    }

    /*@Override
    public CoursePageResult findCourseList(int page, int size) {

        PageHelper.startPage(page, size);
        Page<CourseBase> courseList = courseMapper.findCourseList();
        PageInfo<CourseBase> courseBasePageInfo = courseList.toPageInfo();

        return new CoursePageResult(CommonCode.SUCCESS, courseBasePageInfo);
    }*/

    @Override
    public ResponseResult addCourseBase(CourseBase courseBase) {
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    @Override
    public CourseBase findCourseById(String courseid) {
        Optional<CourseBase> byId = courseBaseRepository.findById(courseid);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
            return null;
        }

    }

    @Override
    public AddCourseResult updateCoursebase(String courseid, CourseBase courseBase) {

           /* Optional<CourseBase> byId = courseBaseRepository.findById(courseid);
            CourseBase courseBase1 = byId.get();
            System.out.println(courseBase.getGrade());
            courseBase.setId(courseid);
            courseBase1.setName(courseBase.getName());
            courseBase1.setUsers(courseBase.getUsers());
            courseBase1.setMt(courseBase.getMt());
            courseBase1.setSt(courseBase.getSt());
            courseBase1.setGrade(courseBase.getGrade());
            courseBase1.setStudymodel(courseBase.getStudymodel());
            courseBase1.setDescription(courseBase.getDescription());
            CourseBase save = courseBaseRepository.save(courseBase1);*/
        int i = 0;
        try {
            i = courseMapper.updateCoursebase(courseBase);
        } catch (Exception e) {
            e.printStackTrace();
            return new AddCourseResult(CourseCode.COURSE_PUBLISH_CDETAILERROR, null);
        }
        if (i == 1) {

            return new AddCourseResult(CommonCode.SUCCESS, courseid);
        } else {
            return new AddCourseResult(CourseCode.COURSE_PUBLISH_CDETAILERROR, null);
        }


    }

    //查询课程视图,包括基本信息,图片,营销信息,课程计划
    @Override
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(coursePic);
        }
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        TeachplanNode teachplanList = teachplanMapper.findTeachplanList(id);
        courseView.setTeachplanNode(teachplanList);


        return courseView;
    }


    @Override
    public CoursePublichResult coursePreview(String courseId) {
        //拼接一个cmspage存入数据库.返回pageid
        String pageId = saveCmsPageAndReturnPageId(courseId);
        String url = previewUrl + pageId;
        CoursePublichResult coursePublichResult = new CoursePublichResult(CommonCode.SUCCESS, url);

//        cmsPageClient.save()
        return coursePublichResult;
    }

    //课程发布
    @Transactional
    @Override
    public CoursePublichResult coursePublish(String courseId) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }
        CourseBase courseBase = courseBaseOptional.get();
        String name = courseBase.getName();
        CmsPage cmsPage = new CmsPage();
//        给cmapage赋值
        cmsPage.setDataUrl(dataUrlPre + courseId);
        cmsPage.setSiteId(siteId);
        cmsPage.setPageName(courseId + ".html");
        cmsPage.setPageAliase(name);
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        cmsPage.setPageWebPath(pageWebPath);
        cmsPage.setTemplateId(templateId);


//        调用cms发布页面接口
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublichResult(CommonCode.FAIL, null);
        }

        //保存课程的发布状态为“已发布”
        CourseBase courseBase1 = this.saveCoursePubState(courseId);
        if (courseBase1 == null) {

            return new CoursePublichResult(CommonCode.FAIL, null);
        }

        //得到页面url
        String pageUrl = cmsPostPageResult.getPageUrl();

        //将媒资信息保存到teachPlanMediaPub
        saveTeachplanMediaPub(courseId);

        return new CoursePublichResult(CommonCode.SUCCESS, pageUrl);

/*
        CmsPageResult cmsPageResult = cmsPageClient.postPage(pageId);
        CourseBase courseBaseById = courseMapper.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseMapper.changeStatusById(courseBaseById);



        String url = previewUrl + pageId;
        return ;*/
    }


    @Override
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {

        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是3级目录
//        查询课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan = optional.get();
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !"3".equals(grade)) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIS_TEACHPLAN_GRADERROR);
        }
        Optional<TeachplanMedia> optional1 = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (!optional1.isPresent()) {
            one = new TeachplanMedia();
        } else {
            one = optional1.get();
        }

        one.setCourseId(teachplan.getCourseid());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);


        TeachplanMedia save = teachplanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    //
    private void saveTeachplanMediaPub(String courseId) {
//
        teachplanMediaPubRepository.deleteByCourseId(courseId);
//        将teachplanmedia表对应数据copy到pub表,加上时间戳
        List<TeachplanMedia> teachplanMedias = teachplanMediaRepository.findByCourseId(courseId);
        List<TeachPlanMediaPub> teachPlanMediaPubs = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMedias) {
            TeachPlanMediaPub teachPlanMediaPub = new TeachPlanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachPlanMediaPub);
            teachPlanMediaPub.setTimestamp(new Date());
            teachPlanMediaPubs.add(teachPlanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachPlanMediaPubs);
    }


    //更新课程状态为已发布 202002
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBaseById = this.findCourseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }


    //拼接一个cmspage存入数据库.返回pageid
    private String saveCmsPageAndReturnPageId(String courseId) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }
        CourseBase courseBase = courseBaseOptional.get();
        String name = courseBase.getName();
        CmsPage cmsPage = new CmsPage();
//        给cmapage赋值
        cmsPage.setDataUrl(dataUrlPre + courseId);
        cmsPage.setSiteId(siteId);
        cmsPage.setPageName(courseId + ".html");
        cmsPage.setPageAliase(name);
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        cmsPage.setPageWebPath(pageWebPath);
        cmsPage.setTemplateId(templateId);
//        远程调用cms接口,存入cmspage
        CmsPageResult cmsPageResult = null;
        try {
            cmsPageResult = cmsPageClient.save(cmsPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!cmsPageResult.isSuccess()) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        return pageId;

    }
}
