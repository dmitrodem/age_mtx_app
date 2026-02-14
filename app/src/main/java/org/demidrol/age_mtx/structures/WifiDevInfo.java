package org.demidrol.age_mtx.structures;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WifiDevInfo implements Serializable {
    public byte digitalDevType;     // Controller type (200 = ARM Cortex-A5)
    public byte infoDevSize;        // Actual size of AGE_DEV_INFO structure in bytes
    public byte softType;           // Software type running on the device
    public byte analogDevType;      // Analog part type
    public byte softRevNum_day;     // Software release - day
    public byte softRevNum_month;   // Software release - month
    public byte softRevNum_year;    // Software release - year (2000 + value)
    public byte nandFlashType;      // NAND Flash memory type
    public short factoryNum;        // Device factory number
    public boolean fpgaConfig;      // FPGA configuration flag
    public boolean constSetting;    // System constants written to SPI Flash flag
    public byte boardConnectState;  // ADC board connection state (signed int8_t)
    public byte wifiSerialNum;      // WiFi network device number (1-254)
    public byte wifiChannel;        // WiFi working channel
    public byte[] wifiPassword = new byte[22];  // WiFi password

    static byte[] create_request() {
        byte[] request = new byte[1456];
        request[0] = 0x02;
        request[1] = 0x06;
        return request;
    }
    public void read(ByteBuffer buffer) {
        digitalDevType = buffer.get();
        infoDevSize = buffer.get();
        softType = buffer.get();
        analogDevType = buffer.get();
        softRevNum_day = buffer.get();
        softRevNum_month = buffer.get();
        softRevNum_year = buffer.get();
        nandFlashType = buffer.get();
        factoryNum = buffer.getShort();
        fpgaConfig = buffer.get() != 0;
        constSetting = buffer.get() != 0;
        boardConnectState = buffer.get();
        wifiSerialNum = buffer.get();
        wifiChannel = buffer.get();
        buffer.get(wifiPassword);
    }
    // Getters with proper unsigned conversions
    public int getDigitalDevType() { return digitalDevType & 0xFF; }
    public int getInfoDevSize() { return infoDevSize & 0xFF; }
    public int getSoftType() { return softType & 0xFF; }
    public int getAnalogDevType() { return analogDevType & 0xFF; }
    public int getSoftRevNumDay() { return softRevNum_day & 0xFF; }
    public int getSoftRevNumMonth() { return softRevNum_month & 0xFF; }
    public int getSoftRevNumYear() { return 2000 + (softRevNum_year & 0xFF); }
    public int getNandFlashType() { return nandFlashType & 0xFF; }
    public int getFactoryNum() { return factoryNum & 0xFFFF; }
    public boolean getFpgaConfig() {return fpgaConfig; }
    public boolean getConstSetting() {return constSetting; }
    public int getBoardConnectState() { return boardConnectState; } // byte is signed
    public int getWifiSerialNum() { return wifiSerialNum & 0xFF; }
    public int getWifiChannel() { return wifiChannel & 0xFF; }
    public String getWifiPassword() {
        // Find null terminator if present
        int len = 0;
        while (len < wifiPassword.length && wifiPassword[len] != 0) len++;
        return new String(wifiPassword, 0, len, StandardCharsets.US_ASCII);
    }
    public String getSoftwareReleaseDate() {
        return String.format("%04d-%02d-%02d",
                getSoftRevNumYear(),
                getSoftRevNumMonth(),
                getSoftRevNumDay());
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WifiDevInfo {\n");
        sb.append(String.format("  DigitalDevType: %d\n", getDigitalDevType()));
        sb.append(String.format("  InfoDevSize: %d bytes\n", getInfoDevSize()));
        sb.append(String.format("  SoftType: %d\n", getSoftType()));
        sb.append(String.format("  AnalogDevType: %d\n", getAnalogDevType()));
        sb.append(String.format("  Software Release: %s\n", getSoftwareReleaseDate()));
        sb.append(String.format("  NandFlashType: %d\n", getNandFlashType()));
        sb.append(String.format("  FactoryNum: %d\n", getFactoryNum()));
        sb.append(String.format("  FPGA Config: %s\n", fpgaConfig));
        sb.append(String.format("  Const Setting: %s\n", constSetting));
        sb.append(String.format("  BoardConnectState: %d\n", boardConnectState));
        sb.append(String.format("  WiFi SerialNum: %d\n", getWifiSerialNum()));
        sb.append(String.format("  WiFi Channel: %d\n", getWifiChannel()));
        sb.append(String.format("  WiFi Password: '%s'\n", getWifiPassword()));
        sb.append("}");
        return sb.toString();
    }
}
