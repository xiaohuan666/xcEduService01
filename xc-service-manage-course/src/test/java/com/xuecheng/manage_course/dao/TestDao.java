package com.xuecheng.manage_course.dao;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.xuecheng.framework.domain.course.Category;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.ManageCourseApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest(classes = ManageCourseApplication.class)
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);

    }

    @Test
    public void testFindTeachplanList(){
        TeachplanNode teachplanList = teachplanMapper.findTeachplanList("4028e581617f945f01617f9dabc40000");
        String s = JSON.toJSONString(teachplanList);
//        String s1 = teachplanList.toString();
        System.out.println(s);

    }
    @Test
    public void testAddTeachplan(){

        Teachplan teachplan = new Teachplan();
        teachplan.setPname("测试001");
        teachplan.setGrade("5");
        teachplan.setParentid("1");
        teachplan.setStatus("0");
        Teachplan save = teachplanRepository.save(teachplan);


    }
    @Test
    public void  testIsEmpty(){
        String s = "1";
        System.out.println(s.isEmpty());
    }
    @Test
    public void testUUID(){
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        String[] split = s.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            sb.append(split[i]);
        }
        System.out.println(sb);
    }

    @Test
    public void testPageHelper(){
        PageHelper.startPage(1,5);
        Page<CourseBase> courseList = courseMapper.findCourseList();
        PageInfo<CourseBase> courseBasePageInfo = courseList.toPageInfo();
        String s = JSON.toJSONString(courseBasePageInfo);
        System.out.println(s);
    }

    @Test
    public  void  testCatrgory(){
        List<Category> all = categoryRepository.findAll();
        System.out.println(all);
    }
    @Test
    public void testCategory(){
        CategoryNode all = categoryMapper.findAll();
        String s = JSON.toJSONString(all);
        System.out.println(s);

    }

    @Autowired
    SystemDictionaryRepository systemDictionaryRepository;
    @Test
    public void  testSyetemDictionaryRepository(){
        SysDictionary byDType = systemDictionaryRepository.findByDType("200");
        System.out.println(byDType);
    }
}
