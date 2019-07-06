package com.xuecheng.search;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
public class TestSearch {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    @Test
    public void testSearchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchSourceBuilder.fetchSource(new String[]{"name", "studymodle", "price", "description"}, new String[]{"timesamp"});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, DEFAULT);
//        搜索结果
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            String id = hit.getId();
//            Map<String, Object> fields = hit.getSourceAsMap();
//            String name = (String) fields.get("name");
//            String description = (String) fields.get("description");
//            System.out.println(name);
//            System.out.println(description);
            String fields = hit.getSourceAsString();
            System.out.println(fields);

        }
    }

    @Test
    public void testSearchPage() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "开发"));
        int page = 1;
        int size = 1;
        searchSourceBuilder.from((page - 1) * size);
        searchSourceBuilder.size(size);
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodle", "price", "description"}, new String[]{"timesamp"});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, DEFAULT);
//        搜索结果
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            String id = hit.getId();
//            Map<String, Object> fields = hit.getSourceAsMap();
//            String name = (String) fields.get("name");
//            String description = (String) fields.get("description");
//            System.out.println(name);
//            System.out.println(description);
            String fields = hit.getSourceAsString();
            System.out.println(fields);

        }
    }


    @Test
    public void testSearchbool() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery();
        idsQueryBuilder.addIds("1", "3");
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description");
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "css");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.should(multiMatchQueryBuilder);
        boolQueryBuilder.filter(idsQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("price", SortOrder.DESC);
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodle", "price", "description"}, new String[]{"timesamp"});

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));

        searchSourceBuilder.highlighter(highlightBuilder);

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, DEFAULT);
//        搜索结果
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit hit : hits1) {
            String name = null;
            String id = hit.getId();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameHighlightField = highlightFields.get("name");
                if (nameHighlightField != null) {
                    Text[] fragments = nameHighlightField.getFragments();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Text fragment : fragments) {
                        stringBuilder.append(fragment);

                    }
                    name = stringBuilder.toString();

                }
            }
//            Map<String, Object> fields = hit.getSourceAsMap();
//            String name = (String) fields.get("name");
//            String description = (String) fields.get("description");
//            System.out.println(name);
//            System.out.println(description);
            System.out.println(name);
            String fields = hit.getSourceAsString();
            System.out.println(fields);

        }
    }
}
