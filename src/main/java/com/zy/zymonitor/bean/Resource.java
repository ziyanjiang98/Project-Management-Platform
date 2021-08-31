package com.zy.zymonitor.bean;

import com.zy.zymonitor.bean.enums.Unit;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/8
 */
@Data
public class Resource implements Serializable {
    private String id;
    private String name;
    private Unit unit;
    private String detail;
    private long demand;
    private long supply;

    public Resource() {
    }

    public Resource(String name, Unit unit, String detail) {
        this.name = name;
        this.unit = unit;
        this.detail = detail;
    }

    public Resource(String id, String name, Unit unit, String detail) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.detail = detail;
    }
}
