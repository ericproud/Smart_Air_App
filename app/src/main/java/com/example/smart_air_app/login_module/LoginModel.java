package com.example.smart_air_app.login_module;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smart_air_app.user_classes.Child;
import com.example.smart_air_app.user_classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kotlin.jvm.internal.FunInterfaceConstructorReference;

public class LoginModel {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    // Callback interface to be implemented by the presenter
    public interface AuthCallback {
        void onAuthSuccess(String userType); // Callback for when login is successful
        void onAuthFailure(String message);  // Callback for when login fails
    }

    // login method takes email and password and will send the presenter the appropriate callback
    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                // If authentication is successful
                .addOnSuccessListener(authResult -> {

                    String userID = authResult.getUser().getUid(); // Get userID from user that just logged in
                    dbRef.child("Users").child(userID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  // Get snapshot of the user's data
                            if (dataSnapshot.exists()) {
                                String userType = dataSnapshot.child("type").getValue(String.class);    // Get the users type
                                if (userType != null && !userType.isEmpty()) {  // if type is not empty/null
                                    callback.onAuthSuccess(userType);   // Call the success callback
                                                                        // with the users type
                                } else {
                                    callback.onAuthFailure("User type not found."); // Call the failure callback
                                                                                    // if the type is not found
                                }
                            } else {
                                callback.onAuthFailure("User data not found.");  // Call the failure callback
                                                                                 // if the user data is not found
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onAuthFailure("Authorization failed");     // Call the failure callback
                                                                                // if there is an error
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    callback.onAuthFailure("Authorization failed"); // Call the failure callback
                                                                    // if there is not a user
                                                                    // associated with the email
                                                                    // and password submitted
                });
    }
}
