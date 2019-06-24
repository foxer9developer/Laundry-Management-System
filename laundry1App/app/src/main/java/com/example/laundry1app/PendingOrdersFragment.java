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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
class PendingData {
    private String english,  quantity;
    private Object price;
    PendingData(String english,  String quantity, Object price) {
        this.english = english;
        this.quantity = quantity;
        this.price = price;
    }
    String getEnglish() {
        return english;
    }
    void setEnglish(String english) {
        this.english = english;
    }
    String getQuantity() {
        return quantity;
    }
    void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    Object getPrice() {
        return price;
    }
    void setPrice(Object price) {
        this.price = price;
    }

}
class PendingOrdersData {
    private String address, itemKey, user, date;
    private Object items, item, price;
    PendingOrdersData(String address, Object items, Object item, Object price, String itemKey, String user, String date) {
        this.address = address;
        this.items = items;
        this.item = item;
        this.price = price;
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
public class PendingOrdersFragment extends Fragment {
    private ProgressBar progress;
    private ArrayList<PendingOrdersData> data;
    private ArrayList<String> items;
    static ArrayList<PendingData> item;
    private TextView orders;
    private View view;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pending_orders, container, false);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        orders = view.findViewById(R.id.orders);
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins/orders/pending");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    items = new ArrayList<>();
                    item = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.child("items").getChildren()) {
                        items.add(snapshot1.child("english").getValue(String.class) + " (" + snapshot1.child("quantity").getValue() + ")");
                        item.add(new PendingData(snapshot1.child("english").getValue(String.class), String.valueOf(snapshot1.child("quantity").getValue()), snapshot1.child("price").getValue()));
                    }
                    data.add(new PendingOrdersData(snapshot.child("address").getValue(String.class), items, item, snapshot.child("price").getValue(), snapshot.getKey(), snapshot.child("user").getValue(String.class), snapshot.child("date").getValue(String.class)));
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
                recyclerView.setAdapter(new PendingOrdersAdapter(data));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.ViewHolder> {
    private DatabaseReference reference, databaseReference;
    private ArrayList<PendingOrdersData> data;
    PendingOrdersAdapter(ArrayList<PendingOrdersData> data) {
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
        viewHolder.completed.setEnabled(true);
        viewHolder.completed.setText(R.string.complete);
        viewHolder.completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("admins/orders/completed").push();
                reference.child("address").setValue(data.get(i).getAddress());
                reference.child("price").setValue(data.get(i).getPrice());
                reference.child("user").setValue(data.get(i).getUser());
                databaseReference = reference.child("items");
                databaseReference.setValue(data.get(i).getItem());
                reference = FirebaseDatabase.getInstance().getReference("users/" + data.get(i).getUser() + "/orders/previous").child(Objects.requireNonNull(reference.getKey()));
                reference.child("address").setValue(data.get(i).getAddress());
                reference.child("price").setValue(data.get(i).getPrice());
                databaseReference = reference.child("items");
                databaseReference.setValue(data.get(i).getItem());
                FirebaseDatabase.getInstance().getReference("admins/orders/pending").child(data.get(i).getItemKey()).removeValue();
                FirebaseDatabase.getInstance().getReference("users/" + data.get(i).getUser() +"/orders/ongoing").child(data.get(i).getItemKey()).removeValue();
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address, items, price, date;
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