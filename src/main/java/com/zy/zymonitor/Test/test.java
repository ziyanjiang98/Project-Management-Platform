package com.zy.zymonitor.Test;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/1
 */
public class test {
    public static void main(String[] args) {
        Md5Hash md5 = new Md5Hash("admin","zane",1024);
        System.out.println(md5.toHex());

        String detail = "这是一个备注信息";
        System.out.println(detail.length());
    }
}
