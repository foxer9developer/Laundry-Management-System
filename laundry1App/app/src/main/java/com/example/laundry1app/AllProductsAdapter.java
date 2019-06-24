package com.example.laundry1app;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

class AllProductsAdapter extends RecyclerView.Adapter<com.example.laundry1app.AllProductsAdapter.ViewHolder> {
    private ArrayList<AllProductsData> data;
    private DatabaseReference reference, databaseReference;
    private boolean flag;
    private Double p;
    AllProductsAdapter(ArrayList<AllProductsData> data) {
        setHasStableIds(true);
        this.data = data;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_products, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.english.setText(data.get(i).getEnglish());
        viewHolder.quantity.setText(viewHolder.quantity.getText().toString());
        GlideApp.with(viewHolder.image.getContext()).load(data.get(i).getUrl()).into(viewHolder.image);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(viewHolder.spinner.getContext(),R.array.Services, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.spinner.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference("admins");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Objects.equals(snapshot.child("email").getValue(String.class), FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            flag = true;
                            viewHolder.add.setEnabled(false);
                            viewHolder.add.setVisibility(View.GONE);
                            viewHolder.quantity.setEnabled(false);
                            viewHolder.quantity.setVisibility(View.GONE);
                            viewHolder.spinner.setEnabled(false);
                            viewHolder.spinner.setVisibility(View.GONE);

                            break;
                        }
                    }
                    if (!flag) {
                        viewHolder.add.setEnabled(true);
                        viewHolder.add.setVisibility(View.VISIBLE);
                        reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (Objects.equals(snapshot.child("english").getValue(), data.get(i).getEnglish())  && Objects.equals(snapshot.child("quantity").getValue(String.class), viewHolder.quantity.getText().toString())) {
                                        viewHolder.add.setEnabled(false);
                                        viewHolder.add.setText(R.string.added);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        viewHolder.add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (viewHolder.english.length() == 0 ) {
                                    viewHolder.english.requestFocus();
                                    viewHolder.english.setError("This Is A Required Field");
                                } else if (viewHolder.spinner.getSelectedItem() == "Wash" || viewHolder.spinner.getSelectedItem() == "Laundry" || viewHolder.spinner.getSelectedItem() == "Steam Press" || viewHolder.spinner.getSelectedItem() == "Wash + Steam Press"){
                                    viewHolder.spinner.requestFocus();
                                    Toast.makeText(viewHolder.spinner.getContext(), "Select a service", Toast.LENGTH_SHORT).show();
                                }else {
                                    databaseReference = reference.push();
                                    databaseReference.child("english").setValue(data.get(i).getEnglish());
                                    databaseReference.child("quantity").setValue(viewHolder.quantity.getText().toString());
                                    databaseReference.child("image").setValue(data.get(i).getUrl());
                                    databaseReference.child("service").setValue(viewHolder.spinner.getSelectedItem().toString());
                                    if (viewHolder.spinner.getSelectedItem().toString().equals("Wash")) {
                                        p = Double.parseDouble(viewHolder.quantity.getText().toString()) * 5;

                                    } else if (viewHolder.spinner.getSelectedItem().toString().equals("Laundry")) {
                                        p = Double.parseDouble(viewHolder.quantity.getText().toString()) * 8;

                                    } else if (viewHolder.spinner.getSelectedItem().toString().equals("Steam Press")) {
                                        p = Double.parseDouble(viewHolder.quantity.getText().toString()) * 3;

                                    } else if (viewHolder.spinner.getSelectedItem().toString().equals("Wash + Steam Press")) {
                                        p = Double.parseDouble(viewHolder.quantity.getText().toString()) * 7;

                                    }
                                    databaseReference.child("price").setValue(p);
                                }

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView english, quantity;
        private Button add;
        Spinner spinner;
        private ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            this.english = itemView.findViewById(R.id.english);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.add = itemView.findViewById(R.id.add);
            this.spinner = itemView.findViewById(R.id.typeof_service);
            this.image = itemView.findViewById(R.id.img);
        }
    }
}

