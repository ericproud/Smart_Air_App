package com.example.smart_air_app.alerts;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public abstract class EntryListeners<T> {
    protected DatabaseReference ref;
    protected List<T> listeners;

    protected Context context;

    public EntryListeners(DatabaseReference ref) {
        this.ref = ref;
        this.listeners = new ArrayList<>();
    }

    public abstract void installListener(String childUserId, String childName);
    public abstract void removeListeners();

    public void setContext(Context context) {
        this.context = context;
    }
}
