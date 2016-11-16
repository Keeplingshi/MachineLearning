package com.feng.fresh.model;

/**
 * 事件类型
 * Created by feng on 2016/8/25.
 */
public enum EventEnum {

    Acupuncture(0, "针刺"), Moxibustion(1, "艾灸"), Cupping(2, "拔罐"), Massage(3, "推拿按摩"),
    Traction(4, "牵引"), Attach(5, "贴敷"), irradiation(6, "照射"), Health(7, "保健"), Cure(8, "治疗");

    private int index;
    private String description;

    private EventEnum(int index, String description){
        this.index = index;
        this.description = description;
    }
}
