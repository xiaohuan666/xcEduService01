package com.xuecheng.ucenter.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XcMenuMapper {
    List<com.xuecheng.framework.domain.ucenter.XcMenu> findMenuListByUserId(String userId);
}
