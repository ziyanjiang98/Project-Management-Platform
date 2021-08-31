package com.zy.zymonitor.controller;

import com.zy.zymonitor.redis.RedisLock;
import com.zy.zymonitor.service.UserService;
import com.zy.zymonitor.util.PinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
@Slf4j
@RestController
@RequestMapping("/pin")
public class PinController {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @PostMapping("/sendPinToPhone")
    public String sendPinToPhone(@RequestParam("phone") String phone,
                                 @RequestParam("page") String page){
        if(page == null || page.length() == 0)
            return "服务器信息缺失，发送失败！";
        String pin = PinUtil.getPin();
        // key: 前缀+手机号 value: pin
        if("REGISTER_USER".equals(page)){
            redisTemplate.opsForValue().set(RedisLock.REGISTER_USER_PIN_KEY + phone,pin,5, TimeUnit.MINUTES);
        }else if("CHANGE_PASSWORD".equals(page)){
            redisTemplate.opsForValue().set(RedisLock.CHANGE_PASSWORD_PIN_KEY + phone,pin,5, TimeUnit.MINUTES);
        }
        //模拟发送短信
        log.info(phone + ":" + pin);
        return "已向手机[" + phone + "]发送验证码，有效期5分钟！";
    }
}
