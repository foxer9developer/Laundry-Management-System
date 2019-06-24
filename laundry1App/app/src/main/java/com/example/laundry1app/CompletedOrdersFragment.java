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
class CompletedOrdersData {
    private String address, date;
    private Object items, price;
    CompletedOrdersData(String address, Object items, Object price, String date) {
        this.address = address;
        this.items = items;
        this.price = price;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }
}
public class CompletedOrdersFragment extends Fragment {
    private ProgressBar progress;
    private ArrayList<CompletedOrdersData> data;
    private TextView orders;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<String> items;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_completed_orders, container, false);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        orders = view.findViewById(R.id.orders);
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins/orders/completed");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    items = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.child("items").getChildren()) {
                        items.add(snapshot1.child("english").getValue(String.class) + " (" + snapshot1.child("quantity").getValue() + ")");
                    }
                    data.add(new CompletedOrdersData(snapshot.child("address").getValue(String.class), items, snapshot.child("price").getValue(), snapshot.child("date").getValue(String.class)));
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
                recyclerView.setAdapter(new CompletedOrdersAdapter(data));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.ViewHolder> {
    private ArrayList<CompletedOrdersData> data;
    CompletedOrdersAdapter(ArrayList<CompletedOrdersData> data) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.address.setText(data.get(i).getAddress());
        viewHolder.date.setText(data.get(i).getDate());
        viewHolder.items.setText(String.valueOf(data.get(i).getItems()));
        viewHolder.price.setText(new StringBuilder().append("₹ ").append(data.get(i).getPrice()));
        viewHolder.completed.setEnabled(false);
        viewHolder.completed.setText(R.string.completed);
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