package com.example.printxpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.model.Tshirt;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TShirtAdapter extends RecyclerView.Adapter<TShirtAdapter.ViewHolder> {

    private List<Tshirt> tshirtList;
    private List<Tshirt> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tshirt item);
        void onOrderClick(Tshirt item);
    }

    public TShirtAdapter(List<Tshirt> tshirtList, OnItemClickListener listener) {
        this.tshirtList = new ArrayList<>(tshirtList);
        this.filteredList = new ArrayList<>(tshirtList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tshirt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tshirt item = filteredList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(item.getTitle());
        holder.tvStyle.setText("Style: " + item.getProductType());
        holder.tvColor.setText("Colour: " + item.getColor());
        holder.tvSize.setText("Size: " + item.getSize());
        holder.tvMaterial.setText("Material: " + item.getMaterial());
        holder.tvPlacement.setText("Placement: " + item.getPrintLocation());
        holder.tvQuality.setText("Quality: " + item.getPrintType());
        holder.tvUnit.setText("Qty: " + item.getQuantity() + " Unit");
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Rs. %.2f", item.getPrice()));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            int resId = context.getResources().getIdentifier(
                    item.getImageUrl(), "drawable", context.getPackageName());
            if (resId != 0) {
                holder.ivTshirtImage.setImageResource(resId);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(item);
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
            filteredList.addAll(tshirtList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Tshirt item : tshirtList) {
                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByPrice(boolean ascending) {
        Collections.sort(filteredList, (o1, o2) -> {
            if (ascending) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            } else {
                return Double.compare(o2.getPrice(), o1.getPrice());
            }
        });
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvStyle, tvColor, tvSize, tvMaterial, tvPlacement, tvQuality, tvUnit, tvPrice;
        public ImageView ivTshirtImage;
        public MaterialButton btnOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.productName);
            tvStyle = itemView.findViewById(R.id.tvStyleLabel);
            tvColor = itemView.findViewById(R.id.tvColorLabel);
            tvSize = itemView.findViewById(R.id.tvSizeLabel);
            tvMaterial = itemView.findViewById(R.id.tvMaterialLabel);
            tvPlacement = itemView.findViewById(R.id.tvPlacementLabel);
            tvQuality = itemView.findViewById(R.id.tvQualityLabel);
            tvUnit = itemView.findViewById(R.id.tvUnitLabel);
            tvPrice = itemView.findViewById(R.id.productPrice);
            ivTshirtImage = itemView.findViewById(R.id.ivTshirtImage);
            btnOrder = itemView.findViewById(R.id.btnOrderTshirt);
        }
    }
}
