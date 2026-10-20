package com.example.quickcommerceapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Holder> {
    public interface Listener {
        void quantityChanged(CartItem item, int quantity);
        void removed(CartItem item);
    }

    private final List<CartItem> items;
    private final Listener listener;

    public CartAdapter(List<CartItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.price.setText(String.format("₹%.2f", item.getPrice() * item.getQuantity()));
        holder.plus.setOnClickListener(v -> listener.quantityChanged(item, item.getQuantity() + 1));
        holder.minus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) listener.quantityChanged(item, item.getQuantity() - 1);
        });
        holder.remove.setOnClickListener(v -> listener.removed(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, quantity, price, plus, minus, remove;

        Holder(View view) {
            super(view);
            name = view.findViewById(R.id.tvCartName);
            quantity = view.findViewById(R.id.tvCartQuantity);
            price = view.findViewById(R.id.tvCartPrice);
            plus = view.findViewById(R.id.btnCartPlus);
            minus = view.findViewById(R.id.btnCartMinus);
            remove = view.findViewById(R.id.btnCartRemove);
        }
    }
}

