package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{
    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    XcUserRepository xcUserRepository;

    @Autowired
    XcMenuMapper xcMenuMapper;
    //根据账号查询xcUser信息
    public XcUser getXcUserByUsername(String username){
        return xcUserRepository.findByUsername(username);
    }

    public XcUserExt getUserExt(String username){
        //查询用户基本信息
        XcUser xcUser = this.getXcUserByUsername(username);
        if (xcUser==null){
            return null;
        }
        String userId = xcUser.getId();
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        String companyId = null;
        if (xcCompanyUser!=null){
            companyId= xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);

        //查询用户权限信息
        String id = xcUserExt.getId();
        List<XcMenu> menuListByUserId = xcMenuMapper.findMenuListByUserId(id);
        xcUserExt.setPermissions(menuListByUserId);

        return xcUserExt;
    }
}
