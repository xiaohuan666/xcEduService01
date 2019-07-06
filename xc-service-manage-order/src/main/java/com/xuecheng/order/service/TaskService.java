package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRespository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRespository xcTaskHisRespository;

    @Transactional
    public List<XcTask> findXcTaskList(Date updateTime,int size){
        Pageable pageable = PageRequest.of(0,size);
        Page<XcTask> byUpdateTimeBefore = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        List<XcTask> list = byUpdateTimeBefore.getContent();

        return list;
    }

    @Transactional
    public void publish(XcTask xcTask,String exchange,String routingKey){
        Optional<XcTask> byId = xcTaskRepository.findById(xcTask.getId());
        if (byId.isPresent()){
            rabbitTemplate.convertAndSend(exchange,routingKey,xcTask);
            XcTask one = byId.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    @Transactional
    public int getTask(String id ,int version){
        return xcTaskRepository.updateTaskVersion(id,version);
    }

    @Transactional
    public void finishTask(XcTask xcTask) {

        //向历史记录中添加xc_task_his
        XcTaskHis xcTaskHis = new XcTaskHis();
        BeanUtils.copyProperties(xcTask,xcTaskHis);
        xcTaskHisRespository.save(xcTaskHis);

        //将任务记录删除xc_task
        xcTaskRepository.deleteById(xcTask.getId());

    }
}
