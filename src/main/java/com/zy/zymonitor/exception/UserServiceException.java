package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/3
 */
public class UserServiceException extends Exception {
    public UserServiceException(){
        super();
    };

    public UserServiceException(String msg){
        super(msg);
    }
}
