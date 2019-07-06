package com.xuecheng.auth.service;

import com.xuecheng.auth.UcenterAuthApplication;
import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SpringBootTest(classes = UcenterAuthApplication.class)
@RunWith(SpringRunner.class)
public class TestAuthService {
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testLogin() {

        ServiceInstance instance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = instance.getUri();
        String authUrl = uri + "/auth/oauth/token";

        //@Nullable T body, @Nullable MultiValueMap<String, String> headers
        HttpHeaders httpHeaders = new HttpHeaders();
        String headerVlaue = getHttpBasic("XcWebApp", "XcWebApp");
        httpHeaders.add("Authorization", headerVlaue);

       /* Map<String, String> body = new HashMap<>();
        body.put("grant_type","password");
        body.put("username","itcast");
        body.put("password","123");*/
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "password");
        body.add("username", "itcast");
        body.add("password", "123");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(body, httpHeaders);
//String url, HttpMethod method,@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables


        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

                if (response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401) {
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        Map bodymap = exchange.getBody();
        System.out.println(bodymap);


    }

    public String getHttpBasic(String clientId, String password) {
        String s = clientId + ":" + password;
        byte[] encode = Base64Utils.encode(s.getBytes());

        return "Basic " + new String(encode);

    }


}
