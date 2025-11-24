package com.example.smart_air_app.inventory;

import java.util.List;

public interface InventoryRepository {
    interface SaveCallback {
        void onSuccess();
        void onError(String e);
    }

    interface FetchCallback {
        void onSuccess(List<Medicine> inventory);
        void onError(String e);
    }
    void saveInventory(List<Medicine> inventory, int index, SaveCallback callback);
    void fetchInventory(FetchCallback callback);
}
