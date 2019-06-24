package com.example.laundry1app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;
public class MyAccountFragment extends Fragment {
    private EditText name, email, address;
    private DatabaseReference reference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        Button verify = view.findViewById(R.id.verify);
        Button update = view.findViewById(R.id.update);
        Button reset = view.findViewById(R.id.reset);
        Button delete = view.findViewById(R.id.delete);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                address.setText(dataSnapshot.child("address").getValue(String.class));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                verify.setEnabled(false);
                verify.setText("Verified");
            } else {
                verify.setEnabled(true);
                verify.setText("Verify");
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Email Verification Mail Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("name").setValue(name.getText().toString());
                reference.child("email").setValue(email.getText().toString());
                reference.child("address").setValue(address.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Account Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Password Reset Mail Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (getContext() != null) {
                                AuthUI.getInstance().delete(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "User Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getContext(), AuthenticationActivity.class));
                                            Objects.requireNonNull(getActivity()).finish();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        return view;
    }
}