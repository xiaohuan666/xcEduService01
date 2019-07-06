import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.ManageOrderApplication;
import com.xuecheng.order.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SpringBootTest(classes = ManageOrderApplication.class)
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    TaskService taskService;

    @Test
    public  void test(){
        List<XcTask> xcTaskList = taskService.findXcTaskList(new Date(), 100);
        System.out.println(xcTaskList);
    }


    @Test
    public void test1(){
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        System.out.println(gregorianCalendar);
        int i = gregorianCalendar.get(GregorianCalendar.MINUTE) - 1;
        gregorianCalendar.set(GregorianCalendar.MINUTE,i);
    }

}
