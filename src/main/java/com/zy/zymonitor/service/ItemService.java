package com.zy.zymonitor.service;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.Item;
import com.zy.zymonitor.bean.ItemResourceChange;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.exception.ItemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/12
 */
public interface ItemService {
    //和前端的协议参数名
    public final static String RESOURCE = "resource";
    public final static String RESOURCE_COUNT = "resourceCount";
    public final static String COUNT = "count";

    void createItem(Map<String, String[]> map) throws ItemException, RedisOutOfTimeException;

    Item getItemById(String id);

    PageInfo<Item> getItemPage(int pageNum, int pageSize);

    @Transactional
    void updateItemInfo(String id, String name, String manager, String detail) throws ItemException, RedisOutOfTimeException;

    @Transactional
    void updateItemResource(String itemId, String resourceId, int change, String detail) throws ItemException, RedisOutOfTimeException;

    PageInfo<ItemResourceChange> getItemResourceChangePageById(String id, int pageNum, int pageSize);

    @Transactional
    void deleteItemInfo(String id) throws RedisOutOfTimeException, ItemException;
}