package org.demidrol.age_mtx;

public class NetworkDeviceItem {

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
        return String.format("%s{SSID=%s, BSSID=%s, level=%d dBm, frequency=%d MHz}",
                this.getClass().toString(),
                SSID,
                BSSID,
                level,
                frequency);
    }
}
