package com.example.laundry1app;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

class dryCleanData {
    private String english, url;

    dryCleanData(String english, String url) {
        this.english = english;
        this.url = url;

    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

public class drycleanFragment extends Fragment {
    private ProgressBar progress;
    private ArrayList<dryCleanData> data;
    private TextView dryclean;
    private View view;
    private RecyclerView recyclerView;
    private FloatingActionButton add, cart;
    private boolean flag;
    @Nullable
    @Override//
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dryclean, container, false);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        add = view.findViewById(R.id.add);
        cart = view.findViewById(R.id.cart);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        add.setEnabled(false);//
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
        dryclean = view.findViewById(R.id.dryclean);
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dryclean");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    data.add(new dryCleanData(snapshot.child("english").getValue(String.class), snapshot.child("url").getValue(String.class)));

                }
                progress.setVisibility(View.GONE);
                if (data.isEmpty()) {
                    dryclean.setVisibility(View.VISIBLE);
                } else {
                    dryclean.setVisibility(View.GONE);
                }//
                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new drycleanAdapter(data));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
class drycleanAdapter extends RecyclerView.Adapter<com.example.laundry1app.drycleanAdapter.ViewHolder> {
    private ArrayList<dryCleanData> data;
    private DatabaseReference reference, databaseReference;
    private boolean flag;
    private Double p;
    drycleanAdapter(ArrayList<dryCleanData> data) {
        setHasStableIds(true);
        this.data = data;
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_dryclean, viewGroup, false));
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
                                    databaseReference.child("service").setValue(viewHolder.spinner.getSelectedItem().toString());
                                    databaseReference.child("image").setValue(data.get(i).getUrl());
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
            this.image = itemView.findViewById(R.id.img);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.add = itemView.findViewById(R.id.add);
            this.spinner = itemView.findViewById(R.id.typeof_service);
        }
    }
}



