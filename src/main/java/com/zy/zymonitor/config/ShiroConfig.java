package com.zy.zymonitor.config;

import com.zy.zymonitor.redis.RedisSessionDao;
import com.zy.zymonitor.shiro.cache.RedisCacheManager;
import com.zy.zymonitor.shiro.realm.AccountRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/1
 */
@Configuration
public class ShiroConfig {
    // 1.ShiroFilter
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        //Public resource
        Map<String, String> map = new HashMap<>();
        map.put("/user/**","anon");
        map.put("/pin/**","anon");
        map.put("/static/**","anon");
        //Private resource
        map.put("/**","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        //Default page without Authorization
        shiroFilterFactoryBean.setLoginUrl("/user/login");
        return shiroFilterFactoryBean;
    }

    // 2.SecurityManager
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("accountRealm") Realm realm, RedisSessionDao redisSessionDao){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(realm);
        defaultWebSecurityManager.setSessionManager(defaultWebSessionManager(redisSessionDao));
        // Cancel remember me
        defaultWebSecurityManager.setRememberMeManager(null);
        return defaultWebSecurityManager;
    }

    // 3.realm
    @Bean("accountRealm")
    public Realm getRealm(){
        //1. Create a Realm
        AccountRealm accountRealm = new AccountRealm();
        //2. Create password Mtcher
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //3. Set encryption method and Hash Iteration time
        credentialsMatcher.setHashAlgorithmName("md5");
        credentialsMatcher.setHashIterations(1024);
        //4. Add Matcher to Realm
        accountRealm.setCredentialsMatcher(credentialsMatcher);
        // 5. Add Cache Manager
        accountRealm.setCacheManager(new RedisCacheManager());
        // Open Cache
        accountRealm.setCachingEnabled(true);
        accountRealm.setAuthenticationCachingEnabled(true);
        accountRealm.setAuthorizationCachingEnabled(true);
        accountRealm.setAuthenticationCacheName("authenticationCache");
        accountRealm.setAuthorizationCacheName("authorizationCache");
        return accountRealm;
    }

    // 4.SessionManager
    @Bean
    public DefaultWebSessionManager defaultWebSessionManager(RedisSessionDao redisSessionDao) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(redisSessionDao.getExpireTime() * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        /**
         * Session name in Cookie
         */
        sessionManager.setSessionIdCookie(new SimpleCookie("JSESSIONID"));
        return sessionManager;
    }
}
