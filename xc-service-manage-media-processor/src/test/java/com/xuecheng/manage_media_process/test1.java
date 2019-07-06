package com.xuecheng.manage_media_process;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class test1 {
    public static void main(String[] args) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        ProcessBuilder ping = processBuilder.command("ping", "127.0.0.1");
        Process start = ping.start();

        InputStream inputStream = start.getInputStream();
//        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String s = IOUtils.toString(inputStream,"gbk");
        System.out.println(s);
    }

    @Test
    public void test(){
        String s = "5fbb79a2016c0eb609ecd0cd3dc48016.avi";
        String[] split = s.split("\\.");

        String s1 = split[1];
        System.out.println(s1);
    }

}
