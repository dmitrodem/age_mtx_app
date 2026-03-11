package org.demidrol.age_mtx;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.demidrol.age_mtx.structures.WifiDevInfo;
import org.demidrol.age_mtx.structures.WifiHeader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MainActivity extends Activity {
    private String TAG = "MyApp";

    private NetworkSpecifier networkSpecifier;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    // UI components
    private Button btnScan;

    // Permissions
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        btnScan = findViewById(R.id.btnScan);

        networkSpecifier = new WifiNetworkSpecifier.Builder()
                .setSsidPattern(new PatternMatcher("age_dev_N",  PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase("password_age")
                .build();
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(networkSpecifier)
                .build();
        connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Network available: " + network);
                try {
                    Socket sock = network.getSocketFactory().createSocket();
                    sock.connect(new InetSocketAddress("192.168.4.1", 22), 1000);
                    byte[] request = WifiDevInfo.create_request();
                    sock.getOutputStream().write(request);
                    byte[] reply = new byte[1456];
                    int r = sock.getInputStream().read(reply);
                    sock.close();
                    ByteBuffer buf = ByteBuffer.wrap(reply);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    WifiHeader wifiHeader = new WifiHeader();
                    WifiDevInfo wifiDevInfo = new WifiDevInfo();
                    wifiHeader.read(buf);
                    wifiDevInfo.read(buf);
                    Log.i(TAG, wifiHeader.toString());
                    Log.i(TAG, wifiDevInfo.toString());
                    connectivityManager.unregisterNetworkCallback(this);
                } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e);
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "Connection OK");
                connectivityManager.unregisterNetworkCallback(this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnScan.setEnabled(true);
                    }
                });
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
                connectivityManager.requestNetwork(networkRequest, networkCallback);
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

            Toast.makeText(this,
                    checkPermissions() ? R.string.toast_permissions_granted : R.string.toast_permissions_denied,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
