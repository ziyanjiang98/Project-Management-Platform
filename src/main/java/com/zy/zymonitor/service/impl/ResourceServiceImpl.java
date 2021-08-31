package com.zy.zymonitor.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.Resource;
import com.zy.zymonitor.mapper.ResourceMapper;
import com.zy.zymonitor.bean.enums.Unit;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.exception.ResourceException;
import com.zy.zymonitor.redis.RedisLock;
import com.zy.zymonitor.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/8
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    ResourceMapper resourceMapper;

    @Autowired
    RedisLock redisLock;

    @Override
    public void addNewResource(String name, String unit, String detail) throws ResourceException, RedisOutOfTimeException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            if(ifResourceNameRepeat(name))
                throw new ResourceException("Duplicate project name!");
            Resource resource = new Resource(name, Unit.valueOf(unit), detail);
            resourceMapper.insertResource(resource);
        }catch (ResourceException e) {
            throw e;
        }finally {
            //release lock
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }
    }

    @Override
    public PageInfo<Resource> getResourcePage(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(resourceMapper.queryAllResources());
    }

    @Override
    public void editResource(String id, String name, String unit, String detail) throws RedisOutOfTimeException, ResourceException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 5);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            Resource resource = resourceMapper.queryResourceById(Integer.parseInt(id));
            if(resource == null)
                throw new ResourceException("Changed resource does not exist!");
            resourceMapper.updateResource(new Resource(id, name, Unit.valueOf(unit), detail));
        } catch (ResourceException e) {
            throw e;
        } finally {
            //release lock
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }
    }

    @Override
    public Resource getResource(int id) {
        return resourceMapper.queryResourceById(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceMapper.queryAllResources();
    }

    private boolean ifResourceNameRepeat(String name){
        if(resourceMapper.queryResourceByName(name) == null){
            return false;
        }
        return true;
    }
}
