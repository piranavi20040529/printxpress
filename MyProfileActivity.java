package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText fullNameInput, emailInput, phoneInput, preferencesInput;
    private MaterialButton updateProfileBtn, changePasswordBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initViews();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        preferencesInput = findViewById(R.id.preferencesInput);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserData() {
        String fullName = sharedPreferences.getString("FULL_NAME", "");
        String email = sharedPreferences.getString("USER_EMAIL", "");
        String phone = sharedPreferences.getString("PHONE", "");
        String preferences = sharedPreferences.getString("PREFERENCES", "Email Notifications, SMS Alerts");

        fullNameInput.setText(fullName);
        emailInput.setText(email);
        phoneInput.setText(phone);
        preferencesInput.setText(preferences);
    }

    private void setupClickListeners() {
        updateProfileBtn.setOnClickListener(v -> updateProfile());
        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void updateProfile() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String preferences = preferencesInput.getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameInput.setError("Full name is required");
            fullNameInput.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            phoneInput.setError("Phone number is required");
            phoneInput.requestFocus();
            return;
        }


        editor.putString("FULL_NAME", fullName);
        editor.putString("USER_EMAIL", email);
        editor.putString("PHONE", phone);
        editor.putString("PREFERENCES", preferences);
        editor.apply();


        String userId = FirebaseHelper.getCurrentUserId();
        if (userId != null) {
            User user = new User(fullName, email, phone);
            FirebaseHelper.getUsersRef().child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Profile saved locally only", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog);

        TextView titleView = new TextView(this);
        titleView.setText("Change Password");
        titleView.setPadding(60, 50, 60, 0);
        titleView.setTextSize(22);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#1A237E"));
        builder.setCustomTitle(titleView);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final TextInputEditText currentPasswordInput = createPasswordEditText("Current Password");
        final TextInputEditText newPasswordInput = createPasswordEditText("New Password");
        final TextInputEditText confirmPasswordInput = createPasswordEditText("Confirm New Password");

        layout.addView(currentPasswordInput);
        layout.addView(newPasswordInput);
        layout.addView(confirmPasswordInput);
        builder.setView(layout);

        builder.setPositiveButton("CHANGE", (dialog, which) -> {
            String currentPwd = currentPasswordInput.getText().toString();
            String newPwd = newPasswordInput.getText().toString();
            String confirmPwd = confirmPasswordInput.getText().toString();

            if (currentPwd.isEmpty()) {
                Toast.makeText(this, "Enter current password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.isEmpty()) {
                Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), currentPwd)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPwd)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            editor.putString("PASSWORD", newPwd);
                                            editor.apply();
                                            Toast.makeText(MyProfileActivity.this, "Password changed successfully!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(MyProfileActivity.this, "Failed: " + task2.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MyProfileActivity.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private TextInputEditText createPasswordEditText(String hint) {
        TextInputEditText editText = new TextInputEditText(this);
        editText.setHint(hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return editText;
    }
}