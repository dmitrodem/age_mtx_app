package org.demidrol.age_mtx;

import java.io.Serializable;

public class NetworkDeviceItem implements Serializable {

    public String SSID;
    public String BSSID;
    public int level;
    public int frequency;
    public NetworkDeviceItem(String SSID, String BSSID, int level, int frequency) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.level = level;
        this.frequency = frequency;
    }
    @Override
    public String toString() {
        return String.format("SSID %s\nBSSID %s\nlevel %d dBm\nfrequency %d MHz",
                SSID,
                BSSID,
                level,
                frequency);
    }
}
