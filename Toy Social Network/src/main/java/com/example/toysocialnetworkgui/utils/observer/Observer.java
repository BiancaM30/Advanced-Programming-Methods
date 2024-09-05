package com.example.toysocialnetworkgui.utils.observer;


import com.example.toysocialnetworkgui.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}