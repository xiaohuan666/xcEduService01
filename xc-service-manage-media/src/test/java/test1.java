import com.xuecheng.manage_media.ManageMediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

@SpringBootTest(classes = ManageMediaApplication.class)
@RunWith(SpringRunner.class)
public class test1 {
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourcefile = new File("D:/develop/video/lucene.avi");
        //块文件目录
        String chunkFileFolder = "D:/develop/video/chunks/";
        //块文件大小
        long chunkFileSize = (1*1024*1024);
        //切成多少块
        long chunkNum = (long)Math.ceil(sourcefile.length()*1.0/chunkFileSize);


        byte[] bytes = new byte[1024];
        RandomAccessFile r= new RandomAccessFile(sourcefile, "r");
        for (int i = 0; i < chunkNum; i++) {
            //块文件
            File file = new File(chunkFileFolder + "_" + i);
            boolean newFile = file.createNewFile();
            int len = -1;
            RandomAccessFile rw = new RandomAccessFile(file, "rw");
            while ((len = r.read(bytes))!=-1){

                rw.write(bytes,0,len);

                if (file.length()>chunkFileSize){
                    break ;
                }

            }
            rw.close();
        }
        r.close();
    }

    @Test
    public void testMerge() throws IOException {
        //块文件目录
        String chunkFileFolder = "D:/develop/video/chunks/";
        File fileFolder = new File(chunkFileFolder);
        File[] files = fileFolder.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName().split("_")[1])>Integer.parseInt(o2.getName().split("_")[1])){

                    return 1;
                }else {
                    return -1;
                }
            }
        });
        File file1 = new File("D:/develop/video/chunk_merge.avi");
        boolean newFile = file1.createNewFile();
        RandomAccessFile rw = new RandomAccessFile(file1, "rw");
        for (File file : files) {
            RandomAccessFile r = new RandomAccessFile(file, "r");
            int len = -1 ;
            byte[] bytes = new byte[1024];
            while ((len = r.read(bytes))!=-1){
                rw.write(bytes);
            }
            r.close();

        }
        rw.close();
    }
}
