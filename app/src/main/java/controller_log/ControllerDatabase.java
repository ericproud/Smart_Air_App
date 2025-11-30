package controller_log;

import android.util.Log;

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

    public interface ScheduleCallBack {
        void onResult(List<String> steps);
    }

    public static String keyHelper(String date, String time)
    {
        String year = "";
        String month = "";
        String day = "";
        String time2 = String.format("%04d", Integer.parseInt(time.replace(":", "")));

        String[] temp = date.split(" ");

        year = temp[2];
        day = temp[1];

        if (temp[0].equals("JAN")) {
            month = "01";
        }
        else if (temp[0].equals("FEB")) {
            month = "02";
        }
        else if (temp[0].equals("MAR")) {
            month = "03";
        }
        else if (temp[0].equals("APR")) {
            month = "04";
        }
        else if (temp[0].equals("MAY")) {
            month = "05";
        }
        else if (temp[0].equals("JUN")) {
            month = "06";
        }
        else if (temp[0].equals("JUL")) {
            month = "07";
        }
        else if (temp[0].equals("AUG")) {
            month = "08";
        }
        else if (temp[0].equals("SEP")) {
            month = "09";
        }
        else if (temp[0].equals("OCT")) {
            month = "10";
        }
        else if (temp[0].equals("NOV")) {
            month = "11";
        }
        else if (temp[0].equals("DEC")) {
            month = "12";
        }

        return year + month + day + "_" + time2;
    }

    public static void logControllerDatabase(String id, ControllerLog info) {
        //key is the converted time to number for easy sorting
        String key = keyHelper(info.getDate(), info.getTime());

        //c_ref will points to the controller logs
        DatabaseReference c_ref = fdb.getReference("ControllerLogs").child(id).child(key);

        //if the controller log exists, this represents the difference between the 2 logs and updates the inventory accordingly
        int[] toDeduct = {info.getDoseInput()};

        c_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*
                if the log exists then we adjust inventory accordingly
                if the first log said the user used 8 puffs then relogs it as 6 puffs, we do 6 - 8 = -2
                so we subtract -2 or add 2 to the inventory rather then subtract 6 blindly. this is to prevent
                double counting of logs which can trigger low canister warnings early
                 */
                if (snapshot.exists()) {
                    DatabaseReference dose_ref = c_ref.child("amountUsed");

                    dose_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Long loggedDose = snapshot.getValue(Long.class);

                            //validating it so we ensure we aren't dealing with null pointer and crashing the program or negative input
                            if (loggedDose != null && loggedDose > 0) {
                                toDeduct[0] -= loggedDose.intValue();
                            }

                            //saves the inventory and the controller logs with the new difference
                            logControllerDatabase2(id, info, toDeduct[0]);
                            logControllerLog(c_ref, info);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else {
                    //saves the inventory and log using default values
                    logControllerDatabase2(id, info, toDeduct[0]);
                    logControllerLog(c_ref, info);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //handles logging controller usage
    private static void logControllerLog(DatabaseReference ref, ControllerLog info) {
        //saving the inputs as given
        ref.child("amountUsed").setValue(info.getDoseInput());
        ref.child("breathRating").setValue(info.getFeeling());
        ref.child("shortnessBreathRating").setValue(info.getBreathShortness());

        //the following inputs are optional and may not have been given

        //setting -1 if pre input wasn't given otherwise logging what was given
        if (info.getPreInput() == -69) {
            ref.child("Pre PEF").setValue(-1);
        } else {
            ref.child("Pre PEF").setValue(info.getPreInput());
        }

        //setting -1 if post input wasn't given otherwise logging what was given
        if (info.getPostInput() == -69) {
            ref.child("postPEF").setValue(-1);
        } else {
            ref.child("postPEF").setValue(info.getPostInput());
        }
    }

    //this function updates the inventory and is called with the toDeduct value to get around asynch running issues
    private static void logControllerDatabase2(String id, ControllerLog info, int deduct) {
        //d_ref will update the inventory
        DatabaseReference d_ref = fdb.getReference("Inventory").child(id).child("1");

        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference inv_ref = d_ref.child("remaining");

                    //since the log exists we want specifically the "remaining" which should exist as the inventory log itself exists
                    inv_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //if the inventory exists we just update the amount used
                            Integer remaining = snapshot.getValue(Integer.class);

                            //ensuring we deal with good non null pointer values so we don't crash
                            if (remaining != null) {
                                int temp_remaining = remaining - deduct;

                                //ensuring temp_remaining >= 0 so we have valid inventory
                                if (temp_remaining < 0) {
                                    temp_remaining = 0;
                                }

                                //saving the non negative inventory
                                inv_ref.setValue(temp_remaining);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else {
                    //if the inventory doesn't exist we fill it with dummy values to show the inventory manager this was used but not logged
                    //the user id does not exist in inventory so we store it using empty strings
                    d_ref.child("expires").setValue("");
                    d_ref.child("lastPurchased").setValue("");
                    d_ref.child("remaining").setValue(200 - info.getDoseInput());
                    d_ref.child("reportedBy").setValue("");
                    d_ref.child("total").setValue(200);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //loads the controller schedule
    public static void ControllerScheduleLoader(String id, ScheduleCallBack callback) {
        DatabaseReference d_ref = fdb.getReference("ControllerSchedule").child(id);

        d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //u_ref is where the schedule for a user is
                    DatabaseReference u_ref = d_ref.child("Schedule");

                    u_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //fill schedule with database info
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
                        }
                    });
                }
                else {
                    callback.onResult(new ArrayList<String>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //saves the controller schedule
    public static void ControllerScheduleSaver(String id, List<String> schedule) {
        DatabaseReference d_ref = fdb.getReference("ControllerSchedule").child(id).child("Schedule");

        d_ref.setValue(schedule);
    }
}
