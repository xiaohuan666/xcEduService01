package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.springframework.stereotype.Repository;


@Repository
public interface TeachplanMapper {
    TeachplanNode findTeachplanList(String courseId);

    int addTeachplan(Teachplan teachplan);
}
