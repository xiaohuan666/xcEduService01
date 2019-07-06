package com.xuecheng.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachPlanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class EsCourseServiceImpl implements EsCourseService {
    @Value("${xuecheng.course.index}")
    String index;
    @Value("${xuecheng.course.media_index}")
    String mediaIndex;

    @Autowired
    RestHighLevelClient Client;
    @Value(("${xuecheng.course.source_field}"))
    String sourceField;

    @Override
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) throws IOException {
        if (courseSearchParam == null) {
            courseSearchParam = new CourseSearchParam();
        }
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过滤源字段
        String[] sourceFields = sourceField.split(",");
        searchSourceBuilder.fetchSource(sourceFields, new String[]{});
//        设置布尔查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {

            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "description", "name", "teachplan")

                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            TermQueryBuilder grade = QueryBuilders.termQuery("grade", courseSearchParam.getGrade());
            boolQueryBuilder.filter(grade);
        }

        searchSourceBuilder.query(boolQueryBuilder);
//        searchSourceBuilder.size(2);
//        searchSourceBuilder.from(0);
        searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub> queryResult = new QueryResult<>();
        ArrayList<CoursePub> list = new ArrayList<>();
        try {
            //执行搜索结果
            SearchResponse search = Client.search(searchRequest, RequestOptions.DEFAULT);
            //拿到结果对象
            SearchHits hits = search.getHits();
            //拿到匹配总记录数
            TotalHits totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits.value);


            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                CoursePub coursePub = new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                //取出name
                String name = (String) sourceAsMap.get("name");
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Float price = null;
                try {
                    if (sourceAsMap.get("price") != null) {
                        price = (Float) sourceAsMap.get("price");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);

                Float price_old = null;
                try {
                    if (sourceAsMap.get("price_old") != null) {
                        price_old = (Float) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        queryResult.setList(list);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);


        return queryResponseResult;
    }

    @Override
    public Map<String, CoursePub> getall(String courseId) throws IOException, InvocationTargetException, IllegalAccessException {

        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder id = QueryBuilders.termQuery("_id", courseId);

        searchSourceBuilder.query(id);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = Client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        CoursePub coursePub = new CoursePub();
        for (SearchHit hit : hits1) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String sourceAsString = hit.getSourceAsString();
            String s = JSON.toJSONString(sourceAsMap);
            coursePub = JSON.parseObject(s, coursePub.getClass());
//            BeanUtils.copyProperties(sourceAsMap,coursePub);

//           org.apache.commons.beanutils.BeanUtils.copyProperties(coursePub,sourceAsMap);
        }
        Map<String, CoursePub> map = new HashMap<>();
        map.put(courseId, coursePub);

        return map;
    }

    @Override
    public TeachPlanMediaPub getmedia(String teachplanId) throws IOException {
        SearchRequest searchRequest = new SearchRequest(mediaIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder id = QueryBuilders.termQuery("_id", teachplanId);

        searchSourceBuilder.query(id);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = Client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        TeachPlanMediaPub teachPlanMediaPub = new TeachPlanMediaPub();
        for (SearchHit hit : hits1) {
            String sourceAsString = hit.getSourceAsString();
            teachPlanMediaPub = JSON.parseObject(sourceAsString, TeachPlanMediaPub.class);
        }

        return teachPlanMediaPub;
    }
}
