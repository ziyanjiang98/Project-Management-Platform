package com.zy.zymonitor.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.User;
import com.zy.zymonitor.exception.ChangePasswordException;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.mapper.UserMapper;
import com.zy.zymonitor.redis.RedisLock;
import com.zy.zymonitor.exception.RegisterException;
import com.zy.zymonitor.exception.UserServiceException;
import com.zy.zymonitor.service.UserService;
import com.zy.zymonitor.util.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisLock redisLock;

    @Override
    public void registerNewUser(String username, String password, String phone, String pin) throws UserServiceException, RedisOutOfTimeException {
        // try to gain lock
        String identifier = redisLock.lockWithTimeout(RedisLock.USER_INFO_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");

        try {
            if(ifUsernameRepeat(username))
                throw new RegisterException("Duplicate username!");
            if(ifPhoneRepeat(phone))
                throw new RegisterException("Duplicate phone number!");
            //match pin
            if(!ifPinCorrect(RedisLock.REGISTER_USER_PIN_KEY, phone, pin))
                throw new RegisterException("Unmatched pin code！");
            //encryption password
            String MD5Password = PasswordUtil.getMD5Password(password);
            // initial level is 1
            User user = new User(username, MD5Password, phone, "1");
            userMapper.insertUser(user);
        } catch (RegisterException e) {
            throw e;
        } finally {
            //release lock
            redisLock.releaseLock(RedisLock.USER_INFO_LOCK_KEY,identifier);
        }
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.queryUserByName(username);
    }

    @Override
    public void changePassword(String username, String password, String phone, String pin) throws UserServiceException, RedisOutOfTimeException {
        String identifier = redisLock.lockWithTimeout(RedisLock.USER_INFO_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            User user = userMapper.queryUserByName(username);
            if(user == null)
                throw new ChangePasswordException("User does not exist!");
            if(!user.getPhone().equals(phone))
                throw new ChangePasswordException("Unmatched phone number!");
            if(!ifPinCorrect(RedisLock.CHANGE_PASSWORD_PIN_KEY, phone, pin));
            //encryption of password
            String MD5Password = PasswordUtil.getMD5Password(password);
            user.setPassword(MD5Password);
            //update database
            userMapper.updateUser(user);
        } catch (ChangePasswordException e) {
            throw e;
        } finally {
            //release lock
            redisLock.releaseLock(RedisLock.USER_INFO_LOCK_KEY,identifier);
        }
    }

    @Override
    public User getUserById(int id) {
        return userMapper.queryUserById(id);
    }

    @Override
    public void updateUserLevel(String id, String level) throws RedisOutOfTimeException, UserServiceException {
        String identifier = redisLock.lockWithTimeout(RedisLock.USER_INFO_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            User user = userMapper.queryUserById(Integer.parseInt(id));
            if(user == null)
                throw new UserServiceException("User does not exist!");
            Subject subject = SecurityUtils.getSubject();
            User currentUser = userMapper.queryUserByName((String) subject.getPrincipal());
            if(currentUser.getId().equals(user.getId()))
                throw new UserServiceException("You are not allowed to modify your own level!");
            user.setLevel(level);
            userMapper.updateUser(user);
        } catch (UserServiceException e) {
            throw e;
        } finally {
            // release lock
            redisLock.releaseLock(RedisLock.USER_INFO_LOCK_KEY,identifier);
        }
    }

    @Override
    public PageInfo<User> getUserPage(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(userMapper.queryAllUsers());
    }

    @Override
    public void changePhone(String username, String password, String phone, String pin) {

    }

    @Override
    public void deleteUser(String id) throws RedisOutOfTimeException, UserServiceException {
        String identifier = redisLock.lockWithTimeout(RedisLock.USER_INFO_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            User user = userMapper.queryUserById(Integer.parseInt(id));
            if(user == null)
                throw new UserServiceException("User does not exist!");
            Subject subject = SecurityUtils.getSubject();
            User currentUser = userMapper.queryUserByName((String) subject.getPrincipal());
            if(currentUser.getId().equals(user.getId()))
                throw new UserServiceException("You are not allowed to delete yourself!");
            userMapper.deleteUserById(Integer.parseInt(user.getId()));
        } catch (UserServiceException e) {
            throw e;
        } finally {
            // release lock
            redisLock.releaseLock(RedisLock.USER_INFO_LOCK_KEY,identifier);
        }
    }

    private boolean ifUsernameRepeat(String username){
        if(userMapper.queryUserByName(username) == null)
            return false;
        return true;
    }

    private boolean ifPhoneRepeat(String phone){
        if(userMapper.queryUserByPhone(phone) == null)
            return false;
        return true;
    }

    private boolean ifPinCorrect(String keyName, String phone, String pin) throws UserServiceException {
        String pinInRedis = redisTemplate.opsForValue().get(keyName + phone);
        if(pinInRedis == null)
            throw new UserServiceException("Pin code is out of time, please try again!");
        if(pin.equals(pinInRedis)) {
            // delete pin
            redisTemplate.delete(keyName + phone);
            return true;
        }
        return false;
    }
}
