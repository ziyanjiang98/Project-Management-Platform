package com.zy.zymonitor.mapper;

import com.zy.zymonitor.bean.ItemResourceChange;
import com.zy.zymonitor.bean.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/6/27
 */
@Mapper
public interface ItemMapper {
    void insertItem(Item item);

    void insertItemResource(@Param("itemId") String itemId, @Param("resourceId") String resourceId, @Param("demand") int demand);

    List<Item> queryAllItems();

    Item queryItemById(@Param("id") String id);

    void updateItemInfo(@Param("item") Item item);

    void updateItemResource(@Param("itemId") String itemId, @Param("resourceId") String resourceId, @Param("change") long change);

    void insertItemResourceChange(@Param("change") ItemResourceChange change);

    List<ItemResourceChange> queryAllChangesOfItem(@Param("itemId") String itemId);

    Item queryItemByName(@Param("name") String name);

    void deleteItemById(@Param("itemId") String itemId);

    void deleteItemResourceById(@Param("itemId") String itemId);

    void deleteItemResourceChangeById(@Param("itemId") String itemId);
}