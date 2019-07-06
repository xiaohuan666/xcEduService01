package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachPlanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachplanMediaPubRepository extends JpaRepository<TeachPlanMediaPub,String> {
    TeachPlanMediaPub findByCourseId(String courseId);
    void deleteByCourseId(String courseId);
}
