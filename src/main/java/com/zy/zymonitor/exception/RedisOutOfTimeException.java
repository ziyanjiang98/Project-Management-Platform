package com.zy.zymonitor.exception;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/3
 */
public class RedisOutOfTimeException extends Exception {
    public RedisOutOfTimeException(){
        super();
    };

    public RedisOutOfTimeException(String msg){
        super(msg);
    }
}
