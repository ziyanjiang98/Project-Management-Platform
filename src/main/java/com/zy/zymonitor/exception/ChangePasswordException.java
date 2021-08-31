package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/3
 */
public class ChangePasswordException extends UserServiceException {
    public ChangePasswordException(){
        super();
    };

    public ChangePasswordException(String msg){
        super(msg);
    }
}