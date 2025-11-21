package com.example.smart_air_app.login_module;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

    public interface AuthCallback {
        void onAuthSuccess(String type);
        void onAuthFailure(String message);
    }

    public void signIn(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();

                    // Get user type from Realtime Database
                    dbRef.child("Users").child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String userType = dataSnapshot.child("type").getValue(String.class);
                                        if (userType != null && !userType.isEmpty()) {
                                            callback.onAuthSuccess(userType);
                                        } else {
                                            callback.onAuthFailure("User type not found.");
                                        }
                                    } else {
                                        callback.onAuthFailure("User data not found.");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    callback.onAuthFailure("Authorization failed");
                                }
                            });
                })
                .addOnFailureListener(exception -> {
                    callback.onAuthFailure("Authorization failed");
                });
    }
}
