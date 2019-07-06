package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginFilterTest extends ZuulFilter {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

/*
从cookie查询令牌是否存在,.不存在则拒绝访问
从header查询jwt令牌是否存在,不存在拒绝访问
从redis查询user_token是否存在,不存在拒绝访问
 */

    @Override
    public Object run() throws ZuulException {
        //拿到request对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();

//        查看请求头,没有就认为没登录
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            refuse(currentContext, response);
            return null;
        }

        //查cookie
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map != null && map.get("uid") == null){
            refuse(currentContext, response);
            return null;
        }
        //查redis
        String uid = map.get("uid");
//         = new StringRedisTemplate();
        String s = stringRedisTemplate.boundValueOps("user_token:" + uid).get();
//        stringRedisTemplate.boundValueOps("user_token:" + userToken).get()
        if (StringUtils.isEmpty(s)){
            refuse(currentContext, response);
            return null;
        }

        return null;
    }

    private void refuse(RequestContext currentContext, HttpServletResponse response) {
        //禁止访问其他内容
        currentContext.setSendZuulResponse(false);
        //设置状态码
        currentContext.setResponseStatusCode(200);
//            设置请求体
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String s = JSON.toJSONString(responseResult);
        currentContext.setResponseBody(s);
        //设置json作为返回值
        response.setContentType("application/json;charset=utf-8");
    }
}
