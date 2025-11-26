package com.example.smart_air_app.inventory;

public interface InventoryPresenter {
    void onEditToggle(int index);
    void onSave(int index, Medicine medicine);
}
