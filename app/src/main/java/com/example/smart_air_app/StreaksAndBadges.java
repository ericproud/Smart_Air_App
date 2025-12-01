package com.example.smart_air_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StreaksAndBadges extends AppCompatActivity {

    int defaul_tec = 10;
    int default_low_rescue = 4;


    /// /////////////I need two int here, one is login days, the other is the number of something el
    /// that can be bundeled for a badges ( i.e. number of t excersises done)
/// PS baraye milistone haye dige bebin kare dige ie mishe card ya hoseleh nadari
    /// /// mitoni 3 ta az ye aks dorost koni ba addad haye moktaleft faghat backend moskeleh


    int login_streak ;
    int exercise ;

    String type;
    String childUId;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaks_and_badges);

        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser(); ///current usr of the page

        if(usr == null){
            finish();
            return;
        }

        String usrId = usr.getUid();

        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference("Users").
                child(usrId);

        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue( String.class );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /// two thing can happen either you are the child and I will just show you your thing
        /// or a parent and I will get the child and do the wierd thing
        if( type == "child" ) {
            childUId  = usrId;
        } else {
            childUId = getIntent().getStringExtra("the kid");
        }
        DatabaseReference streakRef = FirebaseDatabase.getInstance().getReference("Streaks").child(childUId);

        streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot streakSnapshot) {
                // Get Streak Info

                if (streakSnapshot.child("consecutive controller use days").exists()) {
                    login_streak = streakSnapshot.child("consecutive controller use days").getValue(Integer.class);

                }
                if (streakSnapshot.child("consecutive technique conpleted days").exists()) {
                    exercise = streakSnapshot.child("consecutive technique conpleted days").getValue(Integer.class);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        /*
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference("Users").child(usrId)
                . child("type");
        DatabaseReference streakRef = FirebaseDatabase.getInstance().getReference("Streaks").child(usrId);
        //now we wait to
        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                // Get type Info
                String type = userSnapshot.getValue(String.class);

                // 2. NOW, go get the Streak info
                streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot streakSnapshot) {
                        // Get Streak Info
                        Integer techniqueCount = 0;
                        if (streakSnapshot.child("tecnique").exists()) {
                            techniqueCount = streakSnapshot.child("tecnique").getValue(Integer.class);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FIREBASE_ERROR", "Failed to read value.", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE_ERROR", "Failed to read value.", error.toException());
            }
        });
        */



        ImageView rightbadgeImageView = findViewById(R.id.right);

        if(login_streak <= 0) {
            rightbadgeImageView.setImageResource(R.drawable.sam);
        } else if ( login_streak == 1) {
            rightbadgeImageView.setImageResource(R.drawable.tree);
        } else {
            rightbadgeImageView.setImageResource(R.drawable.forest);
        }

        rightbadgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Show the Toast message
                Toast.makeText(StreaksAndBadges.this,
                        "you have a " + login_streak + "consecutive controller use days" ,
                        Toast.LENGTH_SHORT).show();
            }
        });
        ImageView leftbadgeImageView = findViewById(R.id.left);

        if(exercise < defaul_tec) {
            leftbadgeImageView.setImageResource(R.drawable.lungs);
        } else if ( exercise == defaul_tec) {
            leftbadgeImageView.setImageResource(R.drawable.lungs1);
        } else {
            leftbadgeImageView.setImageResource(R.drawable.lungs2);
        }

        leftbadgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StreaksAndBadges.this,
                        "you have a " + exercise + "consecutive technique completed days",
                        Toast.LENGTH_SHORT).show();
            }
        });
        /// milestone!!
        String flag = "";
        if(  login_streak < 7) {
            flag = "not";
        }
        TextView daysStreak = findViewById(R.id.streakNumber);
        daysStreak.setText( "You have " +  login_streak + " days of login streak" +
                 "\n" + "and " + exercise + " days of controlled excersice" + "\n" +
                "You have " + flag + "reached the perfect controller week milestone.");




    }
}
