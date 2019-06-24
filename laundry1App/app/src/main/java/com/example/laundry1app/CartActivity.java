package com.example.laundry1app;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
class CartData {
    private String english;
    private String quantity;
    private Object price;
    private String service;



    CartData(String english, String quantity, Object price, String service) {
        this.english = english;
        this.quantity = quantity;
        this.price = price;
        this.service=service;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
public class CartActivity extends AppCompatActivity {
    private ProgressBar progress;
    static ArrayList<CartData> data;
    private TextView products;
    private RecyclerView recyclerView;
    private Button checkout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        products = findViewById(R.id.products);
        checkout = findViewById(R.id.checkout);
        data = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    data.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        data.add(new CartData(snapshot.child("english").getValue(String.class), snapshot.child("quantity").getValue(String.class),  snapshot.child("price").getValue(), snapshot.child("service").getValue(String.class)));
                    }
                    progress.setVisibility(View.GONE);
                    if (data.isEmpty()) {
                        products.setVisibility(View.VISIBLE);
                        checkout.setEnabled(false);
                    } else {
                        products.setVisibility(View.GONE);
                        checkout.setEnabled(true);
                        checkout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getBaseContext(), CheckoutActivity.class));
                                finish();
                            }
                        });
                    }
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    recyclerView.setAdapter(new CartAdapter(data));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), UserDashboardActivity.class));
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
class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<CartData> data;
    private DatabaseReference reference;
    private String key;
    //    private float totalPrice;
    CartAdapter(ArrayList<CartData> data) {
        setHasStableIds(true);
        this.data = data;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_cart, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.english.setText(data.get(i).getEnglish());
        viewHolder.service.setText( data.get(i).getService());
        viewHolder.quantity.setText("Quantity: " + data.get(i).getQuantity());
        viewHolder.price.setText("Price: ₹" + data.get(i).getPrice());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("english").getValue(), data.get(i).getEnglish())  && Objects.equals(snapshot.child("quantity").getValue(String.class), data.get(i).getQuantity()) && Objects.equals(snapshot.child("service").getValue(String.class), data.get(i).getService())) {
                                    key = snapshot.getKey();
                                }
                            }
                            if (key != null) {
                                reference.child(key).removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView english,  quantity, price, service;
        private ImageButton delete;
        ViewHolder(View itemView) {
            super(itemView);
            this.english = itemView.findViewById(R.id.english);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.price = itemView.findViewById(R.id.price);
            this.delete = itemView.findViewById(R.id.delete);
            this.service=itemView.findViewById(R.id.service);
        }
    }
}