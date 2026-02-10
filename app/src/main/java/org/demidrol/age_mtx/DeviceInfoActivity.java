package org.demidrol.age_mtx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeviceInfoActivity extends Activity {

    private TextView tvDeviceInfo;
    private TextView tvTitle;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        // Initialize UI components
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        Intent intent = getIntent();
        WifiDevInfo info = (WifiDevInfo) intent.getSerializableExtra("EXTRA_DEVICE_INFO");
        String ssid = intent.getStringExtra("EXTRA_SSID");
        String bssid = intent.getStringExtra("EXTRA_BSSID");

        ((TextView)findViewById(R.id.tvDigitalDevType)).setText(String.format("%d", info.DigitalDevType));
        ((TextView)findViewById(R.id.tvInfoDevSize)).setText(String.format("%d", info.InfoDevSize));
        ((TextView)findViewById(R.id.tvSoftType)).setText(String.format("%d", info.SoftType));
        ((TextView)findViewById(R.id.tvAnalogDevType)).setText(String.format("%d", info.AnalogDevType));
        ((TextView)findViewById(R.id.tvSoftRevNum)).setText(String.format("%04d.%02d.%02d",
                2000 + info.SoftRevNum_year,
                info.SoftRevNum_month,
                info.SoftRevNum_day));
        ((TextView)findViewById(R.id.tvNandFlashType)).setText(String.format("%d", info.NandFlashType));
        ((TextView)findViewById(R.id.tvFactoryNum)).setText(String.format("%d", info.FactoryNum));
        ((TextView)findViewById(R.id.tvFpgaConfig)).setText(info.FpgaConfig ? "OK" : "Error");
        ((TextView)findViewById(R.id.tvConstSetting)).setText(info.ConstSetting ? "OK" : "Error");
        ((TextView)findViewById(R.id.tvBoardConnectState)).setText(String.format("%d", info.BoardConnectState));
        ((TextView)findViewById(R.id.tvWifiSerialNum)).setText(String.format("%d", info.WifiSerialNum));
        ((TextView)findViewById(R.id.tvWifiChannel)).setText(String.format("%d", info.WifiChannel));
        ((TextView)findViewById(R.id.tvWifiPassword)).setText(info.WifiPassword);
        // Update UI
        if (ssid != null) {
            tvTitle.setText(getString(R.string.device_info_title) + ": " + ssid);
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
