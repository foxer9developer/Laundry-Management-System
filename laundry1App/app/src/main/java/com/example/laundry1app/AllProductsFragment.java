package com.example.laundry1app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
public class AllProductsFragment extends Fragment {
    private ProgressBar progress;
    private ArrayList<AllProductsData> data;
    private TextView products;
    private View view;
    private RecyclerView recyclerView;
    private FloatingActionButton add, cart;
    private boolean flag;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_products, container, false);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        add = view.findViewById(R.id.add);
        cart = view.findViewById(R.id.cart);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Objects.equals(snapshot.child("email").getValue(String.class), FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            flag = true;
                            add.show();
                            add.setEnabled(true);
                            cart.hide();
                            cart.setEnabled(false);
                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getFragmentManager() != null) {
                                        startActivity(new Intent(getContext(), AddProductActivity.class));
                                        Objects.requireNonNull(getActivity()).finish();
                                    }
                                }
                            });
                            break;
                        }
                    }
                    if (!flag) {
                        add.hide();
                        add.setEnabled(false);
                        cart.show();
                        cart.setEnabled(true);
                        cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getContext(), CartActivity.class));
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        products = view.findViewById(R.id.products);
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    data.add(new AllProductsData(snapshot.child("english").getValue(String.class), snapshot.child("url").getValue(String.class)));

                }
                progress.setVisibility(View.GONE);
                if (data.isEmpty()) {
                    products.setVisibility(View.VISIBLE);
                } else {
                    products.setVisibility(View.GONE);
                }//
                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new AllProductsAdapter(data));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}