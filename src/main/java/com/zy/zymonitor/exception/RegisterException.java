package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
public class RegisterException extends UserServiceException {
    public RegisterException(){
        super();
    };

    public RegisterException(String msg){
        super(msg);
    }
}
