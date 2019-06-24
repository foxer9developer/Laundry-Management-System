package com.example.laundry1app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
public class AuthenticationActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference reference, databaseReference;
    private boolean flag;
    private String key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        progressBar = findViewById(R.id.progress);
        createSignInIntent();
    }
    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), new AuthUI.IdpConfig.PhoneBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setLogo(R.mipmap.laundrylogo).setTosAndPrivacyPolicyUrls("https://example.com/terms.html", "https://example.com/privacy.html").build(), RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        progressBar.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    reference = FirebaseDatabase.getInstance().getReference("admins");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("email").getValue(String.class), FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    flag = true;
                                    key = snapshot.getKey();
                                    databaseReference = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getProviderData()).equals("[google.com]")) {
                                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getBaseContext());
                                        if (acct != null) {
                                            if (acct.getPhotoUrl() != null) {
                                                databaseReference.child("user").setValue(acct.getPhotoUrl().toString());
                                            }
                                        }
                                    }
                                    databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                    databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (!key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                            if (!flag) {
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getProviderData()).equals("[google.com]")) {
                                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getBaseContext());
                                        if (acct != null) {
                                            if (acct.getPhotoUrl() != null) {
                                                reference.child("user").setValue(acct.getPhotoUrl().toString());
                                            }
                                        }
                                    }
                                    reference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                    reference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(getBaseContext(), UserDashboardActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        }
    }
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Click BACK Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}