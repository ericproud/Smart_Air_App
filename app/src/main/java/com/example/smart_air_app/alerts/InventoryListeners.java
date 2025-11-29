package com.example.smart_air_app.alerts;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.smart_air_app.inventory.Medicine;
import com.example.smart_air_app.utils.NotificationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InventoryListeners extends EntryListeners<ValueEventListener> {
    static class UserMedicines {
        public String name;
        public List<Medicine> inventory;

        public UserMedicines() {}

        public UserMedicines(String name, List<Medicine> inventory) {
            this.name = name;
            this.inventory = inventory;
        }

        @Override
        public String toString() {
            return "UserMedicines{" +
                    "name='" + name + '\'' +
                    ", inventory=" + inventory +
                    '}';
        }
    }
    private final Map<String, UserMedicines> inventory;

    private final Map<String, Boolean> notifiedExpired = new HashMap<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);

    private final Runnable expiryChecker;

    public InventoryListeners(DatabaseReference ref) {
        super(ref);
        inventory = new HashMap<>();

        expiryChecker = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();

                inventory.forEach((uid, medicines) -> {
                    if (medicines.inventory.size() < 2) return;

                    Medicine quick = medicines.inventory.get(0);
                    Medicine controller = medicines.inventory.get(1);

                    // Construct unique keys for each medicine
                    String quickKey = uid + "_quick";
                    String controllerKey = uid + "_controller";

                    try {
                        Date quickExpires = sdf.parse(quick.getExpires());
                        Date controllerExpires = sdf.parse(controller.getExpires());

                        // Quick-relief expiry
                        if (quickExpires != null && now.after(quickExpires) && !notifiedExpired.containsKey(quickKey)) {
                            NotificationUtils.show(context, "Inventory Expired",
                                    String.format("%s: Quick-relief medication has expired.", medicines.name));
                            notifiedExpired.put(quickKey, true);
                        }

                        // Controller expiry
                        if (controllerExpires != null && now.after(controllerExpires) && !notifiedExpired.containsKey(controllerKey)) {
                            NotificationUtils.show(context, "Inventory Expired",
                                    String.format("%s: Controller medication has expired.", medicines.name));
                            notifiedExpired.put(controllerKey, true);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });

                // Schedule next run
                handler.postDelayed(this, 5000);
            }
        };


        handler.post(expiryChecker);
    }

    @Override
    public void installListener(String childUserId, String childName) {
        ValueEventListener l = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Medicine> medicines = new ArrayList<>();

                snapshot.getChildren().forEach(medicine -> {
                    Medicine m = medicine.getValue(Medicine.class);
                    medicines.add(m);
                });

                Medicine quick = medicines.get(0);
                Medicine controller = medicines.get(1);
                if ((double) quick.getRemaining() / quick.getTotal() < 0.2) {
                    NotificationUtils.show(context, "Inventory Low (<20%)",
                            String.format("%s: Quick-relief medicine is running low: %d / %d doses remaining.", childName, quick.getRemaining(), quick.getTotal()));
                }

                if ((double) controller.getRemaining() / controller.getTotal() < 0.2) {
                    NotificationUtils.show(context, "Inventory Low (<20%)",
                            String.format("%s: Controller medicine is running low: %d / %d doses remaining.", childName, controller.getRemaining(), controller.getTotal()));
                }

                // add to map to be used later to detect expired medicine
                inventory.put(childUserId, new UserMedicines(childName, medicines));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ref.child(childUserId).addValueEventListener(l);
    }

    @Override
    public void removeListeners() {
        listeners.forEach(l -> ref.removeEventListener(l));
        handler.removeCallbacks(expiryChecker);
    }
}
