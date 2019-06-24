package com.example.laundry1app;


import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;
public class CheckoutActivity extends AppCompatActivity {
    private EditText address;
    private EditText date;
    private DatabaseReference reference;
    private TextView amount;
    private Double sum;
    Button goToCal;
    private String address1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        amount = findViewById(R.id.amount);
        address = findViewById(R.id.address);
        date = findViewById(R.id.date);
        goToCal = findViewById(R.id.getDate);

        Intent incomingIntent = getIntent();
        String d = incomingIntent.getStringExtra("date");
        date.setText(d);
//        calendarView.setFocusedMonthDateColor(Color.RED); // set the red color for the dates of  focused month
//        calendarView.setUnfocusedMonthDateColor(Color.BLUE); // set the yellow color for the dates of an unfocused month
//        calendarView.setSelectedWeekBackgroundColor(Color.RED); // red color for the selected week's background
//        calendarView.setWeekSeparatorLineColor(Color.GREEN);
        goToCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, CalendarActivity.class);
                startActivity(intent);
//                calendarView.setVisibility(View.VISIBLE);
//                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            }

        });

        Button confirm = findViewById(R.id.confirm);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    address1 = dataSnapshot.child("address").getValue(String.class);
                    address.setText(address1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sum = 0.0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        sum = sum +  (snapshot.child("price").getValue(Double.class));
                    }
                    amount.setText(new StringBuilder().append("₹ ").append(sum));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.length() == 0 ) {
                    address.requestFocus();
                    address.setError("This Is A Required Field");
                } else if (date.length() == 0){
                    date.requestFocus();
                    date.setError("This is a required field");
                }
                else {
                    reference = FirebaseDatabase.getInstance().getReference("admins/orders/pending").push();
                    reference.child("user").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child("address").setValue(address.getText().toString());
                    reference.child("date").setValue(date.getText().toString());
                    reference.child("items").setValue(CartActivity.data);
                    FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address").setValue(address.getText().toString());
                    reference.child("price").setValue(Double.parseDouble(amount.getText().toString().replace("₹ ", "")));
                    reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/orders/ongoing").child(Objects.requireNonNull(reference.getKey()));
                    reference.child("address").setValue(address.getText().toString());
                    reference.child("date").setValue(date.getText().toString());
                    reference.child("items").setValue(CartActivity.data);
                    reference.child("price").setValue(Double.parseDouble(amount.getText().toString().replace("₹ ", ""))).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
                                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getBaseContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getBaseContext(), UserDashboardActivity.class));
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), CartActivity.class));
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
}