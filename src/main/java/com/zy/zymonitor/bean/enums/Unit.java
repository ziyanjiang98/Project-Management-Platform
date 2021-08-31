package com.zy.zymonitor.bean.enums;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/8
 */
public enum Unit {
    TON(0, "Ton"),
    METRE(1, "Metre"),
    SQUARE_METRE(2, "Squared metre"),
    CUBIC_METRE(3, "Cubic metre");

    private final int unitCode;
    private final String name;

    Unit(int unitCode, String name){
        this.unitCode = unitCode;
        this.name = name;
    }

    public int getUnitCode(){
        return this.unitCode;
    }

    public String getName() {
        return name;
    }
}