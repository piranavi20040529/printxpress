package com.example.printxpress;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import java.util.Calendar;
import java.util.Locale;

public class OrderManagementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvProductName, tvProductQuantity, tvTotalPrice, tvProductMaterial, tvProductPrinting, tvProductSize;
    private MaterialRadioButton radioHomeDelivery, radioPickup;
    private EditText etAddress, etSpecialInstructions, etCustomerName, etCustomerPhone, etCustomerEmail, etCustomerAddress;
    private TextView tvSelectedDate, tvSelectedTime, tvPickupLocation;
    private MaterialButton btnConfirmOrder, btnSelectDate, btnSelectTime;
    private Calendar selectedCalendar;
    private String selectedDate = "", selectedTime = "";
    private String productName = "Product", quantity = "1", price = "Rs. 0.00", material = "Standard", printing = "Standard", size = "Standard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        getDataFromIntent();
        initViews();
        setupToolbar();
        setupDeliveryLogic();
        btnConfirmOrder.setOnClickListener(v -> validateAndPlaceOrder());
    }

    private void getDataFromIntent() {
        if (getIntent() != null) {
            productName = getIntent().getStringExtra("product_name") != null ? getIntent().getStringExtra("product_name") : "Product";
            quantity = getIntent().getStringExtra("quantity") != null ? getIntent().getStringExtra("quantity") : "1";
            double totalPrice = getIntent().getDoubleExtra("total_price", 0.0);
            if (totalPrice > 0) {
                price = "Rs. " + String.format(Locale.getDefault(), "%.2f", totalPrice);
            } else if (getIntent().getStringExtra("price") != null) {
                price = getIntent().getStringExtra("price");
            }
            material = getIntent().getStringExtra("material") != null ? getIntent().getStringExtra("material") : "Standard";
            printing = getIntent().getStringExtra("printing") != null ? getIntent().getStringExtra("printing") : "Standard";
            size = getIntent().getStringExtra("size") != null ? getIntent().getStringExtra("size") : "Standard";
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvProductMaterial = findViewById(R.id.tvProductMaterial);
        tvProductPrinting = findViewById(R.id.tvProductPrinting);
        tvProductSize = findViewById(R.id.tvProductSize);
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        radioHomeDelivery = findViewById(R.id.radioHomeDelivery);
        radioPickup = findViewById(R.id.radioPickup);
        etAddress = findViewById(R.id.etAddress);
        etSpecialInstructions = findViewById(R.id.etSpecialInstructions);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        tvProductName.setText(productName);
        tvProductQuantity.setText(quantity);
        tvTotalPrice.setText(price);
        tvProductMaterial.setText(material);
        tvProductPrinting.setText(printing);
        tvProductSize.setText(size);

        if (getIntent().getStringExtra("cust_name") != null) {
            etCustomerName.setText(getIntent().getStringExtra("cust_name"));
        }
        if (getIntent().getStringExtra("cust_phone") != null) {
            etCustomerPhone.setText(getIntent().getStringExtra("cust_phone"));
        }
        if (getIntent().getStringExtra("cust_email") != null) {
            etCustomerEmail.setText(getIntent().getStringExtra("cust_email"));
        }
        if (getIntent().getStringExtra("cust_address") != null) {
            String address = getIntent().getStringExtra("cust_address");
            etCustomerAddress.setText(address);
            etAddress.setText(address);
        }
        if (getIntent().getStringExtra("special_req") != null) {
            etSpecialInstructions.setText(getIntent().getStringExtra("special_req"));
        }

        selectedCalendar = Calendar.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Management");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDeliveryLogic() {
        radioHomeDelivery.setOnClickListener(v -> {
            radioHomeDelivery.setChecked(true);
            radioPickup.setChecked(false);
            etAddress.setVisibility(View.VISIBLE);
            tvPickupLocation.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(etCustomerAddress.getText())) {
                etAddress.setText(etCustomerAddress.getText().toString());
            }
        });

        etCustomerAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (radioHomeDelivery.isChecked()) {
                    etAddress.setText(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        radioPickup.setOnClickListener(v -> {
            radioPickup.setChecked(true);
            radioHomeDelivery.setChecked(false);
            tvPickupLocation.setVisibility(View.VISIBLE);
            etAddress.setVisibility(View.GONE);
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = day + "/" + (month + 1) + "/" + year;
            tvSelectedDate.setText(selectedDate);
        }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hr, min) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hr, min);
            tvSelectedTime.setText(selectedTime);
        }, 10, 0, true);
        timePickerDialog.show();
    }

    private void validateAndPlaceOrder() {
        if (!FirebaseHelper.isLoggedIn()) {
            Toast.makeText(this, "!Please Login first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etCustomerName.getText())) {
            Toast.makeText(this, "!enter name", Toast.LENGTH_SHORT).show();
            etCustomerName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(etCustomerPhone.getText())) {
            Toast.makeText(this, "!enter phone number", Toast.LENGTH_SHORT).show();
            etCustomerPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(etCustomerAddress.getText())) {
            Toast.makeText(this, "!enter address", Toast.LENGTH_SHORT).show();
            etCustomerAddress.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(etCustomerEmail.getText())) {
            Toast.makeText(this, "!enter email address", Toast.LENGTH_SHORT).show();
            etCustomerEmail.requestFocus();
            return;
        }

        if (!radioHomeDelivery.isChecked() && !radioPickup.isChecked()) {
            Toast.makeText(this, "!select delivery method", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "!select date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(this, "!select time", Toast.LENGTH_SHORT).show();
            return;
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog);
        builder.setTitle("Confirm Order");
        builder.setMessage("Place order for " + productName + "?");
        builder.setPositiveButton("Yes", (d, w) -> saveOrderToRealtimeDatabase());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveOrderToRealtimeDatabase() {
        btnConfirmOrder.setEnabled(false);
        String orderId = "PP-" + System.currentTimeMillis();
        String userId = FirebaseHelper.getCurrentUserId();
        String method = radioHomeDelivery.isChecked() ? "Home Delivery" : "Pickup";
        Order order = new Order(orderId, userId, productName, selectedDate, "Active", price, quantity, material, printing, method, selectedTime, etCustomerName.getText().toString().trim(), etCustomerPhone.getText().toString().trim(), etCustomerEmail.getText().toString().trim(), etCustomerAddress.getText().toString().trim());
                FirebaseHelper.getOrdersRef().child(orderId).setValue(order).addOnCompleteListener(task -> {
            btnConfirmOrder.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Order Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MyOrdersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Order Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
