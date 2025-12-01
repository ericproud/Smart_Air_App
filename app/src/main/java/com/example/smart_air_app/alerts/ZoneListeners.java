package com.example.smart_air_app.alerts;

import androidx.annotation.NonNull;

import com.example.smart_air_app.utils.NotificationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ZoneListeners extends EntryListeners<ValueEventListener> {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM dd yyyy")
            .toFormatter(Locale.ENGLISH);
    public ZoneListeners(DatabaseReference ref) {
        super(ref);
    }

    @Override
    public void installListener(String childUserId, String childName) {
        ValueEventListener l = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return; // child just created so zone object is null

                String dateString = snapshot.child("date").getValue(String.class);
                if (dateString == null) return;

                LocalDate date;
                try {
                    date = LocalDate.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    return;
                }

                Integer pb = snapshot.child("pb").getValue(Integer.class);
                Integer pef = snapshot.child("pef").getValue(Integer.class);

                if (pb == null || pef == null) return;
                if (!date.isEqual(LocalDate.now())) return;
                if (pb == -1 || pb == 0) return;
                if (pef / (double) pb <= 0.5) {
                    NotificationUtils.show(context, "Red-Zone Day",
                            String.format("Your child %s is in a red-zone day.", childName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        listeners.add(new EntryListener<>(childUserId, l));
        ref.child(childUserId).child("Zones").addValueEventListener(l);
    }

    @Override
    public void removeListeners() {
        listeners.forEach(l -> ref.child(l.childUserId).child("Zones").removeEventListener(l.listener));
    }
}
