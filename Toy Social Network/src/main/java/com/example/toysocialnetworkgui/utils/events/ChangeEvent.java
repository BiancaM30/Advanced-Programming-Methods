package com.example.toysocialnetworkgui.utils.events;

import com.example.toysocialnetworkgui.domain.User;


public class ChangeEvent implements Event {
    private ChangeEventType type;
    private User data, oldData;
    private String info;

    public ChangeEvent(String info) {
        this.info = info;
    }

    public ChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    public ChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }

    public String getInfo() {
        return info;
    }
}
