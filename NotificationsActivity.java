package com.example.printxpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.adapter.NotificationAdapter;
import com.example.printxpress.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.notificationsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        

        adapter = new NotificationAdapter(notificationList, notification -> {
            String title = (notification.getTitle() != null) ? notification.getTitle().toLowerCase() : "";
            

            if (title.contains("banner")) {
                startActivity(new Intent(this, BannerActivity.class));
            } else if (title.contains("business card") || title.contains("card")) {
                startActivity(new Intent(this, BusinesscardActivity.class));
            } else if (title.contains("order")) {
                startActivity(new Intent(this, MyOrdersActivity.class));
            } else {
                Toast.makeText(this, "Offer: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);


        fetchNotificationsFromFirebase();
    }


    private void addPromotionalNotifications() {
        notificationList.add(new Notification(
            "Special Offer ",
            "20% off on Business Cards (Min 500 qty). Check it out!", 
            "Today", 
            "OFFER"
        ));

        notificationList.add(new Notification(
            "Free Delivery ",
            "Free Delivery on orders above Rs. 15,000. Shop more, save more!",
            "Ongoing", 
            "OFFER"
        ));
    }


    private void fetchNotificationsFromFirebase() {
        String userId = FirebaseHelper.getCurrentUserId();
        progressBar.setVisibility(View.VISIBLE);


        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            notificationList.clear();
            addPromotionalNotifications();
            adapter.notifyDataSetChanged();
            return;
        }

        FirebaseHelper.getDatabase().child("Notifications").child(userId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    notificationList.clear();
                    addPromotionalNotifications();


                    if (snapshot.exists()) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Notification notification = data.getValue(Notification.class);
                            if (notification != null) {
                                notificationList.add(notification);
                            }
                        }
                    }
                    

                    if (notificationList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
    }
}
