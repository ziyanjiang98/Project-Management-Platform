package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/9
 */
public class PageException extends Exception {
    public PageException(){
        super();
    };

    public PageException(String msg){
        super(msg);
    }
}
