package com.xuecheng.auth.service;

import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;

public interface AuthService {
    public AuthToken login(LoginRequest loginRequest, String clientId, String clientSecret);

    String getUserjwt(String cookie);

    public boolean logout(AuthToken token);
}
