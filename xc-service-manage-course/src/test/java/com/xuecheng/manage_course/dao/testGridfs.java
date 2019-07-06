package com.xuecheng.manage_course.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.xuecheng.manage_course.ManageCourseApplication;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@SpringBootTest(classes = ManageCourseApplication.class)
@RunWith(SpringRunner.class)
public class testGridfs {
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Test
    public void test1() throws FileNotFoundException {
        File file = new File("C:/Users/Administrator/Pictures/Saved Pictures/timg.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(fileInputStream, "heiqi.jpg");
        System.out.println(store);
    }
    @Test
    public void  test2() throws IOException {
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream("heiqi.jpg");
        int copy = IOUtils.copy(gridFSDownloadStream, new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\heiqi.jpg")));
        System.out.println(copy);

    }

}
