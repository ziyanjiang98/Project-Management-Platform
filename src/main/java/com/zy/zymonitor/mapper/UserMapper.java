package com.zy.zymonitor.mapper;

import com.zy.zymonitor.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/6/27
 */
@Mapper
public interface UserMapper {
    User queryUserById(int id);

    User queryUserByName(String name);

    User queryUserByPhone(String phone);

    void insertUser(User user);

    void updateUser(User user);

    List<User> queryAllUsers();

    void deleteUserById(int id);
}
