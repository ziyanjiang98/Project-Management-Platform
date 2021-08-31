package com.zy.zymonitor.shiro.realm;

import com.zy.zymonitor.bean.User;
import com.zy.zymonitor.service.UserService;
import com.zy.zymonitor.shiro.salt.MySimpleByteSource;
import com.zy.zymonitor.util.ApplicationContextUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/1
 */
public class AccountRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        System.out.println("=====从数据库中获取权限信息=====");
        UserService userService = (UserService) ApplicationContextUtil.getBean("userService");
        User user = userService.getUserByName(username);
        if(user != null){
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            simpleAuthorizationInfo.addRole(user.getLevel());
            return simpleAuthorizationInfo;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        UserService userService = (UserService) ApplicationContextUtil.getBean("userService");
        User user = userService.getUserByName(username);
        if(user != null){
            return new SimpleAuthenticationInfo(user.getUsername(),user.getPassword(), new MySimpleByteSource("zane"),this.getName());
        }
        return null;
    }
}
