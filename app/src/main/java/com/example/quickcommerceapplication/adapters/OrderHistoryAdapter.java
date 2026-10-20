package com.example.quickcommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.activities.OrderDetailsActivity;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final Context context;
    private final List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList){
        this.context=context;
        this.orderList =orderList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderTotal;
        ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Order #"+ order.getId());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTotal.setText("Total: ₹" + String.format("%.2f", order.getTotalAmount()));

        // Format Epoch milliseconds timestamp to readable date string
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(order.getOrderDate()));
        holder.tvOrderDate.setText("Date: " + formattedDate);

        // Explicit Intent to open live status in OrderDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
