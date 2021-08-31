package com.zy.zymonitor.util;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
public class PasswordUtil {
    public static String getMD5Password(String password){
        Md5Hash md5 = new Md5Hash(password,"zane",1024);
        return md5.toHex();
    }
}
