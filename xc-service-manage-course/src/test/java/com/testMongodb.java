package com;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.manage_course.ManageCourseApplication;
import com.xuecheng.manage_course.dao.TeachplanMediaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
@SpringBootTest(classes = ManageCourseApplication.class)
@RunWith(SpringRunner.class)
public class testMongodb {
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Test
    public  void test1(){
        List<TeachplanMedia> byCourseId = teachplanMediaRepository.findByCourseId("4028e58161bd3b380161bd3bcd2f0000");
        for (TeachplanMedia teachplanMedia : byCourseId) {
            System.out.println(teachplanMedia);
        }
    }
}
