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
import com.example.printxpress.adapter.TShirtAdapter;
import com.example.printxpress.model.Tshirt;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TshirtActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;

    private Toolbar toolbar;
    private EditText etSearch, etDesignDesc, etName, etPhone, etAddress, etEmail;
    private TextView tvEstimatedPrice;
    private ImageView ivPreview, btnFilter;
    private MaterialCardView cardPreview;
    private RadioGroup rgTshirtStyle, rgFabric, rgPrintQuality, rgSize, rgQuantity, rgPlacement, rgTshirtColor;
    private Button btnUpload, btnCamera, btnContinue, btnCommentAction;
    private RecyclerView recyclerViewTemplates;
    private TShirtAdapter adapter;
    private List<Tshirt> tshirtList = new ArrayList<>();
    
    private double totalPrice = 0.0;
    private String selectedTemplateName = "Custom T-Shirt";
    private boolean isApplyingTemplate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tshirt);
        initViews();
        setupToolbar();
        setupTemplatesData();
        setupRecyclerView();
        setupListeners();
        updatePrice();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        btnFilter = findViewById(R.id.btnFilter);
        recyclerViewTemplates = findViewById(R.id.recyclerViewTemplates);
        cardPreview = findViewById(R.id.cardPreview);
        ivPreview = findViewById(R.id.ivPreview);
        btnUpload = findViewById(R.id.btnUpload);
        btnCamera = findViewById(R.id.btnCamera);
        etDesignDesc = findViewById(R.id.etDesignDesc);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        rgTshirtStyle = findViewById(R.id.rgTshirtStyle);
        rgFabric = findViewById(R.id.rgFabric);
        rgPrintQuality = findViewById(R.id.rgPrintQuality);
        rgSize = findViewById(R.id.rgSize);
        rgQuantity = findViewById(R.id.rgQuantity);
        rgPlacement = findViewById(R.id.rgPlacement);
        rgTshirtColor = findViewById(R.id.rgTshirtColor);
        tvEstimatedPrice = findViewById(R.id.tvEstimatedPrice);
        btnContinue = findViewById(R.id.btnContinue);
        btnCommentAction = findViewById(R.id.btnComment);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("T-Shirt Design");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTemplatesData() {
        tshirtList.clear();
        tshirtList.add(new Tshirt("1", "Premium Cotton Crew", "Men", "White", "L", "Front Only", "Professional", "100% Cotton", 1, 750.0, "img17"));
        tshirtList.add(new Tshirt("2", "Urban Street Hoodie", "Hoodie", "Black", "XL", "Front and Back", "Premium", "Heavy Material", 2, 4600.0, "img18"));
        tshirtList.add(new Tshirt("3", "Eco Women's V-Neck", "Women", "Navy", "M", "Front Only", "Professional", "Organic Cotton", 5, 4750.0, "img19"));
        tshirtList.add(new Tshirt("4", "Sports Performance Tee", "Men", "Blue", "L", "Front Only", "Premium", "Polyester", 10, 10000.0, "img20"));
        tshirtList.add(new Tshirt("5", "Elite Corporate Polo", "Men", "Black", "L", "Front Only", "Professional", "Cotton-Polyester Blend", 2, 1700.0, "img21"));
    }

    private void setupRecyclerView() {
        adapter = new TShirtAdapter(tshirtList, new TShirtAdapter.OnItemClickListener() {
            @Override public void onItemClick(Tshirt item) { applyTemplate(item); }
            @Override public void onOrderClick(Tshirt item) { applyTemplate(item); }
        });
        recyclerViewTemplates.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewTemplates.setAdapter(adapter);
    }

    private void applyTemplate(Tshirt item) {
        isApplyingTemplate = true;
        selectedTemplateName = item.getTitle();
        if (ivPreview != null) {
            ivPreview.clearColorFilter();
            ivPreview.setImageTintList(null);
            ivPreview.setAlpha(1.0f);
            int resId = getResources().getIdentifier(item.getImageUrl(), "drawable", getPackageName());
            if (resId != 0) {
                ivPreview.setImageResource(resId);
                ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
        if (item.getColor() != null) {
            if (item.getColor().equalsIgnoreCase("White")) rgTshirtColor.check(R.id.rbWhite);
            else if (item.getColor().equalsIgnoreCase("Black")) rgTshirtColor.check(R.id.rbBlack);
            else if (item.getColor().equalsIgnoreCase("Red")) rgTshirtColor.check(R.id.rbRed);
            else if (item.getColor().equalsIgnoreCase("Blue")) rgTshirtColor.check(R.id.rbBlue);
            else if (item.getColor().equalsIgnoreCase("Green")) rgTshirtColor.check(R.id.rbGreen);
            else if (item.getColor().equalsIgnoreCase("Navy")) rgTshirtColor.check(R.id.rbNavy);
        }
        if (item.getSize() != null) {
            if (item.getSize().equalsIgnoreCase("S")) rgSize.check(R.id.rbS);
            else if (item.getSize().equalsIgnoreCase("M")) rgSize.check(R.id.rbM);
            else if (item.getSize().equalsIgnoreCase("L")) rgSize.check(R.id.rbL);
            else if (item.getSize().equalsIgnoreCase("XL")) rgSize.check(R.id.rbXL);
            else if (item.getSize().equalsIgnoreCase("2XL")) rgSize.check(R.id.rb2XL);
            else if (item.getSize().equalsIgnoreCase("3XL")) rgSize.check(R.id.rb3XL);
        }
        if (item.getProductType() != null) {
            if (item.getProductType().equalsIgnoreCase("Men")) rgTshirtStyle.check(R.id.rbMen);
            else if (item.getProductType().equalsIgnoreCase("Women")) rgTshirtStyle.check(R.id.rbWomen);
            else if (item.getProductType().equalsIgnoreCase("Kids")) rgTshirtStyle.check(R.id.rbKids);
            else if (item.getProductType().equalsIgnoreCase("Hoodie")) rgTshirtStyle.check(R.id.rbHoodie);
        }
        if (item.getMaterial() != null) {
            if (item.getMaterial().equalsIgnoreCase("100% Cotton")) rgFabric.check(R.id.rbCotton);
            else if (item.getMaterial().equalsIgnoreCase("Polyester")) rgFabric.check(R.id.rbPolyester);
            else if (item.getMaterial().equalsIgnoreCase("Heavy Material")) rgFabric.check(R.id.rbHeavy);
            else if (item.getMaterial().equalsIgnoreCase("Organic Cotton")) rgFabric.check(R.id.rbOrganic);
            else if (item.getMaterial().equalsIgnoreCase("Cotton-Polyester Blend")) rgFabric.check(R.id.rbBlend);
        }
        if (item.getPrintType() != null) {
            if (item.getPrintType().equalsIgnoreCase("Standard")) rgPrintQuality.check(R.id.rbStandard);
            else if (item.getPrintType().equalsIgnoreCase("Professional")) rgPrintQuality.check(R.id.rbProfessional);
            else if (item.getPrintType().equalsIgnoreCase("Premium")) rgPrintQuality.check(R.id.rbPremium);
        }
        if (item.getPrintLocation() != null) {
            if (item.getPrintLocation().equalsIgnoreCase("Front Only")) rgPlacement.check(R.id.rbFrontOnly);
            else if (item.getPrintLocation().equalsIgnoreCase("Back Only")) rgPlacement.check(R.id.rbBackOnly);
            else if (item.getPrintLocation().equalsIgnoreCase("Front and Back")) rgPlacement.check(R.id.rbFrontBack);
        }
        int qty = item.getQuantity();
        if (qty == 1) rgQuantity.check(R.id.rbQty1);
        else if (qty == 2) rgQuantity.check(R.id.rbQty2);
        else if (qty == 5) rgQuantity.check(R.id.rbQty5);
        else if (qty == 10) rgQuantity.check(R.id.rbQty10);
        else if (qty == 50) rgQuantity.check(R.id.rbQty50);
        else if (qty == 100) rgQuantity.check(R.id.rbQty100);
        
        isApplyingTemplate = false;
        updatePrice();
        Toast.makeText(this, "Template applied: " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void setupListeners() {
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else openCamera();
        });
        btnCommentAction.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("CATEGORY", "T-Shirt");
            intent.putExtra("PREVIOUS_TEXT", etDesignDesc != null ? etDesignDesc.getText().toString() : "");
            startActivityForResult(intent, COMMENT_REQUEST_CODE);
        });
        btnContinue.setOnClickListener(v -> validateAndProceed());
        RadioGroup.OnCheckedChangeListener priceListener = (g, id) -> { if (!isApplyingTemplate) updatePrice(); };
        rgTshirtStyle.setOnCheckedChangeListener(priceListener);
        rgFabric.setOnCheckedChangeListener(priceListener);
        rgPrintQuality.setOnCheckedChangeListener(priceListener);
        rgQuantity.setOnCheckedChangeListener(priceListener);
        rgSize.setOnCheckedChangeListener(priceListener);
        rgTshirtColor.setOnCheckedChangeListener(priceListener);
        rgPlacement.setOnCheckedChangeListener(priceListener);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { if (adapter != null) adapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilter.setOnClickListener(v -> showFilterMenu());
    }

    private void updatePrice() {
        int styleId = rgTshirtStyle.getCheckedRadioButtonId();
        if (styleId == -1) { totalPrice = 0.0; updatePriceDisplay(); return; }
        double unitPrice = 0.0;
        if (styleId == R.id.rbMen) unitPrice = 600.0;
        else if (styleId == R.id.rbWomen) unitPrice = 550.0;
        else if (styleId == R.id.rbKids) unitPrice = 400.0;
        else if (styleId == R.id.rbHoodie) unitPrice = 1500.0;
        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbM) unitPrice += 30.0;
        else if (sizeId == R.id.rbL) unitPrice += 60.0;
        else if (sizeId == R.id.rbXL) unitPrice += 100.0;
        else if (sizeId == R.id.rb2XL) unitPrice += 160.0;
        else if (sizeId == R.id.rb3XL) unitPrice += 220.0;
        int fabId = rgFabric.getCheckedRadioButtonId();
        if (fabId == R.id.rbPolyester) unitPrice += 100.0;
        else if (fabId == R.id.rbHeavy) unitPrice += 200.0;
        else if (fabId == R.id.rbOrganic) unitPrice += 250.0;
        else if (fabId == R.id.rbBlend) unitPrice += 100.0;
        int qualId = rgPrintQuality.getCheckedRadioButtonId();
        if (qualId == R.id.rbProfessional) unitPrice += 150.0;
        else if (qualId == R.id.rbPremium) unitPrice += 300.0;
        if (rgPlacement.getCheckedRadioButtonId() == R.id.rbFrontBack) unitPrice += 300.0;
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
        if (id == R.id.rbQty2) return 2;
        else if (id == R.id.rbQty5) return 5;
        else if (id == R.id.rbQty10) return 10;
        else if (id == R.id.rbQty20) return 20;
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
        PopupMenu popupMenu = new PopupMenu(this, btnFilter);
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
        int sId = rgTshirtStyle.getCheckedRadioButtonId();
        int szId = rgSize.getCheckedRadioButtonId();
        int fId = rgFabric.getCheckedRadioButtonId();
        int pId = rgPlacement.getCheckedRadioButtonId();
        int qId = rgQuantity.getCheckedRadioButtonId();
        if (sId == -1 || szId == -1 || fId == -1 || pId == -1 || qId == -1) {
            Toast.makeText(this, "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rbStyle = findViewById(sId);
        RadioButton rbSize = findViewById(szId);
        RadioButton rbFabric = findViewById(fId);
        RadioButton rbPlace = findViewById(pId);
        RadioButton rbQty = findViewById(qId);
        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "T-Shirt: " + selectedTemplateName);
        intent.putExtra("total_price", totalPrice);
        intent.putExtra("quantity", rbQty.getText().toString());
        intent.putExtra("material", rbFabric.getText().toString());
        intent.putExtra("size", rbStyle.getText().toString() + " (" + rbSize.getText().toString() + ")");
        intent.putExtra("printing", rbPlace.getText().toString());
        intent.putExtra("cust_name", etName.getText().toString());
        intent.putExtra("cust_phone", etPhone.getText().toString());
        intent.putExtra("cust_email", etEmail.getText().toString());
        intent.putExtra("cust_address", etAddress.getText().toString());
        intent.putExtra("special_req", etDesignDesc.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (ivPreview != null) { 
                ivPreview.clearColorFilter(); 
                ivPreview.setImageTintList(null);
                ivPreview.setAlpha(1.0f);
                ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP); 
            }
            if (requestCode == GALLERY_REQUEST_CODE) { if (data.getData() != null) ivPreview.setImageURI(data.getData()); }
            else if (requestCode == CAMERA_REQUEST_CODE) { if (data.getExtras() != null) { Bitmap photo = (Bitmap) data.getExtras().get("data"); ivPreview.setImageBitmap(photo); } }
            else if (requestCode == COMMENT_REQUEST_CODE) { if (etDesignDesc != null) etDesignDesc.setText(data.getStringExtra("INSTRUCTIONS")); }
        }
    }
}
