package com.xuecheng.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;
    @Value("${auth.tokenValiditySeconds}")
    Long tokenValiditySeconds;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public AuthToken login(LoginRequest loginRequest, String clientId, String clientSecret) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        /*ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();*/

        ServiceInstance instance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = instance.getUri();
        String httpUrl = uri + "/auth/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        httpHeaders.add("Authorization", httpBasic);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(body, httpHeaders);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        /*(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                Class<T> responseType, Object... uriVariables)*/

        ResponseEntity<Map> exchange = restTemplate.exchange(httpUrl, HttpMethod.POST, httpEntity, Map.class);
         Map<String,String> bodyMap = exchange.getBody();
        if (bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null) {

            String s = bodyMap.get("error_description");
            if (s!=null&&s.contains("UserDetailsService returned null")){
                ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }
            if (s!=null&&s.contains("用户名或密码错误")){
                ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
            }

            return null;

        }


        AuthToken authToken = new AuthToken();
        authToken.setJwt_token((String) bodyMap.get("access_token"));
        authToken.setAccess_token((String) bodyMap.get("jti"));
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));

        //存储到redis
        boolean b = saveTokenToRedis(authToken);
        if (!b){
            ExceptionCast.cast(AuthCode.AUTH_SAVE_TOKEN_ERROR);
        }
        return authToken;


    }

    @Override
    public String getUserjwt(String cookie) {

        AuthToken tokenFromRedis = this.getTokenFromRedis(cookie);

        if (tokenFromRedis==null){
            return null;
        }

        String jwt_token = tokenFromRedis.getJwt_token();
        return jwt_token;
    }

    @Override
    public boolean logout(AuthToken token) {
        return this.delToken(token);
    }

    private AuthToken getTokenFromRedis(String userToken){

        //redisuser_token:772a010f-84cf-4c32-adcd-c1734f8467b3
        String s = null;
        try {
            s = stringRedisTemplate.boundValueOps("user_token:" + userToken).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return JSON.parseObject(s, AuthToken.class);
    }
    private boolean saveTokenToRedis(AuthToken authToken) {
        String key = "user_token:" + authToken.getAccess_token();
        String s = JSON.toJSONString(authToken);
        stringRedisTemplate.boundValueOps(key).set(s, Duration.ofSeconds(tokenValiditySeconds));
        Long expire = stringRedisTemplate.getExpire(key);
        return expire > 0;
    }

    //删除token
    private boolean delToken(AuthToken authToken) {
        String key = "user_token:" + authToken.getAccess_token();

        Boolean delete = stringRedisTemplate.delete(key);
        Long expire = stringRedisTemplate.getExpire(key);
        return expire < 0;
    }
    private String getHttpBasic(String clientId, String clientSecret) {
        String s = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(s.getBytes());

        return "Basic " + new String(encode);
    }
}
