package com.zy.zymonitor.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/3
 */
@Service
public class RedisLock {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;//redis

    private final String lockPrefix = "REDIS_LOCK:"; //Lock key

    protected final long internalLockLeaseTime = 10;//expire time

    private final long timeout = 999999; //expire time of waiting for lock

    public static final String REGISTER_USER_PIN_KEY = "REGISTER_USER_PIN";
    public static final String CHANGE_PASSWORD_PIN_KEY = "CHANGE_PASSWORD_PIN";

    public static final String USER_INFO_LOCK_KEY = "USER_INFO_LOCK";
    public static final String ITEM_RESOURCE_LOCK_KEY = "ITEM_RESOURCE_LOCK_KEY";

    /**
     * 加锁
     * @param lockName       Lock name
     * @param acquireTimeout Expire time of waiting lock
     * @param timeout        Expire time of lock
     * @return Lock identifier
     */
    public String lockWithTimeout(String lockName, long acquireTimeout, long timeout){
        String lockKey = lockPrefix + lockName;
        String identifier = UUID.randomUUID().toString();
        //Time point to give up
        long end = System.currentTimeMillis() + acquireTimeout * 1000L;
        //Lock identifier
        String retIdentifier = null;

        while (System.currentTimeMillis() < end){
            Boolean ifInsert = redisTemplate.opsForValue().setIfAbsent(lockKey, identifier, timeout, TimeUnit.SECONDS);
            if(ifInsert != null && ifInsert){
                //Successfully obtain
                retIdentifier = identifier;
                return retIdentifier;
            }
            //If the lock does not have a expert time, then set time
            Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);;
            if(expire != null && expire == -1){
                redisTemplate.expire(lockKey,internalLockLeaseTime, TimeUnit.SECONDS);
            }
            try {
                //Sleep and try again
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Out of waiting time, give up
        return retIdentifier;
    }

    public boolean releaseLock(String lockName, String identifier){
        String lockKey =lockPrefix + lockName;
        //Is lock released
        boolean retFlag = false;
        if (identifier.equals(redisTemplate.opsForValue().get(lockKey))){
            //Identifier is matched
            redisTemplate.delete(lockKey);
            retFlag = true;
        }
        return retFlag;
    }
}
