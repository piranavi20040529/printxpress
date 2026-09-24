package com.example.printxpress.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private List<Product> fullList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.fullList = new ArrayList<>(productList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(product.getPriceString());

        if (product.getOldPriceString() != null && !product.getOldPriceString().isEmpty()) {
            holder.productOldPrice.setVisibility(View.VISIBLE);
            holder.productOldPrice.setText(product.getOldPriceString());
            holder.productOldPrice.setPaintFlags(holder.productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.productOldPrice.setVisibility(View.GONE);
        }
        
        if (product.getImageResource() != 0) {
            holder.productImage.setImageResource(product.getImageResource());
        } else {
            holder.productImage.setImageResource(R.drawable.ic_template);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        if (holder.btnOrderProduct != null) {
            holder.btnOrderProduct.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newList) {
        this.productList = newList;
        this.fullList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String category) {
        if (category.isEmpty()) {
            productList = new ArrayList<>(fullList);
        } else {
            List<Product> filteredList = new ArrayList<>();
            for (Product product : fullList) {
                if (product.getCategory().equalsIgnoreCase(category)) {
                    filteredList.add(product);
                }
            }
            productList = filteredList;
        }
        notifyDataSetChanged();
    }

    public void search(String query) {
        if (query.isEmpty()) {
            productList = new ArrayList<>(fullList);
        } else {
            List<Product> filteredList = new ArrayList<>();
            for (Product product : fullList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            productList = filteredList;
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice, productOldPrice;
        ImageView productImage;
        View btnOrderProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productOldPrice = itemView.findViewById(R.id.productOldPrice);
            productImage = itemView.findViewById(R.id.productImage);
            btnOrderProduct = itemView.findViewById(R.id.btnOrderProduct);
        }
    }
}
