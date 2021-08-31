package com.zy.zymonitor.service;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.Resource;
import com.zy.zymonitor.exception.ResourceException;
import com.zy.zymonitor.exception.RedisOutOfTimeException;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/8
 */
public interface ResourceService {
    void addNewResource(String name, String unit, String detail) throws ResourceException, RedisOutOfTimeException;

    PageInfo<Resource> getResourcePage(int pageNum, int pageSize);

    void editResource(String id, String name, String unit, String detail) throws RedisOutOfTimeException, ResourceException;

    Resource getResource(int id);

    List<Resource> getAllResources();
}
