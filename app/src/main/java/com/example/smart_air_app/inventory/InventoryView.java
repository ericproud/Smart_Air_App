package com.example.smart_air_app.inventory;

public interface InventoryView {
    void showEditMode(int index, Medicine medicine);
    void showViewMode(int index, Medicine medicine);
    void clearErrors(int index);
    void showRemainingError(int index, String message);
    void showTotalError(int index, String message);
    void showLastPurchasedError(int index, String message);
    void showExpiresError(int index, String message);
    void showError(String message);
}
