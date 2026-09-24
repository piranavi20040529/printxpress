package com.example.printxpress;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private ImageView productImage;
    private TextView productName, productPrice, detailOldPrice, offerText;
    private TextView txtSpecSize, txtSpecQuality, txtSpecSides, txtSpecQty;
    private MaterialButton btnOrderNow, btnCustomize;
    private String currentProductName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupData();
        setupListeners();
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        productImage = findViewById(R.id.detailProductImage);
        productName = findViewById(R.id.detailProductName);
        productPrice = findViewById(R.id.detailProductPrice);
        detailOldPrice = findViewById(R.id.detailOldPrice);
        offerText = findViewById(R.id.offerText);
        txtSpecSize = findViewById(R.id.txtSpecSize);
        txtSpecQuality = findViewById(R.id.txtSpecQuality);
        txtSpecSides = findViewById(R.id.txtSpecSides);
        txtSpecQty = findViewById(R.id.txtSpecQty);
        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnCustomize = findViewById(R.id.btnCustomize);
    }

    private void setupData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("PRODUCT_NAME");
        String priceStr = intent.getStringExtra("PRODUCT_PRICE_STR");
        int imageRes = intent.getIntExtra("PRODUCT_IMAGE_RES", 0);

        if (name != null) {
            productName.setText(name);
            currentProductName = name;
        }
        if (priceStr != null) productPrice.setText(priceStr);
        if (imageRes != 0) productImage.setImageResource(imageRes);

        updateUIByProduct(name);
    }

    private void updateUIByProduct(String name) {
        if (name == null) return;
        String lowerName = name.toLowerCase();

        if (lowerName.contains("business card")) {
            txtSpecSize.setText("Standard (3.5 * 2.0) inches");
            txtSpecQuality.setText("Premium Shine");
            txtSpecSides.setText("Double Sided");
            txtSpecQty.setText("500 Units");
        }

        else if (lowerName.contains("flyer")) {
            txtSpecSize.setText("A4 Standard");
            txtSpecQuality.setText("Premium Luxury");
            txtSpecSides.setText("Double Sided");
            txtSpecQty.setText("500 Units");
        }

    }


    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());


        btnOrderNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderManagementActivity.class);
            intent.putExtra("product_name", productName.getText().toString());
            intent.putExtra("price", productPrice.getText().toString());
            startActivity(intent);
        });


        btnCustomize.setOnClickListener(v -> {
            String name = currentProductName.toLowerCase();
            Intent intent;
            if (name.contains("card")) intent = new Intent(this, BusinesscardActivity.class);
            else if (name.contains("flyer")) intent = new Intent(this, FlyerActivity.class);
            else if (name.contains("banner")) intent = new Intent(this, BannerActivity.class);
            else if (name.contains("shirt") || name.contains("tee")) intent = new Intent(this, TshirtActivity.class);
            else if (name.contains("mug")) intent = new Intent(this, MugActivity.class);
            else if (name.contains("sticker")) intent = new Intent(this, StickerActivity.class);
            else return;
            startActivity(intent);
        });
    }
}
