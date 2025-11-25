package com.example.smart_air_app.inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryPresenterImpl implements InventoryPresenter {

    private final InventoryView view;
    private final InventoryRepository repo;
    private final List<Medicine> inventory = new ArrayList<>();
    private final boolean[] editing = new boolean[2];
    private final String uid;

    public InventoryPresenterImpl(String uid, InventoryView view, InventoryRepository repo) {
        this.view = view;
        this.repo = repo;
        this.uid = uid;

        repo.setUid(uid);

        repo.fetchInventory(new InventoryRepository.FetchCallback() {
            @Override
            public void onSuccess(List<Medicine> inventory) {
                InventoryPresenterImpl.this.inventory.addAll(inventory);

                if (InventoryPresenterImpl.this.inventory.get(0) == null) {
                    Medicine m1 = new Medicine();
                    m1.setType("Quick-Relief");
                    m1.setRemaining(0);
                    m1.setTotal(0);
                    m1.setLastPurchased("");
                    m1.setExpires("");
                    m1.setReportedBy("parent");

                    InventoryPresenterImpl.this.inventory.set(0, m1);
                }

                if (InventoryPresenterImpl.this.inventory.get(1) == null) {
                    Medicine m2 = new Medicine();
                    m2.setType("Controller");
                    m2.setRemaining(0);
                    m2.setTotal(0);
                    m2.setLastPurchased("");
                    m2.setExpires("");
                    m2.setReportedBy("parent");

                    InventoryPresenterImpl.this.inventory.set(1, m2);
                }

                editing[0] = false;
                editing[1] = false;

                view.showViewMode(0, InventoryPresenterImpl.this.inventory.get(0));
                view.showViewMode(1, InventoryPresenterImpl.this.inventory.get(1));
            }

            @Override
            public void onError(String e) {
                view.showError(e);
            }
        });
    }

    @Override
    public void onEditToggle(int index) {
        boolean nowEditing = !editing[index];
        editing[index] = nowEditing;

        if (nowEditing) {
            view.showEditMode(index, inventory.get(index));
        } else {
            view.showViewMode(index, inventory.get(index));
        }
    }

    @Override
    public void onSave(int index, Medicine medicine) {
        if (medicine == null) {
            view.showError("Nothing to save");
            return;
        }

        boolean valid = true;

        if (medicine.getTotal() == 0) {
            view.showTotalError(index, "Total cannot be 0");
            valid = false;
        }

        if (medicine.getRemaining() > medicine.getTotal()) {
            view.showRemainingError(index, "Remaining must be less than total");
            valid = false;
        }

        if (medicine.getLastPurchased().isEmpty()) {
            view.showLastPurchasedError(index, "Last purchased cannot be empty");
            valid = false;
        }

        if (medicine.getExpires().isEmpty()) {
            view.showExpiresError(index, "Expires on cannot be empty");
            valid = false;
        }

        if (!valid) return;

        inventory.set(index, medicine);
        editing[index] = false;

        repo.saveInventory(inventory, index, new InventoryRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                view.showViewMode(index, medicine);
            }

            @Override
            public void onError(String e) {
                view.showError(e);
            }
        });
    }
}
