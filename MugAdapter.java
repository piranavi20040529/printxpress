package com.example.printxpress.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.model.Mug;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MugAdapter extends RecyclerView.Adapter<MugAdapter.MugViewHolder> {

    private List<Mug> mugList;
    private List<Mug> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mug item, int position);
        void onOrderClick(Mug item, int position);
        void onSaveClick(Mug item, int position);
        void onShareClick(Mug item, int position);
    }

    public MugAdapter(List<Mug> mugList, OnItemClickListener listener) {
        this.mugList = new ArrayList<>(mugList);
        this.filteredList = new ArrayList<>(mugList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mug, parent, false);
        return new MugViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MugViewHolder holder, int position) {
        Mug item = filteredList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubDetails.setText("Material: " + item.getMugType());
        holder.tvDescription.setText("Capacity: " + item.getSize());
        holder.tvColorLabel.setText("Colour: " + item.getColor());
        holder.tvSide.setText("Side: " + item.getPrintArea());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Rs. " + String.format("%.2f", item.getPrice()));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    item.getImageUrl(), "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.ivMugImage.setImageResource(resId);
            }
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item, holder.getAdapterPosition());
            }
        });

        if (holder.btnOrder != null) {
            holder.btnOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(item, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query, String type, String size, String color) {
        filteredList.clear();
        String lowerCaseQuery = (query != null) ? query.toLowerCase().trim() : "";

        for (Mug item : mugList) {
            boolean matchesQuery = lowerCaseQuery.isEmpty() ||
                    item.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    item.getMugType().toLowerCase().contains(lowerCaseQuery) ||
                    item.getColor().toLowerCase().contains(lowerCaseQuery);

            boolean matchesType = type == null || type.equals("All") || type.equals("All Types") || item.getMugType().equalsIgnoreCase(type);
            boolean matchesSize = size == null || size.equals("All") || size.equals("All Sizes") || item.getSize().equalsIgnoreCase(size);
            boolean matchesColor = color == null || color.equals("All") || item.getColor().equalsIgnoreCase(color);

            if (matchesQuery && matchesType && matchesSize && matchesColor) {
                filteredList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void sortByPrice(boolean ascending) {
        Collections.sort(filteredList, new Comparator<Mug>() {
            @Override
            public int compare(Mug o1, Mug o2) {
                if (ascending) {
                    return Double.compare(o1.getPrice(), o2.getPrice());
                } else {
                    return Double.compare(o2.getPrice(), o1.getPrice());
                }
            }
        });
        notifyDataSetChanged();
    }

    public void updateList(List<Mug> newList) {
        this.mugList = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class MugViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivMugImage;
        TextView tvTitle, tvSubDetails, tvDescription, tvColorLabel, tvSide, tvQuantity, tvPrice;
        MaterialButton btnOrder;

        public MugViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardMug);
            ivMugImage = itemView.findViewById(R.id.ivMugImage);
            tvTitle = itemView.findViewById(R.id.tvMugTitle);
            tvSubDetails = itemView.findViewById(R.id.tvMugSubDetails);
            tvDescription = itemView.findViewById(R.id.tvMugDescription);
            tvColorLabel = itemView.findViewById(R.id.tvColorLabel);
            tvSide = itemView.findViewById(R.id.tvSide);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvMugPrice);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
