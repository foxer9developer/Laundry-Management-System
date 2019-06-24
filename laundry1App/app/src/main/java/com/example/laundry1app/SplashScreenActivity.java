package com.example.laundry1app;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
public class SplashScreenActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private boolean flag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    reference = FirebaseDatabase.getInstance().getReference("admins");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("email").getValue(String.class), FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    flag = true;
                                    startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
                                    finish();
                                    break;
                                }
                            }
                            if (!flag) {
                                startActivity(new Intent(getBaseContext(), UserDashboardActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
                    finish();
                }
            }
        }, 1000);
    }
}