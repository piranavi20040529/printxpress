package com.example.printxpress;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailReset, newPassword;
    private MaterialButton resetBtn;
    private ImageButton backBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailReset = findViewById(R.id.emailReset);
        newPassword = findViewById(R.id.newPassword);
        resetBtn = findViewById(R.id.resetBtn);
        backBtn = findViewById(R.id.backBtn);
        progressBar = findViewById(R.id.progressBar);

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        resetBtn.setOnClickListener(v -> {
            String email = emailReset.getText().toString().trim();
            String pwd = newPassword.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailReset.setError("Please enter a valid registered email");
                emailReset.requestFocus();
                return;
            }

            if (pwd.isEmpty() || pwd.length() < 6) {
                newPassword.setError("Password must be at least 6 characters");
                newPassword.requestFocus();
                return;
            }

            resetPassword(email);
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        resetBtn.setEnabled(false);

        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    resetBtn.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Forgot password successfully reset.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
