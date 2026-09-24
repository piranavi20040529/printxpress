package com.example.printxpress;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PublicTrackingActivity extends AppCompatActivity {

    private TextInputEditText etOrderId;
    private MaterialButton btnTrack;
    private MaterialCardView cardResult;
    private TextView tvStatus, tvDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_tracking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        etOrderId = findViewById(R.id.etOrderId);
        btnTrack = findViewById(R.id.btnTrack);
        cardResult = findViewById(R.id.cardResult);
        tvStatus = findViewById(R.id.tvStatus);
        tvDetails = findViewById(R.id.tvDetails);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Public Tracking");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnTrack.setOnClickListener(v -> {
            String orderId = etOrderId.getText().toString().trim();

            if (orderId.startsWith("#")) {
                orderId = orderId.substring(1);
            }

            orderId = orderId.toUpperCase();

            if (TextUtils.isEmpty(orderId)) {
                Toast.makeText(this, "!Please enter an Order ID", Toast.LENGTH_SHORT).show();
            } else {
                trackOrderLive(orderId);
            }
        });
    }

    private void trackOrderLive(String orderId) {
        Toast.makeText(this, "Searching for order...", Toast.LENGTH_SHORT).show();

        FirebaseHelper.getOrdersRef().child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cardResult.setVisibility(View.VISIBLE);

                    String status = snapshot.child("status").getValue(String.class);
                    String productName = snapshot.child("productName").getValue(String.class);

                    if (status == null) status = "Pending Approval";

                    tvStatus.setText("Order Status: " + status);

                    if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Accepted") || status.equalsIgnoreCase("Active")) {
                        tvStatus.setTextColor(ContextCompat.getColor(PublicTrackingActivity.this, android.R.color.holo_green_dark));
                        tvDetails.setText("Great news! Your order for " + productName + " is confirmed and currently " + status.toLowerCase() + ".");
                    } else if (status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Rejected")) {
                        tvStatus.setTextColor(ContextCompat.getColor(PublicTrackingActivity.this, android.R.color.holo_red_dark));
                        tvDetails.setText("Sorry, your order for " + productName + " has been " + status.toLowerCase() + ".");
                    } else if (status.equalsIgnoreCase("Processing") || status.equalsIgnoreCase("Printing")) {
                        tvStatus.setTextColor(ContextCompat.getColor(PublicTrackingActivity.this, android.R.color.holo_blue_dark));
                        tvDetails.setText("Your order for " + productName + " is being printed right now.");
                    } else {
                        tvStatus.setTextColor(ContextCompat.getColor(PublicTrackingActivity.this, android.R.color.darker_gray));
                        tvDetails.setText("Please wait for an update on your order for " + productName + ".");
                    }
                } else {
                    cardResult.setVisibility(View.GONE);
                    Toast.makeText(PublicTrackingActivity.this, "!Order ID not found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PublicTrackingActivity.this, "!Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
