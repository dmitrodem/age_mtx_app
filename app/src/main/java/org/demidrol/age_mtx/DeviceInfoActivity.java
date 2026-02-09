package org.demidrol.age_mtx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceInfoActivity extends AppCompatActivity {
    
    private TextView tvDeviceInfo;
    private TextView tvTitle;
    private Button btnBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        
        // Initialize UI components
        tvTitle = findViewById(R.id.tvTitle);
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        btnBack = findViewById(R.id.btnBack);
        
        // Get data from intent
        Intent intent = getIntent();
        String deviceInfo = intent.getStringExtra("EXTRA_DEVICE_INFO");
        String ssid = intent.getStringExtra("EXTRA_SSID");
        String bssid = intent.getStringExtra("EXTRA_BSSID");
        
        // Update UI
        if (ssid != null) {
            tvTitle.setText(getString(R.string.device_info_title) + ": " + ssid);
        }
        
        if (deviceInfo != null) {
            tvDeviceInfo.setText(deviceInfo);
        } else {
            tvDeviceInfo.setText("No device information available");
        }
        
        // Setup back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
}
