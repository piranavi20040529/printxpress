package com.example.printxpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.OrderManagementActivity;
import com.example.printxpress.R;
import com.example.printxpress.model.BusinessCard;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.ViewHolder> {

    private List<BusinessCard> cardList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BusinessCard item);
    }

    public BusinessCardAdapter(List<BusinessCard> cardList, OnItemClickListener listener) {
        this.cardList = cardList;
        this.listener = listener;
    }

    public void filterList(ArrayList<BusinessCard> filteredList) {
        this.cardList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessCard card = cardList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(card.getTitle());
        holder.tvDescription.setText(card.getDescription());
        holder.tvPrice.setText(card.getPrice());
        
        // Set the image from resource ID
        try {
            int resId = Integer.parseInt(card.getImageUrl());
            holder.ivCardImage.setImageResource(resId);
        } catch (Exception e) {
            holder.ivCardImage.setImageResource(R.drawable.ic_template);
        }

        if (holder.tvPopularBadge != null) {
            holder.tvPopularBadge.setVisibility(card.isPopular() ? View.VISIBLE : View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(card);
            }
        });

        holder.btnOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderManagementActivity.class);
            intent.putExtra("PRODUCT_NAME", card.getTitle());
            intent.putExtra("PRODUCT_PRICE", card.getPrice());
            intent.putExtra("CATEGORY", "Business Card");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPrice, tvPopularBadge;
        ImageView ivCardImage;
        MaterialButton btnOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCardTitle);
            tvDescription = itemView.findViewById(R.id.tvCardDescription);
            tvPrice = itemView.findViewById(R.id.tvCardPrice);
            ivCardImage = itemView.findViewById(R.id.ivCardImage);
            btnOrder = itemView.findViewById(R.id.btnOrder);
            tvPopularBadge = itemView.findViewById(R.id.tvPopularBadge);
        }
    }
}
