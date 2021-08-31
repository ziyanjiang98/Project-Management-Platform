package com.zy.zymonitor.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.Item;
import com.zy.zymonitor.bean.ItemResourceChange;
import com.zy.zymonitor.bean.Resource;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.mapper.ItemMapper;
import com.zy.zymonitor.mapper.ResourceMapper;
import com.zy.zymonitor.redis.RedisLock;
import com.zy.zymonitor.service.ItemService;
import com.zy.zymonitor.exception.ItemException;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/12
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ResourceMapper resourceMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    RedisLock redisLock;

    //Transaction
    @Override
    public void createItem(Map<String, String[]> map) throws ItemException, RedisOutOfTimeException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 30);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");

        try {
            String[] resourceIds = map.get(RESOURCE);
            String resourceCount = map.get(RESOURCE_COUNT)[0];
            String[] count = map.get(COUNT);
            HashSet<String> idSet = new HashSet<>();
            for(String s : resourceIds){
                idSet.add(s);
            }
            if(idSet.size() < resourceIds.length)
                throw new ItemException("Please combine duplicate resource names and amount!");
            if(ifItemNameRepeat(map.get("itemName")[0]))
                throw new ItemException("Duplicate project name!");
            //count is length
            Item item = new Item(map.get("itemName")[0], map.get("manager")[0], map.get("detail")[0]);
            itemMapper.insertItem(item);
            //Item already has an ID assigned by Mybatis
            for(int i = 0; i < Integer.parseInt(resourceCount); i++){
                //Write to database
                itemMapper.insertItemResource(item.getId(), resourceIds[i], Integer.parseInt(count[i]));
            }
        } finally {
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }
    }

    @Override
    public Item getItemById(String id){
        return itemMapper.queryItemById(id);
    }

    @Override
    public PageInfo<Item> getItemPage(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(itemMapper.queryAllItems());
    }

    @Override
    public void updateItemInfo(String id, String name, String manager, String detail) throws ItemException, RedisOutOfTimeException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 10);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");
        try {
            Item item = getItemById(id);
            if(item == null)
                throw new ItemException("The project does not exist!");
            if(ifItemNameRepeat(name))
                throw new ItemException("The project name already exists!");
            item.setName(name).setManager(manager).setDetail(detail);
            itemMapper.updateItemInfo(item);
        } finally {
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }
    }

    @Override
    public void updateItemResource(String itemId, String resourceId, int change, String detail) throws ItemException, RedisOutOfTimeException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 10);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");

        try {
            Item item = itemMapper.queryItemById(itemId);
            if(item == null)
                throw new ItemException("The project does not exist!");
            Resource targetResource = null;
            for(Resource resource : item.getResources()){
                if(resource.getId().equals(resourceId)){
                    //Find matched resource
                    targetResource = resource;
                    long demand = resource.getDemand();
                    long supply = resource.getSupply();
                    long newSupply = supply + change;
                    if(newSupply > demand || newSupply < 0)
                        throw new ItemException("Invalid data after changing storage!");
                }
            }
            if(targetResource == null)
                throw new ItemException("This resource is not included in this project!");
            String username = (String) SecurityUtils.getSubject().getPrincipal();
            ItemResourceChange itemResourceChange = new ItemResourceChange(item.getId(), targetResource.getId(),  username, new java.sql.Date(System.currentTimeMillis()), change, detail);
            itemMapper.insertItemResourceChange(itemResourceChange);
//            //Test if transaction is open
//            int i = 10/0;
            itemMapper.updateItemResource(itemId, resourceId, change);
        } finally {
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }
    }

    @Override
    public PageInfo<ItemResourceChange> getItemResourceChangePageById(String id, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(itemMapper.queryAllChangesOfItem(id));
    }

    @Override
    public void deleteItemInfo(String itemId) throws RedisOutOfTimeException, ItemException {
        String identifier = redisLock.lockWithTimeout(RedisLock.ITEM_RESOURCE_LOCK_KEY, 5, 10);
        if(identifier == null)
            throw new RedisOutOfTimeException("The server is busy. Please try again later!");

        try {
            Item item = itemMapper.queryItemById(itemId);
            if(item == null)
                throw new ItemException("The project does not exist!");
            itemMapper.deleteItemById(itemId);
            itemMapper.deleteItemResourceById(itemId);
            itemMapper.deleteItemResourceChangeById(itemId);
        } finally {
            redisLock.releaseLock(RedisLock.ITEM_RESOURCE_LOCK_KEY,identifier);
        }

    }

    private boolean ifItemNameRepeat(String name){
        Item item = itemMapper.queryItemByName(name);
        if(item == null)
            return false;
        return true;
    }
}
