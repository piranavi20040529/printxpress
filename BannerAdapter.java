package com.example.printxpress.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.model.Banner;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<Banner> bannerList;
    private List<Banner> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Banner item);
        void onOrderClick(Banner item);
    }

    public BannerAdapter(List<Banner> bannerList, OnItemClickListener listener) {
        this.bannerList = new ArrayList<>(bannerList);
        this.filteredList = new ArrayList<>(bannerList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner item = filteredList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSize.setText(" Size: " + item.getSize());
        holder.tvMaterial.setText(" Material: " + item.getMaterial());
        holder.tvLamination.setText(" Lamination: " + item.getLamination());
        holder.tvFinishing.setText(" Finishing: " + item.getFinishing());
        holder.tvQuantity.setText(" Qty: " + item.getQuantity() + " unit");
        holder.tvPrice.setText("Rs. " + (int)item.getPrice());

        try {
            int resId = Integer.parseInt(item.getImageUrl());
            holder.ivBanner.setImageResource(resId);
        } catch (Exception e) {
            holder.ivBanner.setImageResource(R.drawable.ic_template);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnOrder.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(bannerList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Banner item : bannerList) {
                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByPrice(boolean ascending) {
        Collections.sort(filteredList, (o1, o2) -> ascending ? Double.compare(o1.getPrice(), o2.getPrice()) : Double.compare(o2.getPrice(), o1.getPrice()));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView cardView;
        public TextView tvTitle, tvSize, tvMaterial, tvLamination, tvFinishing, tvQuantity, tvPrice;
        public ImageView ivBanner;
        public MaterialButton btnOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardBanner);
            ivBanner = itemView.findViewById(R.id.ivBannerImage);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvSize = itemView.findViewById(R.id.tvBannerSize);
            tvMaterial = itemView.findViewById(R.id.tvBannerMaterial);
            tvLamination = itemView.findViewById(R.id.tvBannerLamination);
            tvFinishing = itemView.findViewById(R.id.tvBannerFinishing);
            tvQuantity = itemView.findViewById(R.id.tvBannerQuantity);
            tvPrice = itemView.findViewById(R.id.tvBannerPrice);
            btnOrder = itemView.findViewById(R.id.btnOrderBanner);
        }
    }
}
