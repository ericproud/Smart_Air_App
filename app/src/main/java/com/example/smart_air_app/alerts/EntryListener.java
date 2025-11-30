package com.example.smart_air_app.alerts;

public class EntryListener<U> {
    public String childUserId;
    public U listener;
    public EntryListener(String childUserId, U listener) {
        this.childUserId = childUserId;
        this.listener = listener;
    }
}