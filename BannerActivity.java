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
import com.example.printxpress.adapter.BannerAdapter;
import com.example.printxpress.model.Banner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BannerActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;
    
    private Toolbar toolbar;
    private EditText etSearch, etCustomWidth, etCustomHeight;
    private EditText etFullName, etPhoneNumber, etAddress, etEmailAddress, etBannerDescription;
    private Button btnGallery, btnCamera, btnContinue, btnComment;
    private TextView tvEstimatedPrice;
    private ImageView ivMockup, btnFilter;
    private RecyclerView rvTemplates;
    private View layoutCustomSize;
    private RadioGroup rgBannerStyle, rgMaterial, rgLamination, rgFinishing, rgQuantity;

    private double totalPrice = 0;
    private List<Banner> fullBannerList = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private String selectedTemplateName = "Custom Banner";
    private boolean isApplyingTemplate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initViews();
        setupToolbar();
        setupTemplatesData();
        setupRecyclerView();
        setupListeners();
        updatePrice();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearchTemplates);
        btnFilter = findViewById(R.id.btnFilterTemplates);
        etCustomWidth = findViewById(R.id.etCustomWidth);
        etCustomHeight = findViewById(R.id.etCustomHeight);
        layoutCustomSize = findViewById(R.id.layoutCustomSize);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etBannerDescription = findViewById(R.id.etBannerDescription);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        btnContinue = findViewById(R.id.btnContinue);
        btnComment = findViewById(R.id.btnComment);
        tvEstimatedPrice = findViewById(R.id.tvEstimatedPrice);
        ivMockup = findViewById(R.id.ivMockup);
        rvTemplates = findViewById(R.id.rvTemplates);
        rgBannerStyle = findViewById(R.id.rgBannerStyle);
        rgMaterial = findViewById(R.id.rgMaterial);
        rgLamination = findViewById(R.id.rgLamination);
        rgFinishing = findViewById(R.id.rgFinishing);
        rgQuantity = findViewById(R.id.rgQuantity);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Banner Design");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTemplatesData() {
        fullBannerList.clear();
        fullBannerList.add(new Banner("1", "Grand Opening", "Vinyl", "Medium (6*3ft)", 10, 8100.0, "",
                "Matte", "Grommets", String.valueOf(R.drawable.img14), false));
        fullBannerList.add(new Banner("2", "Shop Promo", "Fabric", "Large (10*5ft)", 50, 114500.0, "",
                "Glossy", "Pole Pockets", String.valueOf(R.drawable.img15), false));
        fullBannerList.add(new Banner("3", "Event Showcase", "Mesh", "Small (3*2ft)", 1, 540.0, "",
                "Matte", "Hemmed Edges", String.valueOf(R.drawable.img16), false));
    }

    private void setupRecyclerView() {
        bannerAdapter = new BannerAdapter(fullBannerList, new BannerAdapter.OnItemClickListener() {
            @Override public void onItemClick(Banner item) { applyTemplate(item); }
            @Override public void onOrderClick(Banner item) { applyTemplate(item); }
        });
        rvTemplates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTemplates.setAdapter(bannerAdapter);
    }

    private void applyTemplate(Banner item) {
        isApplyingTemplate = true;
        selectedTemplateName = item.getTitle();
        
        if (ivMockup != null) {
            ivMockup.clearColorFilter();
            ivMockup.setImageTintList(null);
            ivMockup.setAlpha(1.0f);
            try {
                int resId = Integer.parseInt(item.getImageUrl());
                ivMockup.setImageResource(resId);
                ivMockup.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception ignored) {}
        }

        if (item.getSize().contains("3*2")) rgBannerStyle.check(R.id.rbSmall);
        else if (item.getSize().contains("6*3")) rgBannerStyle.check(R.id.rbMedium);
        else if (item.getSize().contains("10*5")) rgBannerStyle.check(R.id.rbLarge);

        if (item.getMaterial().equalsIgnoreCase("Vinyl")) rgMaterial.check(R.id.rbVinyl);
        else if (item.getMaterial().equalsIgnoreCase("Fabric")) rgMaterial.check(R.id.rbFabric);
        else if (item.getMaterial().equalsIgnoreCase("Mesh")) rgMaterial.check(R.id.rbMesh);

        if (item.getLamination().equalsIgnoreCase("Matte")) rgLamination.check(R.id.rbMatte);
        else if (item.getLamination().equalsIgnoreCase("Glossy")) rgLamination.check(R.id.rbGlossy);
        else rgLamination.check(R.id.rbNone);

        if (item.getFinishing().equalsIgnoreCase("Grommets")) rgFinishing.check(R.id.rbGrommets);
        else if (item.getFinishing().equalsIgnoreCase("Pole Pockets")) rgFinishing.check(R.id.rbPolePockets);
        else if (item.getFinishing().equalsIgnoreCase("Hemmed Edges")) rgFinishing.check(R.id.rbHemmedEdges);

        int qty = item.getQuantity();
        if (qty == 1) rgQuantity.check(R.id.rbQty1);
        else if (qty == 10) rgQuantity.check(R.id.rbQty10);
        else if (qty == 25) rgQuantity.check(R.id.rbQty25);
        else if (qty == 50) rgQuantity.check(R.id.rbQty50);
        else if (qty == 100) rgQuantity.check(R.id.rbQty100);
        else if (qty == 1000) rgQuantity.check(R.id.rbQty1000);

        isApplyingTemplate = false;
        updatePrice();
        Toast.makeText(this, "Template applied: " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void setupListeners() {
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        btnCamera.setOnClickListener(v -> checkPermissionAndOpenCamera());
        btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("CATEGORY", "Banner");
            intent.putExtra("PREVIOUS_TEXT", etBannerDescription != null ? etBannerDescription.getText().toString() : "");
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });

        RadioGroup.OnCheckedChangeListener priceListener = (group, checkedId) -> {
            if (!isApplyingTemplate) {
                if (group.getId() == R.id.rgBannerStyle) {
                    layoutCustomSize.setVisibility(checkedId == R.id.rbCustom ? View.VISIBLE : View.GONE);
                }
                updatePrice();
            }
        };
        rgBannerStyle.setOnCheckedChangeListener(priceListener);
        rgMaterial.setOnCheckedChangeListener(priceListener);
        rgLamination.setOnCheckedChangeListener(priceListener);
        rgFinishing.setOnCheckedChangeListener(priceListener);
        rgQuantity.setOnCheckedChangeListener(priceListener);

        TextWatcher priceTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { if (!isApplyingTemplate) updatePrice(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etCustomWidth.addTextChangedListener(priceTextWatcher);
        etCustomHeight.addTextChangedListener(priceTextWatcher);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bannerAdapter != null) bannerAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilter.setOnClickListener(v -> showFilterMenu());
        btnContinue.setOnClickListener(v -> validateAndOrder());
    }

    private void updatePrice() {
        int styleId = rgBannerStyle.getCheckedRadioButtonId();
        if (styleId == -1) { totalPrice = 0.0; updatePriceDisplay(); return; }
        double unitPrice = 0.0;
        if (styleId == R.id.rbSmall) unitPrice = 200.0;
        else if (styleId == R.id.rbMedium) unitPrice = 700.0;
        else if (styleId == R.id.rbLarge) unitPrice = 2000.0;
        else if (styleId == R.id.rbCustom) {
            double w = 0, h = 0;
            try { w = Double.parseDouble(etCustomWidth.getText().toString()); h = Double.parseDouble(etCustomHeight.getText().toString()); } catch (Exception ignored) {}
            if (w > 0 && h > 0) unitPrice = (w * h) * 40.0;
            else unitPrice = 500.0;
        }
        int matId = rgMaterial.getCheckedRadioButtonId();
        if (matId == R.id.rbFabric) unitPrice += 150.0;
        else if (matId == R.id.rbMesh) unitPrice += 250.0;
        else if (matId == R.id.rbVinyl) unitPrice += 30.0;
        int lamId = rgLamination.getCheckedRadioButtonId();
        if (lamId == R.id.rbMatte) unitPrice += 60.0;
        else if (lamId == R.id.rbGlossy) unitPrice += 100.0;
        int finId = rgFinishing.getCheckedRadioButtonId();
        if (finId == R.id.rbGrommets) unitPrice += 20.0;
        else if (finId == R.id.rbPolePockets) unitPrice += 40.0;
        else if (finId == R.id.rbHemmedEdges) unitPrice += 30.0;
        totalPrice = unitPrice * getSelectedQuantity();
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        String priceText = String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f", totalPrice);
        if (totalPrice >= 15000) {
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
        else if (id == R.id.rbQty50) return 50;
        else if (id == R.id.rbQty100) return 100;
        else if (id == R.id.rbQty1000) return 1000;
        return 1;
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else openCamera();
    }

    private void openCamera() {
        try { startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST); }
        catch (Exception e) { Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show(); }
    }

    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnFilter);
        popupMenu.getMenu().add("Price: Low to High");
        popupMenu.getMenu().add("Price: High to Low");
        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();
            if (selected.equals("Price: Low to High")) {
                if (bannerAdapter != null) bannerAdapter.sortByPrice(true);
            } else if (selected.equals("Price: High to Low")) {
                if (bannerAdapter != null) bannerAdapter.sortByPrice(false);
            }
            return true;
        });
        popupMenu.show();
    }

    private void validateAndOrder() {
        int sId = rgBannerStyle.getCheckedRadioButtonId();
        int mId = rgMaterial.getCheckedRadioButtonId();
        int qId = rgQuantity.getCheckedRadioButtonId();
        if (sId == -1 || mId == -1 || qId == -1) { Toast.makeText(this, "Please select options", Toast.LENGTH_SHORT).show(); return; }
        RadioButton rbSize = findViewById(sId);
        RadioButton rbMaterial = findViewById(mId);
        RadioButton rbQty = findViewById(qId);
        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "Banner: " + selectedTemplateName);
        intent.putExtra("total_price", totalPrice);
        intent.putExtra("quantity", rbQty.getText().toString());
        intent.putExtra("material", rbMaterial.getText().toString());
        intent.putExtra("size", rbSize.getText().toString());
        intent.putExtra("cust_name", etFullName.getText().toString());
        intent.putExtra("cust_phone", etPhoneNumber.getText().toString());
        intent.putExtra("cust_email", etEmailAddress.getText().toString());
        intent.putExtra("cust_address", etAddress.getText().toString());
        intent.putExtra("special_req", etBannerDescription.getText().toString());
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
            if (requestCode == PICK_IMAGE_REQUEST) { if (data.getData() != null) ivMockup.setImageURI(data.getData()); }
            else if (requestCode == CAMERA_REQUEST) { if (data.getExtras() != null) { if (data.getExtras().get("data") != null) ivMockup.setImageBitmap((Bitmap) data.getExtras().get("data")); } }
            else if (requestCode == COMMENT_REQUEST_CODE) { etBannerDescription.setText(data.getStringExtra("INSTRUCTIONS")); }
        }
    }
}
