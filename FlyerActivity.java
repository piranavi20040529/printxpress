package com.example.printxpress;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.adapter.FlyerAdapter;
import com.example.printxpress.model.Flyer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlyerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;

    private Toolbar toolbar;
    private RecyclerView rvTemplates;
    private FlyerAdapter adapter;
    private TextView tvEstimatedPrice;
    private ImageView ivMockup, btnFilterTemplates;
    private Button btnGallery, btnCamera, btnOrder, btnComment;
    private RadioGroup rgFlyerSize, rgPaperQuality, rgPrintingOption, rgQuantity;
    private EditText etName, etPhone, etEmail, etAddress, etSpecialRequests, etSearchTemplates;
    private LinearLayout llCustomSizeInputs;
    private EditText etCustomWidth, etCustomHeight;

    private List<Flyer> templatesList = new ArrayList<>();
    private double currentTotalPrice = 0.0;
    private String selectedTemplateTitle = "Custom Flyer";
    private boolean isApplyingTemplate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);
        initViews();
        setupToolbar();
        setupTemplatesData();
        setupTemplatesRecyclerView();
        setupListeners();
        updatePrice();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvTemplates = findViewById(R.id.rvTemplates);
        tvEstimatedPrice = findViewById(R.id.tvEstimatedPrice);
        ivMockup = findViewById(R.id.ivMockup);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        btnOrder = findViewById(R.id.btnOrder);
        btnComment = findViewById(R.id.btnComment);
        rgFlyerSize = findViewById(R.id.rgFlyerSize);
        rgPaperQuality = findViewById(R.id.rgPaperQuality);
        rgPrintingOption = findViewById(R.id.rgPrintingOption);
        rgQuantity = findViewById(R.id.rgQuantity);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        llCustomSizeInputs = findViewById(R.id.llCustomSizeInputs);
        etCustomWidth = findViewById(R.id.etCustomWidth);
        etCustomHeight = findViewById(R.id.etCustomHeight);
        etSearchTemplates = findViewById(R.id.etSearchTemplates);
        btnFilterTemplates = findViewById(R.id.btnFilterTemplates);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Flyers");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTemplatesData() {
        templatesList.clear();
        templatesList.add(new Flyer("1", "Grand Opening", "A5", "Premium Glossy", 10, 2400.0, "", "Portrait", "Single Side", "img11", false));
        templatesList.add(new Flyer("2", "Business Event", "A4", "Professional Matte", 25, 5500.0, "", "Portrait", "Double Side", "img12", false));
        templatesList.add(new Flyer("3", "Music Festival", "Letter", "Eco Recycled", 50, 9500.0, "", "Portrait", "Single Side", "img13", false));
    }

    private void setupTemplatesRecyclerView() {
        adapter = new FlyerAdapter(templatesList, new FlyerAdapter.OnItemClickListener() {
            @Override public void onItemClick(Flyer item) { applyTemplate(item); }
            @Override public void onOrderClick(Flyer item) { applyTemplate(item); }
        });
        rvTemplates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTemplates.setAdapter(adapter);
    }

    private void applyTemplate(Flyer item) {
        isApplyingTemplate = true;
        selectedTemplateTitle = item.getTitle();
        
        if (ivMockup != null) {
            ivMockup.clearColorFilter();
            ivMockup.setImageTintList(null);
            ivMockup.setAlpha(1.0f);
            int resId = getResources().getIdentifier(item.getImageUrl(), "drawable", getPackageName());
            if (resId != 0) {
                ivMockup.setImageResource(resId);
                ivMockup.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }

        if (item.getSize().equalsIgnoreCase("A5")) rgFlyerSize.check(R.id.rbA5);
        else if (item.getSize().equalsIgnoreCase("A4")) rgFlyerSize.check(R.id.rbA4);
        else if (item.getSize().equalsIgnoreCase("Letter")) rgFlyerSize.check(R.id.rbLetter);

        if (item.getPaperQuality().equalsIgnoreCase("Premium Glossy")) rgPaperQuality.check(R.id.rbPremiumGlossy);
        else if (item.getPaperQuality().equalsIgnoreCase("Professional Matte")) rgPaperQuality.check(R.id.rbProfessionalMatte);
        else if (item.getPaperQuality().equalsIgnoreCase("Eco Recycled")) rgPaperQuality.check(R.id.rbEcoRecycled);

        if (item.getPrintingSide().equalsIgnoreCase("Double Side")) rgPrintingOption.check(R.id.rbDoubleSide);
        else rgPrintingOption.check(R.id.rbSingleSide);

        int qty = item.getQuantity();
        if (qty == 1) rgQuantity.check(R.id.rbQty1);
        else if (qty == 10) rgQuantity.check(R.id.rbQty10);
        else if (qty == 25) rgQuantity.check(R.id.rbQty25);
        else if (qty == 50) rgQuantity.check(R.id.rbQty50);
        else if (qty == 100) rgQuantity.check(R.id.rbQty100);
        else if (qty == 1000) rgQuantity.check(R.id.rbQty1000);

        isApplyingTemplate = false;
        updatePrice();
    }

    private void setupListeners() {
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else openCamera();
        });
        btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("CATEGORY", "Flyer");
            intent.putExtra("PREVIOUS_TEXT", etSpecialRequests != null ? etSpecialRequests.getText().toString() : "");
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        btnOrder.setOnClickListener(v -> validateAndProceed());
        RadioGroup.OnCheckedChangeListener priceListener = (g, id) -> {
            if (!isApplyingTemplate) {
                if (id == R.id.rbCustomSize) llCustomSizeInputs.setVisibility(View.VISIBLE);
                else llCustomSizeInputs.setVisibility(View.GONE);
                updatePrice();
            }
        };
        rgFlyerSize.setOnCheckedChangeListener(priceListener);
        rgPaperQuality.setOnCheckedChangeListener(priceListener);
        rgPrintingOption.setOnCheckedChangeListener(priceListener);
        rgQuantity.setOnCheckedChangeListener(priceListener);
        etSearchTemplates.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilterTemplates.setOnClickListener(v -> showFilterMenu());
    }

    private void updatePrice() {
        int sizeId = rgFlyerSize.getCheckedRadioButtonId();
        if (sizeId == -1) { currentTotalPrice = 0.0; updatePriceDisplay(); return; }
        
        double unitPrice = 0.0;
        if (sizeId == R.id.rbA5) unitPrice = 180.0;
        else if (sizeId == R.id.rbA4) unitPrice = 250.0;
        else if (sizeId == R.id.rbLetter) unitPrice = 220.0;
        else unitPrice = 200.0;

        int pqId = rgPaperQuality.getCheckedRadioButtonId();
        if (pqId == R.id.rbPremiumGlossy) unitPrice += 60.0;
        else if (pqId == R.id.rbProfessionalMatte) unitPrice += 90.0;
        else if (pqId == R.id.rbPremiumLuxury) unitPrice += 120.0;

        if (rgPrintingOption.getCheckedRadioButtonId() == R.id.rbDoubleSide) unitPrice += 80.0;

        currentTotalPrice = unitPrice * getSelectedQuantity();
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        String priceText = String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f", currentTotalPrice);
        if (currentTotalPrice >= 15000) {
            tvEstimatedPrice.setTextColor(Color.parseColor("#2E7D32"));
            priceText += "\nFREE DELIVERY APPLIED";
        } else {
            tvEstimatedPrice.setTextColor(Color.BLACK);
        }
        tvEstimatedPrice.setText(priceText);
    }

    private int getSelectedQuantity() {
        int id = rgQuantity.getCheckedRadioButtonId();
        if (id == R.id.rbQty10) return 10;
        else if (id == R.id.rbQty25) return 25;
        else if (id == R.id.rbQty50) return 50;
        else if (id == R.id.rbQty100) return 100;
        else if (id == R.id.rbQty1000) return 1000;
        return 1;
    }

    private void openCamera() {
        try { startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE); }
        catch (Exception e) { Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show(); }
    }

    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnFilterTemplates);
        popupMenu.getMenu().add("Price: Low to High");
        popupMenu.getMenu().add("Price: High to Low");
        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();
            if (adapter != null) {
                if (selected.equals("Price: Low to High")) adapter.sortByPrice(true);
                else if (selected.equals("Price: High to Low")) adapter.sortByPrice(false);
            }
            return true;
        });
        popupMenu.show();
    }

    private void validateAndProceed() {
        if (currentTotalPrice <= 0) { Toast.makeText(this, "Please select flyer options", Toast.LENGTH_SHORT).show(); return; }
        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "Flyer: " + selectedTemplateTitle);
        intent.putExtra("total_price", currentTotalPrice);
        intent.putExtra("cust_name", etName.getText().toString());
        intent.putExtra("cust_phone", etPhone.getText().toString());
        intent.putExtra("cust_email", etEmail.getText().toString());
        intent.putExtra("cust_address", etAddress.getText().toString());
        intent.putExtra("special_req", etSpecialRequests.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (ivMockup != null) { 
                ivMockup.clearColorFilter(); 
                ivMockup.setImageTintList(null);
                ivMockup.setAlpha(1.0f); 
                ivMockup.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            if (requestCode == GALLERY_REQUEST_CODE) { if (data.getData() != null) ivMockup.setImageURI(data.getData()); }
            else if (requestCode == CAMERA_REQUEST_CODE) { if (data.getExtras() != null) { ivMockup.setImageBitmap((Bitmap) data.getExtras().get("data")); } }
            else if (requestCode == COMMENT_REQUEST_CODE) { etSpecialRequests.setText(data.getStringExtra("INSTRUCTIONS")); }
        }
    }
}
