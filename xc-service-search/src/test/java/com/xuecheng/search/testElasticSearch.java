package com.xuecheng.search;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@SpringBootTest(classes = SearchApplication.class)
@RunWith(SpringRunner.class)
public class testElasticSearch {
 @Autowired
    RestHighLevelClient client;
 @Autowired
    RestClient restClient;

 @Test
    public void testCreateIndex() throws IOException {
     CreateIndexRequest xx_course = new CreateIndexRequest("x1_course");
     xx_course.settings(Settings.builder().put("number_of_shards", "1").put("number_of_replicas", "0"));
xx_course.mapping("_doc"," {\n" +
        " \t\"properties\": {\n" +
        "            \"studymodel\":{\n" +
        "             \"type\":\"keyword\"\n" +
        "           },\n" +
        "            \"name\":{\n" +
        "             \"type\":\"keyword\"\n" +
        "           },\n" +
        "           \"description\": {\n" +
        "              \"type\": \"text\",\n" +
        "              \"analyzer\":\"ik_max_word\",\n" +
        "              \"search_analyzer\":\"ik_smart\"\n" +
        "           },\n" +
        "           \"pic\":{\n" +
        "             \"type\":\"text\",\n" +
        "             \"index\":false\n" +
        "           }\n" +
        " \t}\n" +
        "}", XContentType.JSON);
     CreateIndexResponse createIndexResponse = client.indices().create(xx_course, DEFAULT);
     boolean acknowledged = createIndexResponse.isAcknowledged();

     System.out.println(acknowledged);


 }
 @Test
    public void testDeleIndex() throws IOException {
//     RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("http://127.0.0.1:9200")));
     DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("sx_course");
//     RequestOptions build = new RequestOptions.Builder().build();
     System.out.println(client);
//     RequestOptions requestOptions = new RequestOptions(new RequestOptions.Builder());
     AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
     boolean acknowledged = delete.isAcknowledged();
     System.out.println(acknowledged);

 }

    @Test
    public void testAddDoc() throws IOException {
        //文档内容
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);

        //创建索引创建对象
        IndexRequest indexRequest = new IndexRequest("x1_course","_doc");
        //文档内容
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse indexResponse = client.index(indexRequest,DEFAULT);
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);

    }
    @Test
    public  void getIndex() throws IOException {
        GetRequest x1_course = new GetRequest("x1_course", "2-jyd2sBc1BIofshQdT2");
        GetResponse documentFields = client.get(x1_course, DEFAULT);
        String sourceAsString = documentFields.getSourceAsString();
        System.out.println(sourceAsString);
    }
}
