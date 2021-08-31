package com.zy.zymonitor.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/12
 */
@Data
@Accessors(chain = true)
public class Item {
    private String id;
    private String name;
    private String manager;
    private String detail;
    private List<Resource> resources;

    public Item() {
    }

    public Item(String name, String manager, String detail, List<Resource> resources) {
        this.name = name;
        this.manager = manager;
        this.detail = detail;
        this.resources = resources;
    }

    public Item(String name, String manager, String detail) {
        this.name = name;
        this.manager = manager;
        this.detail = detail;
    }
}
