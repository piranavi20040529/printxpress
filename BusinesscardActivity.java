package com.example.printxpress;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.model.BusinessCard;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BusinesscardActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;

    private Toolbar toolbar;
    private RecyclerView rvTemplates;
    private TemplatesAdapter adapter;
    private TextView tvEstimatedPrice;
    private ImageView ivMockup, btnFilterTemplates;
    private Button btnGallery, btnCamera, btnOrder, btnComment;
    private RadioGroup rgCardStyle, rgPaperQuality, rgSidePrinting, rgQuantity;
    private EditText etSearchTemplates, etName, etPhone, etEmail, etAddress, etSpecialRequests;
    private LinearLayout llCustomSizeInputs;
    private EditText etCustomWidth, etCustomHeight;

    private List<BusinessCard> templatesList = new ArrayList<>();
    private List<BusinessCard> filteredList = new ArrayList<>();
    private double currentTotalPrice = 0.0;
    private String selectedTemplateName = "Custom Business Card";
    private boolean isApplyingTemplate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

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
        rgCardStyle = findViewById(R.id.rgCardStyle);
        rgPaperQuality = findViewById(R.id.rgPaperQuality);
        rgSidePrinting = findViewById(R.id.rgSidePrinting);
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
            getSupportActionBar().setTitle("Business Cards");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTemplatesData() {
        templatesList.clear();
        templatesList.add(new BusinessCard("1", "Gold Sparkle", "3.5 * 2.0", "Premium Plain", "Front & Back", "10", "430", "", String.valueOf(R.drawable.img7), true));
        templatesList.add(new BusinessCard("2", "Scholar's Gold Glow", "3.5 * 2.0", "Premium Shine", "Front Only", "25", "1025", "", String.valueOf(R.drawable.img8), false));
        templatesList.add(new BusinessCard("3", "Campus Elite Shine", "3.5 * 2.0", "Standard Plain", "Front Only", "50", "1450", "", String.valueOf(R.drawable.img9), false));
        templatesList.add(new BusinessCard("4", "Academic Gold Pro", "3.5 * 2.0", "Premium Plain", "Front & Back", "500", "16000", "20000", String.valueOf(R.drawable.img10), true));
        filteredList.clear();
        filteredList.addAll(templatesList);
    }

    private void setupTemplatesRecyclerView() {
        adapter = new TemplatesAdapter(filteredList);
        rvTemplates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTemplates.setAdapter(adapter);
    }

    private void applyTemplate(BusinessCard item) {
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

        if (item.getPaperQuality().equalsIgnoreCase("Premium Plain")) rgPaperQuality.check(R.id.rbPremiumPlain);
        else if (item.getPaperQuality().equalsIgnoreCase("Premium Shine")) rgPaperQuality.check(R.id.rbPremiumShine);
        else rgPaperQuality.check(R.id.rbStandardPlain);

        if (item.getSide().equalsIgnoreCase("Front & Back")) rgSidePrinting.check(R.id.rbFrontBack);
        else rgSidePrinting.check(R.id.rbFrontOnly);

        String qty = item.getQuantity();
        if (qty.equals("1")) rgQuantity.check(R.id.rbQty1);
        else if (qty.equals("10")) rgQuantity.check(R.id.rbQty10);
        else if (qty.equals("25")) rgQuantity.check(R.id.rbQty25);
        else if (qty.equals("50")) rgQuantity.check(R.id.rbQty50);
        else if (qty.equals("100")) rgQuantity.check(R.id.rbQty100);
        else if (qty.equals("250")) rgQuantity.check(R.id.rbQty250);
        else if (qty.equals("500")) rgQuantity.check(R.id.rbQty500);
        else if (qty.equals("1000")) rgQuantity.check(R.id.rbQty1000);

        if (item.getSize().equalsIgnoreCase("Custom Size")) rgCardStyle.check(R.id.rbCustomSize);
        else rgCardStyle.check(R.id.rbStandardSize);

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
            intent.putExtra("CATEGORY", "Business Card");
            intent.putExtra("PREVIOUS_TEXT", etSpecialRequests != null ? etSpecialRequests.getText().toString() : "");
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        btnOrder.setOnClickListener(v -> validateAndProceed());

        RadioGroup.OnCheckedChangeListener priceListener = (g, id) -> {
            if (!isApplyingTemplate) {
                selectedTemplateName = "Custom Business Card";
                if (id == R.id.rbCustomSize) llCustomSizeInputs.setVisibility(View.VISIBLE);
                else if (id == R.id.rbStandardSize) llCustomSizeInputs.setVisibility(View.GONE);
                updatePrice();
            }
        };
        rgCardStyle.setOnCheckedChangeListener(priceListener);
        rgPaperQuality.setOnCheckedChangeListener(priceListener);
        rgSidePrinting.setOnCheckedChangeListener(priceListener);
        rgQuantity.setOnCheckedChangeListener(priceListener);

        etSearchTemplates.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterTemplates(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilterTemplates.setOnClickListener(v -> showFilterMenu());
    }

    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnFilterTemplates);
        popupMenu.getMenu().add("Price: Low to High");
        popupMenu.getMenu().add("Price: High to Low");
        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();
            if (selected.equals("Price: Low to High")) sortTemplates(true);
            else if (selected.equals("Price: High to Low")) sortTemplates(false);
            return true;
        });
        popupMenu.show();
    }

    private void filterTemplates(String query) {
        filteredList.clear();
        String lowerQuery = query.toLowerCase().trim();
        for (BusinessCard item : templatesList) {
            if (lowerQuery.isEmpty() || item.getTitle().toLowerCase().contains(lowerQuery)) filteredList.add(item);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void sortTemplates(boolean ascending) {
        if (filteredList == null || filteredList.isEmpty()) return;
        Collections.sort(filteredList, (o1, o2) -> {
            try {
                double p1 = Double.parseDouble(o1.getPrice());
                double p2 = Double.parseDouble(o2.getPrice());
                return ascending ? Double.compare(p1, p2) : Double.compare(p2, p1);
            } catch (NumberFormatException e) { return 0; }
        });
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void updatePrice() {
        int cardStyleId = rgCardStyle.getCheckedRadioButtonId();
        if (cardStyleId == -1) { currentTotalPrice = 0.0; updatePriceDisplay(); return; }

        int qty = 1;
        int qtyId = rgQuantity.getCheckedRadioButtonId();
        if (qtyId == R.id.rbQty10) qty = 10;
        else if (qtyId == R.id.rbQty25) qty = 25;
        else if (qtyId == R.id.rbQty50) qty = 50;
        else if (qtyId == R.id.rbQty100) qty = 100;
        else if (qtyId == R.id.rbQty250) qty = 250;
        else if (qtyId == R.id.rbQty500) qty = 500;
        else if (qtyId == R.id.rbQty1000) qty = 1000;

        double unitPrice = 20.0;
        int paperId = rgPaperQuality.getCheckedRadioButtonId();
        if (paperId == R.id.rbStandardPlain) unitPrice += 5.0;
        else if (paperId == R.id.rbPremiumPlain) unitPrice += 10.0;
        else if (paperId == R.id.rbPremiumShine) unitPrice += 15.0;

        int sideId = rgSidePrinting.getCheckedRadioButtonId();
        if (sideId == R.id.rbFrontOnly) unitPrice += 5.0;
        else if (sideId == R.id.rbFrontBack) unitPrice += 10.0;

        if (cardStyleId == R.id.rbCustomSize) unitPrice += 20.0;

        currentTotalPrice = unitPrice * qty;
        if (qty >= 500) currentTotalPrice *= 0.8;
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

    private void openCamera() {
        try { startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE); }
        catch (Exception e) { Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show(); }
    }

    private void validateAndProceed() {
        int qId = rgQuantity.getCheckedRadioButtonId();
        int sId = rgCardStyle.getCheckedRadioButtonId();
        int pId = rgPaperQuality.getCheckedRadioButtonId();
        int prId = rgSidePrinting.getCheckedRadioButtonId();

        if (qId == -1 || sId == -1 || pId == -1 || prId == -1) {
            Toast.makeText(this, "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbQty = findViewById(qId);
        RadioButton rbSize = findViewById(sId);
        RadioButton rbMaterial = findViewById(pId);
        RadioButton rbPrinting = findViewById(prId);

        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "Business card: " + selectedTemplateName);
        intent.putExtra("total_price", currentTotalPrice);
        intent.putExtra("quantity", rbQty.getText().toString());
        intent.putExtra("material", rbMaterial.getText().toString());
        intent.putExtra("size", rbSize.getText().toString());
        intent.putExtra("printing", rbPrinting.getText().toString());

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

    private class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.ViewHolder> {
        private List<BusinessCard> list;
        public TemplatesAdapter(List<BusinessCard> list) { this.list = list; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_business_card_template, p, false);
            return new ViewHolder(v);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            BusinessCard item = list.get(pos);
            h.tvTitle.setText(item.getTitle());
            h.tvPrice.setText("Rs. " + item.getPrice());
            h.tvSize.setText("Size: " + item.getSize());
            h.tvPaper.setText("Paper: " + item.getPaperQuality());
            h.tvSide.setText("Side: " + item.getSide());
            h.tvQty.setText("Qty: " + item.getQuantity() + " Units");
            try { int resId = Integer.parseInt(item.getImageUrl()); h.ivTemplate.setImageResource(resId); } catch (Exception e) { h.ivTemplate.setImageResource(R.drawable.img7); }
            if (item.getOldPrice() != null && !item.getOldPrice().isEmpty()) {
                h.tvOldPrice.setVisibility(View.VISIBLE);
                h.tvOldPrice.setText("Rs. " + item.getOldPrice());
                h.tvOldPrice.setPaintFlags(h.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else h.tvOldPrice.setVisibility(View.GONE);
            h.btnOrder.setOnClickListener(v -> applyTemplate(item));
        }
        @Override public int getItemCount() { return list.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvSize, tvPaper, tvSide, tvQty, tvOldPrice;
            ImageView ivTemplate;
            MaterialButton btnOrder;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTemplateName);
                tvPrice = itemView.findViewById(R.id.tvTemplatePrice);
                tvSize = itemView.findViewById(R.id.tvSize);
                tvPaper = itemView.findViewById(R.id.tvPaperQuality);
                tvSide = itemView.findViewById(R.id.tvSidePrinting);
                tvQty = itemView.findViewById(R.id.tvQuantity);
                tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
                ivTemplate = itemView.findViewById(R.id.ivTemplateImage);
                btnOrder = itemView.findViewById(R.id.btnSelectTemplate);
            }
        }
    }
}
