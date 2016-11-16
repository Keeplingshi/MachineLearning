package com.feng.fresh.model;

/**
 * 事件类
 * 组成：事件类型； 事件触发词， 事件触发词个数
 * Created by feng on 2016/8/25.
 */
public class Event {

    EventEnum type;
    String triggerWord;
    Integer count;

    public Event(EventEnum type, String triggerWord, Integer count) {
        this.type = type;
        this.triggerWord = triggerWord;
        this.count = count;
    }

    public EventEnum getType() {
        return type;
    }

    public void setType(EventEnum type) {
        this.type = type;
    }

    public String getTriggerWord() {
        return triggerWord;
    }

    public void setTriggerWord(String triggerWord) {
        this.triggerWord = triggerWord;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {

        if((null==obj) || !(obj instanceof Event)) return false;
        Event event = (Event)obj;
        if(event.type.equals(this.type) && event.triggerWord.equals(this.triggerWord)) return true;
        return false;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", triggerWord='" + triggerWord + '\'' +
                ", count=" + count +
                '}';
    }
}
