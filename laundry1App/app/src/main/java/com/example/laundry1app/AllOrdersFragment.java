package com.example.laundry1app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

class AllData {
    private String english,  quantity, date;
//    private Object price;
    AllData(String english, String quantity ) {
        this.english = english;
        this.quantity = quantity;
//        this.date = date;
//        this.price = price;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
class AllOrdersData {
    private String address, key, itemKey, user, date;
    private Object items, item, price;
    AllOrdersData(String address, Object price, Object items, Object item,  String key, String itemKey, String user, String date) {
        this.address = address;
        this.items = items;
        this.item = item;
        this.price = price;
        this.key = key;
        this.itemKey = itemKey;
        this.user = user;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getItems() {
        return items;
    }

    public void setItems(Object items) {
        this.items = items;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }
}
public class AllOrdersFragment extends Fragment {
    private ProgressBar progress;
    private ArrayList<AllOrdersData> data;
    private ArrayList<String> items;
    static ArrayList<AllData> item;
    private TextView orders;
    private View view;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_orders, container, false);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        orders = view.findViewById(R.id.orders);
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins/orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        items = new ArrayList<>();
                        item = new ArrayList<>();
                        for (DataSnapshot snapshot2 : snapshot1.child("items").getChildren()) {
                            items.add(snapshot2.child("english").getValue(String.class) + " (" + snapshot2.child("quantity").getValue() + ")");
                            item.add(new AllData(snapshot2.child("english").getValue(String.class), String.valueOf(snapshot2.child("quantity").getValue())));
                        }
                        data.add(new AllOrdersData(snapshot1.child("address").getValue(String.class), snapshot1.child("price").getValue(), items, item, snapshot.getKey(), snapshot1.getKey(), snapshot1.child("user").getValue(String.class), snapshot1.child("date").getValue(String.class)));
                    }
                }
                progress.setVisibility(View.GONE);
                if (data.isEmpty()) {
                    orders.setVisibility(View.VISIBLE);
                } else {
                    orders.setVisibility(View.GONE);
                }
                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new AllOrdersAdapter(data));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.ViewHolder> {
    private DatabaseReference reference, databaseReference;
    private ArrayList<AllOrdersData> data;
    AllOrdersAdapter(ArrayList<AllOrdersData> data) {
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
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_orders, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.address.setText(data.get(i).getAddress());
        viewHolder.date.setText(data.get(i).getDate());
        viewHolder.items.setText(String.valueOf(data.get(i).getItems()));
        viewHolder.price.setText(new StringBuilder().append("₹ ").append(data.get(i).getPrice()));
        if (data.get(i).getKey().equals("completed")) {
            viewHolder.completed.setEnabled(false);
            viewHolder.completed.setText(R.string.completed);
        } else if (data.get(i).getKey().equals("pending")) {
            viewHolder.completed.setEnabled(true);
            viewHolder.completed.setText(R.string.complete);
            viewHolder.completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference = FirebaseDatabase.getInstance().getReference("admins/orders/completed").push();
                    reference.child("address").setValue(data.get(i).getAddress());
                    reference.child("date").setValue(data.get(i).getDate());
                    reference.child("price").setValue(data.get(i).getPrice());
                    reference.child("user").setValue(data.get(i).getUser());
                    databaseReference = reference.child("items");
                    databaseReference.setValue(data.get(i).getItem());
                    reference = FirebaseDatabase.getInstance().getReference("users/" + data.get(i).getUser() + "/orders/previous").child(Objects.requireNonNull(reference.getKey()));
                    reference.child("address").setValue(data.get(i).getAddress());
                    reference.child("date").setValue(data.get(i).getDate());
                    reference.child("price").setValue(data.get(i).getPrice());
                    databaseReference = reference.child("items");
                    databaseReference.setValue(data.get(i).getItem());
                    FirebaseDatabase.getInstance().getReference("admins/orders/pending").child(data.get(i).getItemKey()).removeValue();
                    FirebaseDatabase.getInstance().getReference("users/" + data.get(i).getUser() +"/orders/ongoing").child(data.get(i).getItemKey()).removeValue();
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address, items, date, price;
        private Button completed;
        ViewHolder(View itemView) {
            super(itemView);
            this.address = itemView.findViewById(R.id.address);
            this.items = itemView.findViewById(R.id.items);
            this.price = itemView.findViewById(R.id.price);
            this.completed = itemView.findViewById(R.id.completed);
            this.date = itemView.findViewById(R.id.date);
        }
    }
}