package com.example.printxpress;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import com.example.printxpress.adapter.MugAdapter;
import com.example.printxpress.model.Mug;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MugActivity extends AppCompatActivity implements MugAdapter.OnItemClickListener {
    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int COMMENT_REQUEST_CODE = 102;
    private Toolbar toolbar;
    private RadioGroup radioGroupMugType, radioGroupCapacity, radioGroupQuantity, radioGroupDesignSide, radioGroupMugColor;
    private Button btnUploadGallery, btnCamera, btnOrderNow, btnComment;
    private EditText etFullName, etContact, etEmail, etAddress, etSpecialRequests, etSearch;
    private TextView txtTotalPrice;
    private ImageView ivMockup, btnFilter;
    private RecyclerView recyclerViewTemplates;
    private MugAdapter mugAdapter;
    private List<Mug> fullMugList;
    private double totalPrice = 0;
    private String selectedTemplateName = "Custom Mug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mug);

        initViews();
        setupToolbar();
        setupPopularMugs();
        setupListeners();
        updatePrice();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        radioGroupMugType = findViewById(R.id.rgMaterial);
        radioGroupCapacity = findViewById(R.id.rgCapacity);
        radioGroupMugColor = findViewById(R.id.rgMugColor);
        radioGroupQuantity = findViewById(R.id.rgQuantity);
        radioGroupDesignSide = findViewById(R.id.rgDesignSide);
        btnOrderNow = findViewById(R.id.btnOrder);
        btnComment = findViewById(R.id.btnComment);
        btnUploadGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        etFullName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        txtTotalPrice = findViewById(R.id.tvEstimatedPrice);
        ivMockup = findViewById(R.id.ivMockup);
        recyclerViewTemplates = findViewById(R.id.rvTemplates);
        etSearch = findViewById(R.id.etSearchTemplates);
        btnFilter = findViewById(R.id.btnFilterTemplates);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mug Design");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPopularMugs() {
        fullMugList = new ArrayList<>();
        fullMugList.add(new Mug("1", "Classic Ceramic", "Ceramic", "350 ml", "White", "One side only", 2, 1000.0, "img22"));
        fullMugList.add(new Mug("2", "Travel Flask", "Travel", "450 ml", "Steel", "Both side", 1, 1450.0, "img23"));
        fullMugList.add(new Mug("3", "Elegant Glass", "Glass", "350 ml", "Clear", "Full wrap", 5, 4500.0, "img24"));

        mugAdapter = new MugAdapter(fullMugList, this);
        recyclerViewTemplates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTemplates.setAdapter(mugAdapter);
    }

    private void applyTemplate(Mug item) {
        selectedTemplateName = item.getTitle();

        if (ivMockup != null) {
            ivMockup.clearColorFilter();
            ivMockup.setImageTintList(null);
            int resId = getResources().getIdentifier(item.getImageUrl(), "drawable", getPackageName());
            if (resId != 0) {
                ivMockup.setImageResource(resId);
                ivMockup.setAlpha(1.0f);
                ivMockup.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }


        if (item.getMugType().equalsIgnoreCase("Ceramic")) radioGroupMugType.check(R.id.rbCeramic);
        else if (item.getMugType().equalsIgnoreCase("Travel")) radioGroupMugType.check(R.id.rbTravel);
        else if (item.getMugType().equalsIgnoreCase("Magic")) radioGroupMugType.check(R.id.rbMagic);
        else if (item.getMugType().equalsIgnoreCase("Glass")) radioGroupMugType.check(R.id.rbGlass);

        if (item.getSize().contains("180")) radioGroupCapacity.check(R.id.rb180ml);
        else if (item.getSize().contains("350")) radioGroupCapacity.check(R.id.rb350ml);
        else if (item.getSize().contains("450")) radioGroupCapacity.check(R.id.rb450ml);
        else if (item.getSize().contains("600")) radioGroupCapacity.check(R.id.rb600ml);

        if (item.getColor().equalsIgnoreCase("White")) radioGroupMugColor.check(R.id.rbWhite);
        else if (item.getColor().equalsIgnoreCase("Black")) radioGroupMugColor.check(R.id.rbBlack);
        else if (item.getColor().equalsIgnoreCase("Red")) radioGroupMugColor.check(R.id.rbRed);
        else if (item.getColor().equalsIgnoreCase("Blue")) radioGroupMugColor.check(R.id.rbBlue);
        else if (item.getColor().equalsIgnoreCase("Steel")) radioGroupMugColor.check(R.id.rbSteel);
        else if (item.getColor().equalsIgnoreCase("Clear")) radioGroupMugColor.check(R.id.rbClear);
        else if (item.getColor().equalsIgnoreCase("Gold")) radioGroupMugColor.check(R.id.rbGold);

        if (item.getPrintArea().equalsIgnoreCase("One side only")) radioGroupDesignSide.check(R.id.rbOneSide);
        else if (item.getPrintArea().equalsIgnoreCase("Both side")) radioGroupDesignSide.check(R.id.rbBothSide);
        else if (item.getPrintArea().equalsIgnoreCase("Full wrap")) radioGroupDesignSide.check(R.id.rbFullWrap);

        int qty = item.getQuantity();
        if (qty == 1) radioGroupQuantity.check(R.id.rbQty1);
        else if (qty == 2) radioGroupQuantity.check(R.id.rbQty2);
        else if (qty == 3) radioGroupQuantity.check(R.id.rbQty3);
        else if (qty == 5) radioGroupQuantity.check(R.id.rbQty5);
        else if (qty == 10) radioGroupQuantity.check(R.id.rbQty10);
        else if (qty == 25) radioGroupQuantity.check(R.id.rbQty25);
        else if (qty == 50) radioGroupQuantity.check(R.id.rbQty50);
        else if (qty == 100) radioGroupQuantity.check(R.id.rbQty100);


        updatePrice();
        Toast.makeText(this, "Template applied: " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }


    private void setupListeners() {

        btnUploadGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });


        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });


        if (btnComment != null) {
            btnComment.setOnClickListener(v -> {
                Intent intent = new Intent(this, CommentActivity.class);
                intent.putExtra("CATEGORY", "Mug");
                intent.putExtra("PREVIOUS_TEXT", etSpecialRequests != null ? etSpecialRequests.getText().toString() : "");
                startActivityForResult(intent, COMMENT_REQUEST_CODE);
            });
        }


        btnOrderNow.setOnClickListener(v -> validateAndProceed());


        RadioGroup.OnCheckedChangeListener priceListener = (g, id) -> updatePrice();
        radioGroupMugType.setOnCheckedChangeListener(priceListener);
        radioGroupCapacity.setOnCheckedChangeListener(priceListener);
        radioGroupQuantity.setOnCheckedChangeListener(priceListener);
        radioGroupDesignSide.setOnCheckedChangeListener(priceListener);
        radioGroupMugColor.setOnCheckedChangeListener(priceListener);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mugAdapter != null) mugAdapter.filter(s.toString(), "All", "All", "All");
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilter.setOnClickListener(v -> showFilterMenu());
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try { startActivityForResult(intent, CAMERA_REQUEST_CODE); }
        catch (ActivityNotFoundException e) { Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show(); }
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
            if (requestCode == GALLERY_REQUEST_CODE) { ivMockup.setImageURI(data.getData()); }
            else if (requestCode == CAMERA_REQUEST_CODE) { Bitmap photo = (Bitmap) data.getExtras().get("data"); ivMockup.setImageBitmap(photo); }
            else if (requestCode == COMMENT_REQUEST_CODE) { if (etSpecialRequests != null) etSpecialRequests.setText(data.getStringExtra("INSTRUCTIONS")); }
        }
    }


    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnFilter);
        popupMenu.getMenu().add("Price: Low to High");
        popupMenu.getMenu().add("Price: High to Low");
        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();
            if (selected.equals("Price: Low to High")) { if (mugAdapter != null) mugAdapter.sortByPrice(true); }
            else if (selected.equals("Price: High to Low")) { if (mugAdapter != null) mugAdapter.sortByPrice(false); }
            return true;
        });
        popupMenu.show();
    }

    @Override public void onItemClick(Mug item, int position) { applyTemplate(item); }
    @Override public void onOrderClick(Mug item, int position) { applyTemplate(item); validateAndProceed(); }
    @Override public void onSaveClick(Mug item, int position) { Toast.makeText(this, "Template saved!", Toast.LENGTH_SHORT).show(); }
    @Override public void onShareClick(Mug item, int position) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this mug: " + item.getTitle());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }

    private void validateAndProceed() {
        String name = etFullName.getText().toString().trim();
        String phone = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }


        RadioButton rbType = findViewById(radioGroupMugType.getCheckedRadioButtonId());
        RadioButton rbCapacity = findViewById(radioGroupCapacity.getCheckedRadioButtonId());
        RadioButton rbQty = findViewById(radioGroupQuantity.getCheckedRadioButtonId());
        RadioButton rbSide = findViewById(radioGroupDesignSide.getCheckedRadioButtonId());

        if (rbType == null || rbCapacity == null || rbQty == null || rbSide == null) {
            Toast.makeText(this, "Please select all specifications", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.putExtra("product_name", "Mug: " + selectedTemplateName);
        intent.putExtra("total_price", totalPrice);
        intent.putExtra("quantity", rbQty.getText().toString());
        intent.putExtra("cust_name", name);
        intent.putExtra("cust_phone", phone);
        intent.putExtra("cust_email", email);
        intent.putExtra("cust_address", address);
        intent.putExtra("material", rbType.getText().toString());
        intent.putExtra("size", rbCapacity.getText().toString());
        intent.putExtra("printing", rbSide.getText().toString());
        intent.putExtra("special_req", etSpecialRequests.getText().toString().trim());
        startActivity(intent);
    }

    private void updatePrice() {
        int checkedMugId = radioGroupMugType.getCheckedRadioButtonId();


        if (checkedMugId == -1) {
            totalPrice = 0.0;
            updatePriceDisplay();
            return;
        }

        double unitPrice = 0.0;

        if (checkedMugId == R.id.rbCeramic) unitPrice = 450.0;
        else if (checkedMugId == R.id.rbMagic) unitPrice = 750.0;
        else if (checkedMugId == R.id.rbTravel) unitPrice = 1200.0;
        else if (checkedMugId == R.id.rbGlass) unitPrice = 600.0;


        int capacityId = radioGroupCapacity.getCheckedRadioButtonId();
        if (capacityId == R.id.rb350ml) unitPrice += 50.0;
        else if (capacityId == R.id.rb450ml) unitPrice += 100.0;
        else if (capacityId == R.id.rb600ml) unitPrice += 200.0;


        int colorId = radioGroupMugColor.getCheckedRadioButtonId();
        if (colorId == R.id.rbRed || colorId == R.id.rbBlue) unitPrice += 40.0;
        else if (colorId == R.id.rbGold) unitPrice += 150.0;


        int sideId = radioGroupDesignSide.getCheckedRadioButtonId();
        if (sideId == R.id.rbBothSide) unitPrice += 150.0;
        else if (sideId == R.id.rbFullWrap) unitPrice += 250.0;


        int qty = getSelectedQuantityValue();


        totalPrice = unitPrice * qty;

        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        if (totalPrice >= 15000) {
            txtTotalPrice.setTextColor(Color.parseColor("#2E7D32"));
            txtTotalPrice.setText(String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f\nFREE DELIVERY APPLIED", totalPrice));
        } else {
            txtTotalPrice.setTextColor(Color.BLACK);
            txtTotalPrice.setText(String.format(Locale.getDefault(), "Estimated Price: Rs. %.2f", totalPrice));
        }
    }

    private int getSelectedQuantityValue() {
        int qtyId = radioGroupQuantity.getCheckedRadioButtonId();
        if (qtyId == R.id.rbQty2) return 2;
        if (qtyId == R.id.rbQty3) return 3;
        if (qtyId == R.id.rbQty5) return 5;
        if (qtyId == R.id.rbQty10) return 10;
        if (qtyId == R.id.rbQty25) return 25;
        if (qtyId == R.id.rbQty50) return 50;
        if (qtyId == R.id.rbQty100) return 100;
        return 1;
    }
}
