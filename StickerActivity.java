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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.adapter.StickerAdapter;
import com.example.printxpress.model.Sticker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StickerActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;

    private Toolbar toolbar;
    private EditText etSearch, etName, etPhone, etAddress, etEmail, etSpecialRequests;
    private EditText etCustomWidth, etCustomHeight;
    private LinearLayout llCustomSizeInputs;
    private Button btnGallery, btnCamera, btnOrder, btnComment;
    private TextView tvEstimatedPrice;
    private ImageView ivMockup, btnFilterTemplates;
    private RecyclerView rvTemplates;

    private RadioGroup rgStickerType, rgMaterial, rgShape, rgFinish, rgSize, rgQuantity;

    private final List<Sticker> fullStickerList = new ArrayList<>();
    private StickerAdapter stickerAdapter;
    private String selectedTemplateName = "Custom Stickers";
    private double currentTotalPrice = 0.0;
    private boolean isApplyingTemplate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        initViews();
        setupToolbar();
        setupTemplatesData();
        setupTemplatesRecyclerView();
        setupListeners();
        updatePrice();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearchTemplates);
        btnFilterTemplates = findViewById(R.id.btnFilterTemplates);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        llCustomSizeInputs = findViewById(R.id.llCustomSizeInputs);
        etCustomWidth = findViewById(R.id.etCustomWidth);
        etCustomHeight = findViewById(R.id.etCustomHeight);
        tvEstimatedPrice = findViewById(R.id.tvEstimatedPrice);
        rgStickerType = findViewById(R.id.rgStickerType);
        rgMaterial = findViewById(R.id.rgMaterial);
        rgShape = findViewById(R.id.rgShape);
        rgFinish = findViewById(R.id.rgFinish);
        rgSize = findViewById(R.id.rgSize);
        rgQuantity = findViewById(R.id.rgQuantity);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        btnOrder = findViewById(R.id.btnOrder);
        btnComment = findViewById(R.id.btnComment);
        ivMockup = findViewById(R.id.ivMockup);
        rvTemplates = findViewById(R.id.rvTemplates);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sticker Design");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTemplatesData() {
        fullStickerList.clear();
        fullStickerList.add(new Sticker("1", "Logo Pack", "Circle", "Glossy", "Vinyl", 2, 2, 50, 1000.0, "img25"));
        fullStickerList.add(new Sticker("2", "Business Labels", "Rectangle", "Matte", "Paper", 3, 2, 500, 6500.0, "img26"));
        fullStickerList.add(new Sticker("3", "Clear Branding", "Die Cut", "UV Coat", "Clear", 2.5, 2.5, 1000, 13000.0, "img27"));
    }

    private void setupTemplatesRecyclerView() {
        stickerAdapter = new StickerAdapter(fullStickerList, new StickerAdapter.OnItemClickListener() {
            @Override public void onItemClick(Sticker item, int position) { applyTemplate(item); }
            @Override public void onOrderClick(Sticker item, int position) { applyTemplate(item); }
        });
        rvTemplates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTemplates.setAdapter(stickerAdapter);
    }

    private void applyTemplate(Sticker item) {
        isApplyingTemplate = true;
        selectedTemplateName = item.getTitle();
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
        if (item.getMaterial().equalsIgnoreCase("Vinyl")) {
            rgStickerType.check(R.id.rbVinyl);
            rgMaterial.check(R.id.rbVinylMat);
        } else if (item.getMaterial().equalsIgnoreCase("Paper")) {
            rgStickerType.check(R.id.rbPaper);
            rgMaterial.check(R.id.rbPaperMat);
        }
        if (item.getShape().equalsIgnoreCase("Circle")) rgShape.check(R.id.rbCircle);
        else if (item.getShape().equalsIgnoreCase("Rectangle")) rgShape.check(R.id.rbRectangle);
        int qty = item.getQuantity();
        if (qty == 1) rgQuantity.check(R.id.rbQty1);
        else if (qty == 50) rgQuantity.check(R.id.rbQty50);
        else if (qty == 100) rgQuantity.check(R.id.rbQty100);
        else if (qty == 500) rgQuantity.check(R.id.rbQty500);
        else if (qty == 1000) rgQuantity.check(R.id.rbQty1000);
        isApplyingTemplate = false;
        updatePrice();
        Toast.makeText(this, "Template applied: " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void setupListeners() {
        btnGallery.setOnClickListener(v -> openFileChooser());
        btnCamera.setOnClickListener(v -> checkPermissionAndOpenCamera());
        btnOrder.setOnClickListener(v -> validateAndProceed());
        btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("CATEGORY", "Sticker");
            intent.putExtra("PREVIOUS_TEXT", etSpecialRequests.getText().toString());
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        btnFilterTemplates.setOnClickListener(v -> showSortMenu());
        RadioGroup.OnCheckedChangeListener priceListener = (group, checkedId) -> {
            if (!isApplyingTemplate) {
                if (checkedId == R.id.rbSizeCustom) llCustomSizeInputs.setVisibility(View.VISIBLE);
                else if (checkedId == R.id.rbSize2x2 || checkedId == R.id.rbSize3x3 || checkedId == R.id.rbSize4x4) llCustomSizeInputs.setVisibility(View.GONE);
                updatePrice();
            }
        };
        rgStickerType.setOnCheckedChangeListener(priceListener);
        rgMaterial.setOnCheckedChangeListener(priceListener);
        rgShape.setOnCheckedChangeListener(priceListener);
        rgFinish.setOnCheckedChangeListener(priceListener);
        rgSize.setOnCheckedChangeListener(priceListener);
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
                if (stickerAdapter != null) stickerAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Design"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        try { startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST); }
        catch (Exception e) { Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show(); }
    }

    private void updatePrice() {
        double unitPrice = 0.0;
        int typeId = rgStickerType.getCheckedRadioButtonId();
        
        if (typeId != -1) {
            if (typeId == R.id.rbVinyl) unitPrice = 15.0; 
            else if (typeId == R.id.rbPaper) unitPrice = 8.0;
        }

        int matId = rgMaterial.getCheckedRadioButtonId();
        if (matId == R.id.rbVinylMat) unitPrice += 5.0; 
        else if (matId == R.id.rbPaperMat) unitPrice += 2.0;

        int shapeId = rgShape.getCheckedRadioButtonId();
        if (shapeId == R.id.rbRectangle) unitPrice += 3.0; 
        else if (shapeId == R.id.rbDieCut) unitPrice += 10.0;

        int finishId = rgFinish.getCheckedRadioButtonId();
        if (finishId == R.id.rbGlossy || finishId == R.id.rbMatte) unitPrice += 2.0; 
        else if (finishId == R.id.rbUVCoat) unitPrice += 7.0;

        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbSize3x3) unitPrice += 5.0; 
        else if (sizeId == R.id.rbSize4x4) unitPrice += 12.0; 
        else if (sizeId == R.id.rbSizeCustom) {
            double w = 0, h = 0;
            try { 
                w = Double.parseDouble(etCustomWidth.getText().toString()); 
                h = Double.parseDouble(etCustomHeight.getText().toString()); 
            } catch (Exception ignored) {}
            if (w > 0 && h > 0) unitPrice += (w * h * 0.5); 
            else unitPrice += 20.0; 
        }

        int qty = getSelectedQuantity();
        currentTotalPrice = unitPrice * qty;

        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        if (currentTotalPrice >= 15000) {
            tvEstimatedPrice.setTextColor(Color.parseColor("#2E7D32"));
            tvEstimatedPrice.setText(String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f\nFREE DELIVERY APPLIED", currentTotalPrice));
        } else {
            tvEstimatedPrice.setTextColor(Color.BLACK);
            tvEstimatedPrice.setText(String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f", currentTotalPrice));
        }
    }

    private int getSelectedQuantity() {
        int id = rgQuantity.getCheckedRadioButtonId();
        if (id == R.id.rbQty1) return 1;
        if (id == R.id.rbQty50) return 50;
        if (id == R.id.rbQty100) return 100;
        if (id == R.id.rbQty500) return 500;
        if (id == R.id.rbQty1000) return 1000;
        return 1;
    }

    private void validateAndProceed() {
        int matId = rgMaterial.getCheckedRadioButtonId();
        String materialStr = (matId != -1) ? ((RadioButton) findViewById(matId)).getText().toString() : "Standard";

        int sizeId = rgSize.getCheckedRadioButtonId();
        String sizeStr = "Standard";
        if (sizeId == R.id.rbSizeCustom) {
            sizeStr = etCustomWidth.getText().toString() + "x" + etCustomHeight.getText().toString() + " Custom";
        } else if (sizeId != -1) {
            sizeStr = ((RadioButton) findViewById(sizeId)).getText().toString();
        }

        int finishId = rgFinish.getCheckedRadioButtonId();
        String finishStr = (finishId != -1) ? ((RadioButton) findViewById(finishId)).getText().toString() : "Standard";

        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "Stickers: " + selectedTemplateName);
        intent.putExtra("total_price", currentTotalPrice);
        intent.putExtra("quantity", String.valueOf(getSelectedQuantity()));
        
        intent.putExtra("material", materialStr);
        intent.putExtra("size", sizeStr);
        intent.putExtra("printing", finishStr);

        intent.putExtra("cust_name", etName.getText().toString());
        intent.putExtra("cust_phone", etPhone.getText().toString());
        intent.putExtra("cust_email", etEmail.getText().toString());
        intent.putExtra("cust_address", etAddress.getText().toString());
        intent.putExtra("special_req", etSpecialRequests.getText().toString());
        startActivity(intent);
    }

    private void showSortMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnFilterTemplates);
        popupMenu.getMenu().add("Price: Low to High");
        popupMenu.getMenu().add("Price: High to Low");
        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();
            if (stickerAdapter != null) {
                if (selected.equals("Price: Low to High")) stickerAdapter.sortByPrice(true);
                else stickerAdapter.sortByPrice(false);
            }
            return true;
        });
        popupMenu.show();
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (ivMockup != null) {
                ivMockup.clearColorFilter();
                ivMockup.setImageTintList(null);
                ivMockup.setAlpha(1.0f);
                ivMockup.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            if (requestCode == PICK_IMAGE_REQUEST) { ivMockup.setImageURI(data.getData()); }
            else if (requestCode == CAMERA_REQUEST) { Bitmap photo = (Bitmap) data.getExtras().get("data"); ivMockup.setImageBitmap(photo); }
            else if (requestCode == COMMENT_REQUEST_CODE) { if (etSpecialRequests != null) etSpecialRequests.setText(data.getStringExtra("INSTRUCTIONS")); }
        }
    }
}
