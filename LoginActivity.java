package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit, passwordEdit;
    private MaterialButton loginBtn;
    private ProgressBar progressBar;
    private TextView forgotPassword, registerLink;
    private boolean isNavigating = false;
    private String enteredPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        forgotPassword = findViewById(R.id.forgotPassword);
        registerLink = findViewById(R.id.registerLink);
    }

    private void setupListeners() {
        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        loginBtn.setOnClickListener(v -> {
            validateAndLogin();
        });
    }

    private void validateAndLogin() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (email.isEmpty()) { emailEdit.setError("Email required"); return; }
        if (password.isEmpty()) { passwordEdit.setError("Password required"); return; }

        enteredPassword = password;
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        FirebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchUserData(email);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);
                        handleError(task.getException());
                    }
                });
    }

    private void fetchUserData(String email) {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getUsersRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    String name = (user != null) ? user.getName() : email.split("@")[0];
                    String phone = (user != null && user.getPhone() != null) ? user.getPhone() : "";
                    navigateToHome(email, name, phone);
                }

                @Override public void onCancelled(@NonNull DatabaseError error) {
                    navigateToHome(email, email.split("@")[0], "");
                }
            });
        } else {
            navigateToHome(email, email.split("@")[0], "");
        }
    }

    private void navigateToHome(String email, String name, String phone) {
        if (isNavigating) return;
        isNavigating = true;

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit()
                .putString("FULL_NAME", name)
                .putString("USER_EMAIL", email)
                .putString("USER_NAME", name)
                .putString("PHONE", phone)
                .putString("PASSWORD", enteredPassword)
                .apply();

        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}