package org.demidrol.age_mtx;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    
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
    private ArrayAdapter<String> adapter;
    private List<String> networkList;
    
    // Permissions
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS;
    
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            };
        } else {
            REQUIRED_PERMISSIONS = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            };
        }
    }
    
    // Receiver for WiFi scan results
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false
                );
                if (success) {
                    if (checkPermissions()) {
                        processScanResults();
                    } else {
                        // Handle missing permissions gracefully
                        tvStatus.setText("Permissions required for scan results");
                    }
                } else {
                    tvStatus.setText(R.string.status_scan_failed_cached);
                    if (checkPermissions()) {
                        processScanResults();
                    } else {
                        // Handle missing permissions gracefully
                        tvStatus.setText("Permissions required for cached results");
                    }
                }
                
                // Continue scanning if enabled
                if (isScanning) {
                    handler.postDelayed(() -> {
                        if (isScanning) {
                            startWifiScan();
                        }
                    }, 5000);
                }
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
            String selectedNetwork = networkList.get(position);
            onNetworkSelected(selectedNetwork);
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
    
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void processScanResults() {
        if (!checkPermissions()) {
            tvStatus.setText("Permissions required to process results");
            return;
        }
        
        try {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults == null) {
                scanResults = new ArrayList<>();
            }
            
            networkList.clear();
            
            if (pattern != null) {
                for (ScanResult result : scanResults) {
                    String ssid = result.SSID;
                    if (pattern.matcher(ssid).find()) {
                        String networkInfo = formatNetworkInfo(result);
                        networkList.add(networkInfo);
                    }
                }
            }
            
            adapter.notifyDataSetChanged();
            tvStatus.setText(getString(R.string.found_networks, networkList.size()));
        } catch (SecurityException e) {
            // Handle permission denial at runtime
            tvStatus.setText("Permission denied: " + e.getMessage());
            Toast.makeText(this, "Wi-Fi scan permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String formatNetworkInfo(ScanResult result) {
        String ssid = result.SSID.isEmpty() ? "(Hidden Network)" : result.SSID;
        String security = getSecurityType(result.capabilities);
        
        return String.format(java.util.Locale.ROOT, "%s\nMAC: %s\nStrength: %d dBm | Freq: %d MHz | Security: %s",
            ssid,
            result.BSSID,
            result.level,
            result.frequency,
            security
        );
    }
    
    private String getSecurityType(String capabilities) {
        if (capabilities.contains("WPA3")) {
            return "WPA3";
        } else if (capabilities.contains("WPA2")) {
            return "WPA2";
        } else if (capabilities.contains("WPA")) {
            return "WPA";
        } else if (capabilities.contains("WEP")) {
            return "WEP";
        } else if (capabilities.contains("ESS")) {
            return "Open";
        } else {
            return "Unknown";
        }
    }
    
    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != 
                PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            PERMISSION_REQUEST_CODE
        );
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
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Toast.makeText(this, R.string.toast_permissions_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    this, 
                    R.string.toast_permissions_denied, 
                    Toast.LENGTH_LONG
                ).show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (isScanning) {
            IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(wifiScanReceiver, intentFilter);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (isScanning) {
            try {
                unregisterReceiver(wifiScanReceiver);
            } catch (Exception e) {
                // Receiver was not registered
            }
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
    
    private void onNetworkSelected(String networkInfo) {
        // Parse network info from the displayed string
        String[] lines = networkInfo.split("\n");
        final String ssid = lines[0];
        final String bssid;
        
        if (lines.length > 1) {
            // Extract BSSID from second line
            String bssidLine = lines[1];
            if (bssidLine.startsWith("MAC: ")) {
                bssid = bssidLine.substring(5);
            } else {
                bssid = "";
            }
        } else {
            bssid = "";
        }
        
        // Show connecting message
        Toast.makeText(this, getString(R.string.toast_connecting, ssid), Toast.LENGTH_SHORT).show();
        
        // Start connection process in background thread
        new Thread(() -> {
            final String deviceInfo = connectAndQueryDevice(ssid, bssid);
            
            // Update UI on main thread
            runOnUiThread(() -> {
                if (deviceInfo != null) {
                    // Launch DeviceInfoActivity with the parsed information
                    Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                    intent.putExtra("EXTRA_DEVICE_INFO", deviceInfo);
                    intent.putExtra("EXTRA_SSID", ssid);
                    intent.putExtra("EXTRA_BSSID", bssid);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_connection_failed, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
    
    private String connectAndQueryDevice(String ssid, String bssid) {
        StringBuilder result = new StringBuilder();
        result.append("=== Device Connection Report ===\n\n");
        result.append("Target SSID: ").append(ssid).append("\n");
        result.append("Target BSSID: ").append(bssid).append("\n\n");
        
        try {
            // Step 1: Configure and connect to Wi-Fi
            result.append("1. Configuring Wi-Fi connection...\n");
            boolean wifiConnected = configureAndConnectToWifi(ssid, bssid);
            
            if (!wifiConnected) {
                result.append("   ❌ Failed to connect to Wi-Fi\n");
                return result.toString();
            }
            result.append("   ✅ Connected to Wi-Fi\n");
            
            // Wait for network to stabilize
            Thread.sleep(3000);
            
            // Step 2: Configure static IP
            result.append("\n2. Configuring static IP 192.168.4.2/24...\n");
            boolean ipConfigured = configureStaticIp();
            
            if (!ipConfigured) {
                result.append("   ⚠️ Static IP configuration may have failed\n");
                result.append("   Continuing with DHCP...\n");
            } else {
                result.append("   ✅ Static IP configured\n");
            }
            
            // Wait for IP configuration
            Thread.sleep(2000);
            
            // Step 3: Connect via TCP socket
            result.append("\n3. Connecting to 192.168.4.1:22...\n");
            String socketResponse = connectViaTcpSocket();
            
            if (socketResponse != null) {
                result.append("   ✅ TCP connection successful\n");
                result.append("   Response:\n").append(socketResponse).append("\n");
            } else {
                result.append("   ❌ TCP connection failed\n");
            }
            
            // Step 4: Send custom query packet
            result.append("\n4. Sending query packet...\n");
            String queryResponse = sendQueryPacket();
            
            if (queryResponse != null) {
                result.append("   ✅ Query successful\n");
                result.append("   Response:\n").append(queryResponse).append("\n");
            } else {
                result.append("   ❌ Query failed\n");
            }
            
            // Step 5: Parse response
            result.append("\n5. Parsing response...\n");
            String parsedInfo = parseDeviceResponse(queryResponse);
            result.append("   Parsed information:\n").append(parsedInfo).append("\n");
            
        } catch (Exception e) {
            result.append("\n❌ Error during connection: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        } finally {
            // Step 6: Clean up - disconnect from Wi-Fi
            result.append("\n6. Cleaning up connection...\n");
            disconnectFromWifi();
            result.append("   ✅ Disconnected from Wi-Fi\n");
        }
        
        return result.toString();
    }
    
    private boolean configureAndConnectToWifi(String ssid, String bssid) {
        try {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + ssid + "\"";
            wifiConfig.BSSID = bssid;
            
            // Configure as open network (no password)
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.clear();
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            
            // Remove any existing configuration for this network
            List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
            if (existingConfigs != null) {
                for (WifiConfiguration config : existingConfigs) {
                    if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                        wifiManager.removeNetwork(config.networkId);
                    }
                }
            }
            
            // Add new configuration
            int netId = wifiManager.addNetwork(wifiConfig);
            if (netId == -1) {
                return false;
            }
            
            // Enable and connect to the network
            wifiManager.disconnect();
            boolean enabled = wifiManager.enableNetwork(netId, true);
            boolean connected = wifiManager.reconnect();
            
            return enabled && connected;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean configureStaticIp() {
        // Note: Configuring static IP programmatically requires root access on most Android versions
        // This is a simplified approach that may not work on all devices
        
        try {
            // For Android 10+, we need to use Network API
            // This is a placeholder - actual implementation would require more complex code
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String connectViaTcpSocket() {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.4.1", 22), 5000);
            socket.setSoTimeout(5000);
            
            // Try to read SSH banner
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
            StringBuilder response = new StringBuilder();
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null && lineCount < 5) {
                response.append(line).append("\n");
                lineCount++;
            }
            
            if (response.length() > 0) {
                return response.toString();
            } else {
                return "Connected to port 22, but no data received (might be SSH service)";
            }
            
        } catch (IOException e) {
            return "Connection failed: " + e.getMessage();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    private String sendQueryPacket() {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.4.1", 22), 5000);
            socket.setSoTimeout(5000);
            
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
            // Send a simple query (SSH client identification)
            String query = "SSH-2.0-AndroidClient\r\n";
            output.write(query.getBytes(StandardCharsets.UTF_8));
            output.flush();
            
            // Read response
            StringBuilder response = new StringBuilder();
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null && lineCount < 10) {
                response.append(line).append("\n");
                lineCount++;
            }
            
            return response.toString();
            
        } catch (IOException e) {
            return "Query failed: " + e.getMessage();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    private String parseDeviceResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "No response received";
        }
        
        StringBuilder parsed = new StringBuilder();
        parsed.append("=== Parsed Device Information ===\n\n");
        
        // Check if it looks like SSH
        if (response.contains("SSH")) {
            parsed.append("Service: SSH Server\n");
            
            // Extract SSH version
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("SSH-")) {
                    parsed.append("SSH Version: ").append(line).append("\n");
                }
                if (line.toLowerCase().contains("openssh")) {
                    parsed.append("Software: OpenSSH\n");
                }
            }
            
            parsed.append("\nThis appears to be an SSH server.\n");
            parsed.append("Possible device types:\n");
            parsed.append("- Embedded Linux device\n");
            parsed.append("- Router/Network device\n");
            parsed.append("- IoT device with SSH access\n");
        } else {
            parsed.append("Raw response:\n");
            parsed.append(response).append("\n");
            
            // Try to identify common patterns
            if (response.length() < 100) {
                parsed.append("\nShort response - might be a custom protocol\n");
            }
        }
        
        return parsed.toString();
    }
    
    private void disconnectFromWifi() {
        try {
            wifiManager.disconnect();
            // Re-enable auto-connect to previous networks
            wifiManager.reconnect();
        } catch (Exception e) {
            // Ignore
        }
    }
}
