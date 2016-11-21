package com.model;

/**
 * 事件类型
 */
public enum EventEnum {

	Earthquake(0, "地震"), Fire(1, "火灾"), Accident(2, "交通事故"), Terror(3, "恐怖袭击"), FoodPoison(4, "食物中毒");

    private int index;
    private String description;

    private EventEnum(int index, String description){
        this.index = index;
        this.description = description;
    }
}
