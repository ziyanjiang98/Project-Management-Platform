package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/8
 */
public class ResourceException extends Exception {
    public ResourceException(){
        super();
    };

    public ResourceException(String msg){
        super(msg);
    }
}
