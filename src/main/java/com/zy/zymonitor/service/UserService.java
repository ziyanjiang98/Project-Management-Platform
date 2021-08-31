package com.zy.zymonitor.service;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.User;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.exception.UserServiceException;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
public interface UserService {

    void registerNewUser(String username, String password, String phone, String pin) throws RedisOutOfTimeException, UserServiceException;

    User getUserByName(String username);

    void changePassword(String username, String password, String phone, String pin) throws RedisOutOfTimeException, UserServiceException;

    User getUserById(int id);

    void updateUserLevel(String id, String level) throws RedisOutOfTimeException, UserServiceException;

    PageInfo<User> getUserPage(int pageNum, int pageSize);

    void changePhone(String username, String password, String phone, String pin);

    void deleteUser(String id) throws RedisOutOfTimeException, UserServiceException;
}