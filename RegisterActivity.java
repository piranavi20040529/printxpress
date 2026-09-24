package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.model.User;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEdit, emailEdit, phoneEdit, passwordEdit;
    private CheckBox termsCheck;
    private MaterialButton registerBtn;
    private TextView loginLink;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setupListeners();
    }

    private void initViews() {
        fullNameEdit = findViewById(R.id.fullNameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        termsCheck = findViewById(R.id.termsCheck);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        registerBtn.setOnClickListener(v -> validateAndRegister());
        View backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());
    }

    private void validateAndRegister() {
        String name = fullNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            fullNameEdit.setError("Name is required");
            fullNameEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Email is required");
            emailEdit.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Enter valid email");
            emailEdit.requestFocus();
            return;
        }
        if (phone.length() != 10) {
            phoneEdit.setError("Enter valid phone number (10 digits)");
            phoneEdit.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEdit.setError("Password must be at least 6 characters");
            passwordEdit.requestFocus();
            return;
        }
        if (!termsCheck.isChecked()) {
            Toast.makeText(this, "Accept terms first", Toast.LENGTH_SHORT).show();
            return;
        }
        performRegistration(name, email, phone, password);
    }

    private void performRegistration(String name, String email, String phone, String pwd) {
        registerBtn.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirebase(name, email, phone);
                    } else {
                        registerBtn.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirebase(String name, String email, String phone) {
        String userId = FirebaseHelper.getCurrentUserId();
        if (userId == null) return;
        User user = new User(name, email, phone);
        FirebaseHelper.getUsersRef().child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    registerBtn.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        saveToPrefs(name, email, phone, passwordEdit.getText().toString().trim());  // CHANGED: Added phone and password
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveToPrefs(String name, String email, String phone, String password) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit()
                .putString("FULL_NAME", name)
                .putString("USER_EMAIL", email)
                .putString("PHONE", phone)
                .putString("PASSWORD", password)
                .apply();
    }
}