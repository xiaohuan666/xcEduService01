package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {
    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Autowired
    AuthService authService;
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;


    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if (StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        AuthToken tokens = authService.login(loginRequest, clientId, clientSecret);
        /*if (responseEntity.getStatusCodeValue()==400||responseEntity.getStatusCodeValue()==401){
            return new LoginResult(AuthCode.AUTH_CREDENTIAL_ERROR,null);
        }
        Map body = responseEntity.getBody();
        if (body == null || body.get("access_token") == null||
        body.get("refresh_token"))
//        AuthToken authToken = new AuthToken();
            String jti = (String) body.get("jti");*/
        if (tokens == null) {
            return new LoginResult(AuthCode.AUTH_CREDENTIAL_ERROR, null);

        } else {
            String access_token = tokens.getAccess_token();
            this.saveCookie(access_token);
            return new LoginResult(CommonCode.SUCCESS, tokens.getAccess_token());
        }
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //从redis中删除令牌token
        String cookie = this.getCookie();
        AuthToken token = new AuthToken();
        token.setAccess_token(cookie);
        boolean logout = authService.logout(token);

        //删除cookie
        this.delCookie(cookie);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        //取出cookie令牌
        String cookie = getCookie();
        //拿着身份令牌,从redis中查询jwt令牌
        String userjwt = authService.getUserjwt(cookie);
        if (userjwt == null) {
            return new JwtResult(CommonCode.FAIL, null);
        }
        return new JwtResult(CommonCode.SUCCESS, userjwt);
    }

    private void delCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();


        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }

    private String getCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map != null && map.get("uid") != null) {
            return map.get("uid");
        }
        return null;

    }


    private void saveCookie(String token) {
        /*HttpServletResponse response,String domain,String path, String name,
                String value, int maxAge,boolean httpOnly*/
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        /*Cookie cookie = new Cookie();
        response.addCookie();*/
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);

    }
}
