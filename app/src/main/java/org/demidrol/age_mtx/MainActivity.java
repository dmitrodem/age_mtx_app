package org.demidrol.age_mtx;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MyApp";
    // UI components
    private EditText etPattern;
    private Button btnScan;
    private TextView tvStatus;
    private ListView lvNetworks;
    
    // WiFi and scanning
    private WifiManager wifiManager;
    private boolean isScanning = false;
    private Handler handler;
    private Pattern pattern;
    
    // Double back press to exit
    private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds
    private long backPressedTime = 0;
    
    // Adapter for displaying networks
    private ArrayAdapter<NetworkDeviceItem> adapter;
    private List<NetworkDeviceItem> networkList;

    // Permissions
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
};
    
    // Receiver for WiFi scan results
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        public void onReceive(Context context, @NonNull Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                networkList.clear();
                if (pattern != null) {
                    for (ScanResult result : wifiManager.getScanResults()) {
                        if (pattern.matcher(result.SSID).find()) {
                            networkList.add(new NetworkDeviceItem(
                                    result.SSID,
                                    result.BSSID,
                                    result.level,
                                    result.frequency));
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                tvStatus.setText(getString(R.string.found_networks, networkList.size()));
                Log.i(TAG, "New scan results available: " + networkList.size());
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        etPattern = findViewById(R.id.etPattern);
        btnScan = findViewById(R.id.btnScan);
        tvStatus = findViewById(R.id.tvStatus);
        lvNetworks = findViewById(R.id.lvNetworks);
        
        // Initialize WiFi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        handler = new Handler(Looper.getMainLooper());
        
        // Setup network list adapter
        networkList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, networkList);
        lvNetworks.setAdapter(adapter);

        // Setup click listener for scan button
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) {
                    stopScanning();
                } else {
                    startScanning();
                }
            }
        });
        
        // Setup click listener for network items
        lvNetworks.setOnItemClickListener((parent, view, position, id) -> {
            if (isScanning) {
                stopScanning();
            }
            NetworkDeviceItem dev = networkList.get(position);
            onNetworkSelected(dev);
        });
    }
    
    private void startScanning() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }
        
        String patternText = etPattern.getText().toString().trim();
        if (patternText.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_pattern, Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_invalid_regex, Toast.LENGTH_SHORT).show();
            return;
        }
        
        isScanning = true;
        btnScan.setText(R.string.button_stop_scanning);
        tvStatus.setText(R.string.status_scanning);
        
        // Clear previous results
        networkList.clear();
        adapter.notifyDataSetChanged();
        
        // Register receiver
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
        
        startWifiScan();
    }
    
    private void stopScanning() {
        isScanning = false;
        btnScan.setText(R.string.button_start_scanning);
        tvStatus.setText(R.string.status_scanning_stopped);
        
        // Remove any pending scan callbacks
        handler.removeCallbacksAndMessages(null);
        
        try {
            unregisterReceiver(wifiScanReceiver);
        } catch (Exception e) {
            // Receiver was not registered
        }
    }
    
    private void startWifiScan() {
        if (!isScanning) {
            // Don't start scan if we're not in scanning mode
            return;
        }
        
        if (checkPermissions()) {
            if (wifiManager.startScan()) {
                tvStatus.setText(R.string.status_scanning);
            } else {
                tvStatus.setText(R.string.status_scan_failed);
                Toast.makeText(this, R.string.toast_scan_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                allGranted = allGranted && (result == PackageManager.PERMISSION_GRANTED);
            }
            if (allGranted) {
                Toast.makeText(this, R.string.toast_permissions_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_permissions_denied, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (isScanning) {
            unregisterReceiver(wifiScanReceiver);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
        handler.removeCallbacksAndMessages(null);
    }
    
    @Override
    public void onBackPressed() {
        if (backPressedTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            // Double back press detected - exit gracefully
            super.onBackPressed();
            finish();
        } else {
            // First back press - show toast message
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressedTime = System.currentTimeMillis();
        }
    }
    
    private void onNetworkSelected(NetworkDeviceItem dev) {
        final String ssid = dev.SSID;
        final String bssid = dev.BSSID;
        Toast.makeText(this, getString(R.string.toast_connecting, ssid), Toast.LENGTH_SHORT).show();
        new Thread(() -> queryDeviceInfo(ssid, bssid)).start();
    }
    
    private void queryDeviceInfo(String ssid, String bssid) {
        try {
            NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setBssid(MacAddress.fromString(bssid))
                    .setWpa2Passphrase("password_age")
                    .build();
            NetworkRequest request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build();
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            ConnectivityManager.NetworkCallback cb = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Log.i(TAG, "Found requested wifi network");
                    try {
                        Socket socket = network.getSocketFactory().createSocket();
                        socket.setSoTimeout(5000);
                        socket.connect(new InetSocketAddress("192.168.4.1", 22));
                        Log.i(TAG, "Successfully connected to a socket");
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        byte[] msg = WifiDevInfo.create_request();
                        out.write(msg);
                        out.flush();
                        Log.i(TAG, "Sent request");

                        byte[] reply = new byte[1456];
                        int r = input.read(reply);

                        Log.i(TAG, "Received " + r + " bytes");
                        HexFormat hf = java.util.HexFormat.ofDelimiter(" ");
                        String hexdump = String.format("Msg = %s", hf.formatHex(reply, 0, r));
                        Log.i(TAG, hexdump);
                        socket.close();
                        WifiDevInfo devInfo = new WifiDevInfo(reply);
                        Log.i(TAG, "WifiDevInfo = " + devInfo);
                        connectivityManager.unregisterNetworkCallback(this);
                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                            intent.putExtra("EXTRA_DEVICE_INFO", devInfo.toString());
                            intent.putExtra("EXTRA_SSID", ssid);
                            intent.putExtra("EXTRA_BSSID", bssid);
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Something bad");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                    for (LinkAddress addr: linkProperties.getLinkAddresses()) {
                        Log.i(TAG, "Link address = " + addr);
                    }
                }
            };
            connectivityManager.requestNetwork(request, cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
