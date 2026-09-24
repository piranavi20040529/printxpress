package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SwitchMaterial switchPushNotify;
    private LinearLayout btnChangePwd, btnAbout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupListeners();
    }


    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        switchPushNotify = findViewById(R.id.switchPushNotify);
        btnChangePwd = findViewById(R.id.btnChangePwd);
        btnAbout = findViewById(R.id.btnAbout);
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        boolean isNotifyOn = sharedPreferences.getBoolean("PUSH_NOTIFY", true);
        switchPushNotify.setChecked(isNotifyOn);
    }


    private void setupListeners() {
        switchPushNotify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("PUSH_NOTIFY", isChecked).apply();
            String msg = isChecked ? "Notifications Enabled" : "Notifications Disabled";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });


        btnChangePwd.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
        });


        btnAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About PrintXPress")
                .setMessage("PrintXPress is your all-in-one professional printing partner. We specialize in bringing your creative visions to life.\n\nVersion: 1.0.4\nDeveloped by: Piranavi Thilainathan")
                .setPositiveButton("OK", null)
                .show();
    }
}
