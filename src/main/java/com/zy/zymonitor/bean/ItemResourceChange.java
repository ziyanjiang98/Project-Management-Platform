package com.zy.zymonitor.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/18
 */
@Data
@Accessors(chain = true)
public class ItemResourceChange {
    private String id;
    private String itemId;
    private String resourceId;
    private String username;
    private Date time;
    private long change;
    private String detail;

    //Detail infomation of resource
    private Resource resource;

    public ItemResourceChange() {
    }

    public ItemResourceChange(String itemId, String resourceId, String username, Date time, long change, String detail) {
        this.itemId = itemId;
        this.resourceId = resourceId;
        this.username = username;
        this.time = time;
        this.change = change;
        this.detail = detail;
    }
}
