package com.example.printxpress.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> allOrdersList;
    private List<Order> displayedOrdersList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.allOrdersList = orderList;
        this.displayedOrdersList = new ArrayList<>(orderList);
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        Order currentOrder = displayedOrdersList.get(position);


        String status = currentOrder.getStatus() != null ? currentOrder.getStatus() : "Active";
        holder.orderStatusText.setText(status.toUpperCase());
        holder.orderIdAndDateText.setText("Order #" + currentOrder.getOrderId() + " · " + currentOrder.getDate());
        holder.orderProductDetailsText.setText(currentOrder.getProductName() + " x" + currentOrder.getQuantity());
        holder.orderTotalPriceText.setText("Total: " + currentOrder.getPrice());

        int statusColorValue;
        int cardBackgroundColor;

        switch (status.toLowerCase()) {
            case "active":
            case "approved":
                statusColorValue = Color.parseColor("#166534"); // Green
                cardBackgroundColor = Color.parseColor("#DCFCE7");
                holder.orderActionButtonsLayout.setVisibility(View.VISIBLE);
                break;
            case "rescheduled":
                statusColorValue = Color.parseColor("#0369A1"); // Blue
                cardBackgroundColor = Color.parseColor("#E0F2FE");
                holder.orderActionButtonsLayout.setVisibility(View.VISIBLE);
                break;
            case "pending":
            case "pending approval":
                statusColorValue = Color.parseColor("#92400E"); // Orange
                cardBackgroundColor = Color.parseColor("#FEF3C7");
                holder.orderActionButtonsLayout.setVisibility(View.VISIBLE);
                break;
            case "completed":
                statusColorValue = Color.parseColor("#1E40AF"); // Dark Blue
                cardBackgroundColor = Color.parseColor("#DBEAFE");
                holder.orderActionButtonsLayout.setVisibility(View.GONE);
                break;
            case "cancelled":
            case "rejected":
                statusColorValue = Color.parseColor("#991B1B"); // Red
                cardBackgroundColor = Color.parseColor("#FEE2E2");
                holder.orderActionButtonsLayout.setVisibility(View.GONE);
                break;
            default:
                statusColorValue = Color.GRAY;
                cardBackgroundColor = Color.WHITE;
                holder.orderActionButtonsLayout.setVisibility(View.GONE);
        }


        holder.orderStatusIndicatorView.setBackgroundTintList(ColorStateList.valueOf(statusColorValue));
        holder.orderStatusText.setTextColor(statusColorValue);
        holder.orderMainCardView.setCardBackgroundColor(cardBackgroundColor);
        holder.orderMainCardView.setStrokeColor(statusColorValue);

        holder.orderStatusText.setOnClickListener(v -> {
            PopupMenu statusPopupMenu = new PopupMenu(context, v);
            statusPopupMenu.getMenu().add("Approve");
            statusPopupMenu.getMenu().add("Reject");
            statusPopupMenu.getMenu().add("Active");

            statusPopupMenu.setOnMenuItemClickListener(menuItem -> {
                String newSelectedStatus = menuItem.getTitle().toString();
                FirebaseHelper.getOrdersRef().child(currentOrder.getOrderId()).child("status").setValue(newSelectedStatus)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Status Updated: " + newSelectedStatus, Toast.LENGTH_SHORT).show());
                return true;
            });
            statusPopupMenu.show();
        });


        holder.cancelOrderButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Order?")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        FirebaseHelper.getOrdersRef().child(currentOrder.getOrderId()).child("status").setValue("Cancelled");
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        holder.rescheduleOrderButton.setOnClickListener(v -> {
            Calendar calendarInstance = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, day) -> {
                String newDate = day + "/" + (month + 1) + "/" + year;
                FirebaseHelper.getOrdersRef().child(currentOrder.getOrderId()).child("date").setValue(newDate);
                FirebaseHelper.getOrdersRef().child(currentOrder.getOrderId()).child("status").setValue("Rescheduled")
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Rescheduled to " + newDate, Toast.LENGTH_SHORT).show());
            }, calendarInstance.get(Calendar.YEAR), calendarInstance.get(Calendar.MONTH), calendarInstance.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public int getItemCount() {
        return displayedOrdersList.size();
    }

    public void filter(String searchQuery, String filterStatusName) {
        displayedOrdersList.clear();
        for (Order orderItem : allOrdersList) {
            String currentStatus = orderItem.getStatus() != null ? orderItem.getStatus() : "Active";
            boolean matchesSearchQuery = orderItem.getProductName().toLowerCase().contains(searchQuery.toLowerCase());

            boolean matchesStatusSelection = filterStatusName.equalsIgnoreCase("All") ||
                    (filterStatusName.equalsIgnoreCase("Active") && (currentStatus.equalsIgnoreCase("Active") || currentStatus.toLowerCase().contains("pending") || currentStatus.equalsIgnoreCase("Rescheduled"))) ||
                    currentStatus.equalsIgnoreCase(filterStatusName);

            if (matchesSearchQuery && matchesStatusSelection) {
                displayedOrdersList.add(orderItem);
            }
        }
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderStatusText, orderIdAndDateText, orderProductDetailsText, orderTotalPriceText;
        View orderStatusIndicatorView, orderActionButtonsLayout;
        MaterialButton rescheduleOrderButton, cancelOrderButton;
        MaterialCardView orderMainCardView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatusText = itemView.findViewById(R.id.tvOrderStatus);
            orderIdAndDateText = itemView.findViewById(R.id.tvOrderIdDate);
            orderProductDetailsText = itemView.findViewById(R.id.tvOrderDetails);
            orderTotalPriceText = itemView.findViewById(R.id.tvOrderPrice);
            orderStatusIndicatorView = itemView.findViewById(R.id.viewStatusIndicator);
            orderActionButtonsLayout = itemView.findViewById(R.id.layoutOrderActions);
            rescheduleOrderButton = itemView.findViewById(R.id.btnReschedule);
            cancelOrderButton = itemView.findViewById(R.id.btnCancelOrder);
            orderMainCardView = itemView.findViewById(R.id.cardOrder);
        }
    }
}