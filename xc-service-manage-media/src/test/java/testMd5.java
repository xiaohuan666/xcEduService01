public class testMd5 {



    public static void main(String[] args) {
        String uploadFolder ="D:/develop/video/";
        String fileMd5 = "5586546123daeddsfg";
    String s =      uploadFolder+fileMd5.toCharArray()[0]+"/"+fileMd5.toCharArray()[1]+"/"+fileMd5+"/";
        System.out.println(s);
    }
}
