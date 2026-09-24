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
import com.example.printxpress.model.Sticker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private List<Sticker> stickerList;
    private List<Sticker> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sticker item, int position);
        void onOrderClick(Sticker item, int position);
    }

    public StickerAdapter(List<Sticker> stickerList, OnItemClickListener listener) {
        this.stickerList = new ArrayList<>(stickerList);
        this.filteredList = new ArrayList<>(this.stickerList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        Sticker item = filteredList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(item.getTitle());
        holder.tvMaterial.setText("Material: " + item.getMaterial());
        holder.tvShape.setText("Shape: " + item.getShape());
        holder.tvFinish.setText("Finish: " + item.getFinish());

        String sizeDisplay = String.format("%.1f * %.1f", item.getWidth(), item.getHeight());
        holder.tvSize.setText("Size: " + sizeDisplay);
        
        holder.tvPrice.setText("Rs. " + String.format("%.0f", item.getPrice()));
        holder.tvQuantity.setText("Qty: " + item.getQuantity() + " Units");

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            int resId = context.getResources().getIdentifier(
                    item.getImageUrl(), "drawable", context.getPackageName());
            if (resId != 0) {
                holder.ivSticker.setImageResource(resId);
            } else {
                holder.ivSticker.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.ivSticker.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item, holder.getAdapterPosition());
            }
        });

        holder.btnOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(item, holder.getAdapterPosition());
            } else {
                Intent intent = new Intent(context, OrderManagementActivity.class);
                intent.putExtra("PRODUCT_NAME", item.getTitle());
                intent.putExtra("PRODUCT_PRICE", String.valueOf(item.getPrice()));
                intent.putExtra("CATEGORY", "Sticker");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(stickerList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Sticker item : stickerList) {
                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByPrice(boolean lowToHigh) {
        Collections.sort(filteredList, new Comparator<Sticker>() {
            @Override
            public int compare(Sticker s1, Sticker s2) {
                if (lowToHigh) {
                    return Double.compare(s1.getPrice(), s2.getPrice());
                } else {
                    return Double.compare(s2.getPrice(), s1.getPrice());
                }
            }
        });
        notifyDataSetChanged();
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivSticker;
        TextView tvTitle, tvMaterial, tvShape, tvFinish, tvSize, tvPrice, tvQuantity;
        MaterialButton btnOrder;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardSticker);
            ivSticker = itemView.findViewById(R.id.ivSticker);
            tvTitle = itemView.findViewById(R.id.tvStickerTitle);
            tvMaterial = itemView.findViewById(R.id.tvMaterial);
            tvShape = itemView.findViewById(R.id.tvShape);
            tvFinish = itemView.findViewById(R.id.tvFinish);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvStickerPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnOrder = itemView.findViewById(R.id.btnOrderSticker);
        }
    }
}
