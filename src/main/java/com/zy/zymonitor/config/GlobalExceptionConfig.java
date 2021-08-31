package com.zy.zymonitor.config;

import com.zy.zymonitor.exception.PageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/6
 */

@Slf4j
@Configuration
public class GlobalExceptionConfig implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView();
        // redirect to diffent wrong page if the situation is included in following
        if (ex instanceof AuthorizationException) {
            mv.setViewName("error/403");
        } else if(ex instanceof PageException) {
            mv.setViewName("error/404");
        } else{
            mv.setViewName("error/404");
        }
        mv.addObject("error", ex.toString());
        return mv;
    }
}
