package com.zy.zymonitor.mapper;

import com.zy.zymonitor.bean.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/6/27
 */
@Mapper
public interface ResourceMapper {
    Resource queryResourceById(int id);

    void insertResource(Resource resource);

    Resource queryResourceByName(String name);

    List<Resource> queryAllResources();

    void updateResource(Resource resource);
}
