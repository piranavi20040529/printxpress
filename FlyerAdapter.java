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
import com.example.printxpress.model.Flyer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlyerAdapter extends RecyclerView.Adapter<FlyerAdapter.ViewHolder> {

    private List<Flyer> flyerList;
    private List<Flyer> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Flyer item);
        void onOrderClick(Flyer item);
    }

    public FlyerAdapter(List<Flyer> flyerList, OnItemClickListener listener) {
        this.flyerList = new ArrayList<>(flyerList);
        this.filteredList = new ArrayList<>(flyerList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flyer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flyer item = filteredList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(item.getTitle());
        holder.tvSize.setText(" Size: " + item.getSize());
        holder.tvPaperInfo.setText(" Paper: " + item.getPaperType());
        holder.tvSubDetails.setText(" Side: " + item.getPrintingType());
        holder.tvQuantity.setText(" Qty: " + item.getQuantity() + " units");
        holder.tvPrice.setText("Rs. " + (int)item.getPrice());

        if (item.getImageUrl() != null) {
            int resId = context.getResources().getIdentifier(item.getImageUrl(), "drawable", context.getPackageName());
            if (resId != 0) {
                holder.ivFlyer.setImageResource(resId);
            } else {
                holder.ivFlyer.setImageResource(R.drawable.ic_template);
            }
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
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
            filteredList.addAll(flyerList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Flyer item : flyerList) {
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
        public TextView tvTitle, tvSubDetails, tvSize, tvPaperInfo, tvQuantity, tvPrice;
        public ImageView ivFlyer;
        public MaterialButton btnOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardFlyer);
            ivFlyer = itemView.findViewById(R.id.ivFlyerImage);
            tvTitle = itemView.findViewById(R.id.tvFlyerTitle);
            tvSubDetails = itemView.findViewById(R.id.tvFlyerSubDetails);
            tvSize = itemView.findViewById(R.id.tvFlyerSize);
            tvPaperInfo = itemView.findViewById(R.id.tvFlyerPaper);
            tvQuantity = itemView.findViewById(R.id.tvFlyerQuantity);
            tvPrice = itemView.findViewById(R.id.tvFlyerPrice);
            btnOrder = itemView.findViewById(R.id.btnOrderFlyer);
        }
    }
}
