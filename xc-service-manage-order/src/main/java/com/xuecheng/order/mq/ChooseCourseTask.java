package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//选课
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    @Scheduled(cron = "0/3 * * * * *")
    public void sendChooseTask() {
        GregorianCalendar calendar = new GregorianCalendar();
        Date time1 = calendar.getTime();
        System.out.println(time1);
        int i = calendar.get(GregorianCalendar.MINUTE)-1;
        calendar.set(GregorianCalendar.MINUTE,i);
        Date time = calendar.getTime();
        System.out.println(time);

        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);


        //调用service发布消息,将选课任务发送给mq
        for (XcTask xcTask : xcTaskList) {
            if (taskService.getTask(xcTask.getId(),xcTask.getVersion())>0) {
                String mqExchange = xcTask.getMqExchange();//取出交换机
                String mqRoutingkey = xcTask.getMqRoutingkey();//取出routingkey
                taskService.publish(xcTask, mqExchange, mqRoutingkey);
            }
        }

    }

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public  void finishAddChooseCourse(XcTask xcTask){
        if (xcTask!=null&& StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask);

        }

    }

}
