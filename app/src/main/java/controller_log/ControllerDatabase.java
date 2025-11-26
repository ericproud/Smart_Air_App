package controller_log;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ControllerDatabase {
    private static FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    public interface ControllerCallBack {
        void onResult(int value);
    }

    public interface ScheduleCallBack {
        void onResult(List<String> steps);
    }

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

    public static void personalBestGetter(String id, ControllerCallBack callback)
    {
        DatabaseReference d_ref = fdb.getReference("Users").child(id);

        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference u_ref = d_ref.child("personalBest");

                    u_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer pb = snapshot.getValue(Integer.class);
                            if (pb != null && pb > 0) {
                                callback.onResult(pb);
                            }
                            else {
                                callback.onResult(-1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //if this happens you are very unlucky
                        }
                    });
                }
                else {
                    callback.onResult(-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //hopefully this doesn't happen
            }
        });
    }

    public static void personalBestLogger(String id, ControllerLog inputs)
    {
        DatabaseReference d_ref = fdb.getReference("Users").child(id);

        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference u_ref = d_ref.child("personalBest");

                    u_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer set_pb = snapshot.getValue(Integer.class);
                            int toDoPB = 0;

                            if (set_pb != null && set_pb > inputs.getPB()) {
                                toDoPB = set_pb;
                            }
                            else {
                                toDoPB = inputs.getPB();
                            }

                            u_ref.setValue(toDoPB);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //hopefully this never happens
                        }
                    });
                }
                else {
                    DatabaseReference u_ref = d_ref.child("personalBest");

                    u_ref.setValue(inputs.getPB());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //hopefully this never runs
            }
        });
    }

    public static void ControllerScheduleLoader(String id, ScheduleCallBack callback) {
        DatabaseReference d_ref = fdb.getReference("ControllerSchedule").child(id);

        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference u_ref = d_ref.child("Schedule");

                    u_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> schedule = new ArrayList<>();
                            for (DataSnapshot val: snapshot.getChildren()) {
                                if (val != null) {
                                    schedule.add(val.getValue(String.class));
                                }
                            }
                            callback.onResult(schedule);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //if this happens you are very unlucky
                        }
                    });
                }
                else {
                    callback.onResult(new ArrayList<String>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //in the java gods we pray this does not happen
            }
        });
    }

    public static void ControllerScheduleSaver(String id, List<String> schedule) {
        DatabaseReference d_ref = fdb.getReference("ControllerSchedule").child(id).child("Schedule");

        d_ref.setValue(schedule);
    }
}
