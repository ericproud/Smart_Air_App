package controller_log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControllerDatabase {
    private static FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    public static void logControllerDatabase(String id, ControllerLog info) {
        DatabaseReference d_ref = fdb.getReference("Inventory").child(id);
        DatabaseReference c_ref = fdb.getReference("ControllerLogs").child(id);

        //updates inventory
        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //the user id exists in inventory so we can adjust it here
                    DatabaseReference i_ref = d_ref.child("1").child("remaining");

                    i_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer remainingVal = snapshot.getValue(Integer.class);
                            if (remainingVal != null && remainingVal > 0) {
                                int temp_remaining = remainingVal - info.getDoseInput();

                                if (temp_remaining < 0) {
                                    temp_remaining = 0;
                                }

                                i_ref.setValue(temp_remaining);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //hopefully this never happens
                        }
                    });
                }
                else {
                    //the user id does not exist in inventory so we store it using empty strings
                    d_ref.child("1").child("expires").setValue("");
                    d_ref.child("1").child("lastPurchased").setValue("");
                    d_ref.child("1").child("remaining").setValue(200 - info.getDoseInput());
                    d_ref.child("1").child("reportedBy").setValue("");
                    d_ref.child("1").child("total").setValue(200);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pray this never happens
            }
        });

        c_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference to_log = c_ref.child(info.getDate()).child(info.getTime());

                to_log.child("Date").setValue(info.getDate());
                to_log.child("Amount Used").setValue(info.getDoseInput());
                to_log.child("Breath Rating").setValue(info.getFeeling());
                to_log.child("Post PEF").setValue(info.getPostInput());
                to_log.child("Zone").setValue(info.getZone());

                if (info.getPreInput() == -69) {
                    to_log.child("Pre PEF").setValue(0);
                }
                else {
                    to_log.child("Pre PEF").setValue(info.getPreInput());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //hopefully this doesn't run
            }
        });
    }
}
