package org.demidrol.age_mtx;

import static org.demidrol.age_mtx.constants.CommandId.CmdId_ACK;
import static org.demidrol.age_mtx.constants.CommandId.CmdId_Mpu_Flash;
import static org.demidrol.age_mtx.constants.CommandId.CmdId_Mpu_WiFi;
import static org.demidrol.age_mtx.constants.WifiSubcmdNand.wSC_Nand_PREPARE_HEADER_DATA_FILE;
import static org.demidrol.age_mtx.constants.WifiSubcmdSys.wSC_Sys_GET_INFO;
import static org.demidrol.age_mtx.constants.WifiSubcmdSys.wSC_Sys_READ_MEM;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.RouteInfo;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.demidrol.age_mtx.structures.AGE_DataFileHeadSys_Main;
import org.demidrol.age_mtx.structures.AGE_MainDataFileHeader;
import org.demidrol.age_mtx.structures.WifiDevInfo;
import org.demidrol.age_mtx.structures.WifiHeader;
import org.demidrol.age_mtx.structures.WifiPacket;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MainActivity extends Activity {
    private String TAG = "MyApp";

    private final int MTU = 1456;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    // UI components
    private EditText etSSID;
    private EditText etPSK;
    private Button btnScan;
    private TextView logTextView;
    private ScrollView scrollView;

    // Permissions
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };

    private static final long DOUBLE_BACK_INTERVAL = 2000; // 2 seconds
    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        etSSID = findViewById(R.id.etSSID);
        etPSK = findViewById(R.id.etPSK);
        btnScan = findViewById(R.id.btnConnect);
        logTextView = findViewById(R.id.logTextView);
        scrollView = findViewById(R.id.scrollView);

        connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties);
                for (RouteInfo route: linkProperties.getRoutes()) {
                    if (route.isDefaultRoute()) {
                        InetAddress server = route.getGateway();
                        logMessage("Connecting to server: " + server.getHostAddress());
                        try {
                            Socket sock = network.getSocketFactory().createSocket();
                            sock.connect(new InetSocketAddress(server, 22), 1000);
                            //getWifiDevInfo(sock);
                            getWifiNandInfo(sock);
                            sock.close();
                        } catch (IOException e) {
                            Log.e(TAG, "IOException: " + e);
                            throw new RuntimeException(e);
                        }
                        finally {
                            connectivityManager.unregisterNetworkCallback(this);
                            runOnUiThread(() -> {btnScan.setEnabled(true);});
                        }
                    }
                }
            }

            @Override
            public void onUnavailable() {
                runOnUiThread(() -> {
                    logMessage("Network unavailable");
                    btnScan.setEnabled(true);
                });
                super.onUnavailable();
            }
            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    logMessage("Network lost");
                    btnScan.setEnabled(true);
                });
                super.onLost(network);
            }
        };
        // Setup click listener for scan button
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] remaining_permissions =
                        Arrays.stream(REQUIRED_PERMISSIONS)
                                .filter((permission) -> (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED))
                                .toArray(String[]::new);
                if (remaining_permissions.length > 0) {
                    requestPermissions(remaining_permissions, PERMISSION_REQUEST_CODE);
                    return;
                }

                String ssidPattern = etSSID.getText().toString();
                String psk = etPSK.getText().toString();

                if (ssidPattern.isEmpty() || psk.isEmpty()) {
                    logMessage("Empty SSID or password");
                    return;
                }
                NetworkSpecifier networkSpecifier = new WifiNetworkSpecifier.Builder()
                        .setSsidPattern(new PatternMatcher(ssidPattern, PatternMatcher.PATTERN_PREFIX))
                        .setWpa2Passphrase(psk)
                        .build();
                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .setNetworkSpecifier(networkSpecifier)
                        .build();
                connectivityManager.requestNetwork(networkRequest, networkCallback, 5000);
                btnScan.setEnabled(false);
            }
        });
    }

    private boolean checkPermissions() {
        return Arrays.stream(REQUIRED_PERMISSIONS)
                .map((permission) -> (checkSelfPermission(permission)))
                .allMatch((x) -> (x == PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = Arrays.stream(grantResults)
                    .allMatch((permission) -> (permission == PackageManager.PERMISSION_GRANTED));

            logMessage(checkPermissions() ? "Permissions granted" : "Permissions denied");
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = SystemClock.uptimeMillis();

        if (currentTime - lastBackPressTime <= DOUBLE_BACK_INTERVAL) {
            finish();
        } else {
            lastBackPressTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void logMessage(final String message) {
        runOnUiThread(() -> {
            logTextView.append(message + "\n");
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            Log.d(TAG, message);
        });
    }

    private void displayInfo(final Serializable info) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, GenericInfoActivity.class);
            intent.putExtra("INFO", info);
            startActivity(intent);
        });
    }

    private void getWifiDevInfo(Socket s) throws IOException {
        WifiPacket req = new WifiPacket()
                .setCmd(CmdId_Mpu_WiFi)
                .setSubCmd(wSC_Sys_GET_INFO)
                .setErr(0);
        s.getOutputStream().write(req.serialize());
        byte[] data = new byte[MTU];
        int data_len = s.getInputStream().read(data);

        WifiPacket reply = new WifiPacket()
                .read(ByteBuffer
                        .wrap(data, 0, data_len)
                        .order(ByteOrder.LITTLE_ENDIAN));
        logMessage("Reply is: " + reply);
        WifiDevInfo wifiDevInfo = new WifiDevInfo()
                .read(ByteBuffer
                        .wrap(reply.getData())
                        .order(ByteOrder.LITTLE_ENDIAN));
        displayInfo(wifiDevInfo);
    }

    private void getWifiNandInfo(Socket s) throws IOException {
        long headerAddress = -1;
        {
            WifiPacket req = new WifiPacket()
                    .setCmd(CmdId_Mpu_Flash)
                    .setSubCmd(wSC_Nand_PREPARE_HEADER_DATA_FILE)
                    .setData(new byte[]{0x00});
            s.getOutputStream().write(req.serialize());
            byte[] data = new byte[MTU];
            int data_len = s.getInputStream().read(data);

            WifiPacket reply = new WifiPacket()
                    .read(ByteBuffer
                            .wrap(data, 0, data_len)
                            .order(ByteOrder.LITTLE_ENDIAN));
            logMessage("Reply is: " + reply);

            headerAddress = ByteBuffer
                    .wrap(reply.getData())
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt() & 0xffffffffL;
            logMessage(String.format("Header address: 0x%08x", headerAddress));
        }

        byte[] nandHeader = new byte[2*4096];
        ByteBuffer nandHeaderBuffer = ByteBuffer
                .wrap(nandHeader)
                .order(ByteOrder.LITTLE_ENDIAN);
        long chunkSize = 1024;
        for (long offset = 0; offset < nandHeader.length; offset += chunkSize) {
            byte[] memory_data = new byte[8];
            ByteBuffer
                    .wrap(memory_data)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt((int) chunkSize)
                    .putInt((int)(headerAddress + offset));

            WifiPacket req = new WifiPacket()
                    .setCmd(CmdId_Mpu_WiFi)
                    .setSubCmd(wSC_Sys_READ_MEM)
                    .setData(memory_data);
            s.getOutputStream().write(req.serialize());

            byte[] content = new byte[MTU];
            int content_len = s.getInputStream().read(content);
            WifiPacket reply = new WifiPacket()
                    .read(ByteBuffer
                            .wrap(content, 0, content_len)
                            .order(ByteOrder.LITTLE_ENDIAN));
            nandHeaderBuffer.put(reply.getData());
            logMessage(String.format("Read data at 0x%08x", headerAddress + offset));
        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < nandHeader.length; i += 64) {
//            for (int j = 0; j < 64; j += 1) {
//                sb.append(String.format("%02x", nandHeader[i+j]));
//            }
//            sb.append("\n");
//        }
//        logMessage(sb.toString());
        nandHeaderBuffer.flip();
        AGE_MainDataFileHeader hdr = new AGE_MainDataFileHeader()
                .read(nandHeaderBuffer);
        logMessage(hdr.toString());
    }
}
