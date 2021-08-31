package com.zy.zymonitor.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/6/27
 */
@Data
public class User implements Serializable {
    private String id;
    private String username;
    //MD5 + salt
    private String password;
    private String phone;
    private String level;

    public User() {
    }

    public User(String username, String password, String phone, String level) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.level = level;
    }
}
