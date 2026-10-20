package com.example.quickcommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import com.bumptech.glide.Glide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickcommerceapplication.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.activities.ProductDetailsActivity;
import com.example.quickcommerceapplication.activities.CartActivity;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.Product;
import com.example.quickcommerceapplication.utils.Constants;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList){
        this.context=context;
        this.productList=productList;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        Button btnAdd;

        ViewHolder(View itemView){
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAdd = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Product p = productList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("₹"+String.format("%.2f",p.getPrice()));
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivProduct);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, p.getId());
            context.startActivity(intent);
        });

        holder.btnAdd.setOnClickListener(v -> {
            try {
                new DatabaseHelper(context).addToCart(p.getId(), p.getName(), p.getPrice(),
                        1, p.getImageUrl());
                Toast.makeText(context, p.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            } catch (SQLiteException exception) {
                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return productList.size();
    }
}
