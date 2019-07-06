
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.gridfs.GridFS;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.ManageCmsClientApplication;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@SpringBootTest(classes = ManageCmsClientApplication.class)
@RunWith(SpringRunner.class)
public class hehe {
    @Value("${xuecheng.mq.queue}")
    public String queue;
@Autowired
    CmsPageRepository cmsPageRepository;
@Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void test(){

        System.out.println(queue);
    }

    @Test
    public void test1() throws IOException {

        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(new ObjectId("5a94d6c6b00ffc3ab4bfa4f4"));
        String s = IOUtils.toString(gridFSDownloadStream, "utf-8");
        System.out.println(s);


    }


}
