package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface XcTaskRepository extends JpaRepository<XcTask, String> {

    /**
     * 根据更新日期分页查询定时任务发送消息
     *
     * @param pageable   分页参数
     * @param updateTime 数据库更新时间
     * @return 返回xcTask分页对象
     */
//    Page<XcTask> findByUpdateTimeBefore (Pageable pageable, Date updateTime);
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    /**
     * 更新updateTime
     * @param id 主键
     * @param updateTime 更新时间
     * @return  影响改变行数
     */
    @Modifying
    @Query("update XcTask x set x.updateTime = :updateTime where x.id=:id")
    public int updateTaskTime(@Param("id") String id,@Param("updateTime")  Date updateTime);

    /**
     * 更新乐观锁版本号version
     * @param id   主键
     * @param version   乐观锁
     * @return  影响改变行数
     */
    @Modifying
    @Query("update XcTask x set x.version = :version+1 where x.id=:id and x.version=:version")
    public int updateTaskVersion(@Param("id") String id,@Param("version") int version);

}
