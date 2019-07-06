import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.UcenterApplication;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = UcenterApplication.class)
@RunWith(SpringRunner.class)
public class testMapper {
    @Autowired
    XcMenuMapper xcMenuMapper;
    @Autowired
    UserService userService;
    @Test
    public void test(){
        List<XcMenu> menuListByUserId = xcMenuMapper.findMenuListByUserId("49");
        for (XcMenu xcMenu : menuListByUserId) {
            System.out.println(xcMenu.getCode());
        }
    }

    @Test
    public void test1(){
        XcUserExt itcast = userService.getUserExt("itcast");
        System.out.println(itcast);
    }
}
