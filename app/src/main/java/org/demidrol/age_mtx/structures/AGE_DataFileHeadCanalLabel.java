package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AGE_DataFileHeadCanalLabel {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 16;
    private short pkt;              // Пикет канала
    private short prf;              // Профиль канала
    private float razm;             // Размер датчика (в метрах)
    private short nvt;              // Число витков в петле (uint16_t)
    private byte idk;               // Код имени канала - номер по списку (BZ,EX,EY,HX,HY,HZ,U,I,-)
    private byte ttk;               // Тип сенсора - номер по списку
    // 4 bytes of reserve follow automatically

    public AGE_DataFileHeadCanalLabel read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);

        pkt = buffer.getShort();
        prf = buffer.getShort();
        razm = buffer.getFloat();
        nvt = buffer.getShort();
        idk = buffer.get();
        ttk = buffer.get();

        // Skip 4 bytes of reserve
        buffer.position(buffer.position() + 4);

        return this;
    }

    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        ByteBuffer buffer = ByteBuffer
                .wrap(result)
                .order(BYTE_ORDER);

        buffer.putShort(pkt);
        buffer.putShort(prf);
        buffer.putFloat(razm);
        buffer.putShort(nvt);
        buffer.put(idk);
        buffer.put(ttk);

        // Add 4 bytes of reserve (padding)
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);

        return result;
    }

    // Getters and setters
    public int getPkt() {
        return pkt;
    }

    public AGE_DataFileHeadCanalLabel setPkt(int pkt) {
        this.pkt = (short) pkt;
        return this;
    }

    public int getPrf() {
        return prf;
    }

    public AGE_DataFileHeadCanalLabel setPrf(int prf) {
        this.prf = (short) prf;
        return this;
    }

    public float getRazm() {
        return razm;
    }

    public AGE_DataFileHeadCanalLabel setRazm(float razm) {
        this.razm = razm;
        return this;
    }

    public int getNvt() {
        return nvt & 0xFFFF;
    }

    public AGE_DataFileHeadCanalLabel setNvt(int nvt) {
        this.nvt = (short) nvt;
        return this;
    }

    public int getIdk() {
        return idk & 0xFF;
    }

    public AGE_DataFileHeadCanalLabel setIdk(int idk) {
        this.idk = (byte) idk;
        return this;
    }

    public int getTtk() {
        return ttk & 0xFF;
    }

    public AGE_DataFileHeadCanalLabel setTtk(int ttk) {
        this.ttk = (byte) ttk;
        return this;
    }

    // Convenience methods for channel name constants
    public static final int CHANNEL_BZ = 1;
    public static final int CHANNEL_EX = 2;
    public static final int CHANNEL_EY = 3;
    public static final int CHANNEL_HX = 4;
    public static final int CHANNEL_HY = 5;
    public static final int CHANNEL_HZ = 6;
    public static final int CHANNEL_U = 7;
    public static final int CHANNEL_I = 8;
    public static final int CHANNEL_NONE = 9;

    public String getIdkAsString() {
        int idkValue = idk & 0xFF;
        switch (idkValue) {
            case CHANNEL_BZ: return "BZ";
            case CHANNEL_EX: return "EX";
            case CHANNEL_EY: return "EY";
            case CHANNEL_HX: return "HX";
            case CHANNEL_HY: return "HY";
            case CHANNEL_HZ: return "HZ";
            case CHANNEL_U: return "U";
            case CHANNEL_I: return "I";
            case CHANNEL_NONE: return "-";
            default: return "UNKNOWN(" + idkValue + ")";
        }
    }

    public AGE_DataFileHeadCanalLabel setIdkFromString(String channelName) {
        if (channelName == null) {
            this.idk = (byte) CHANNEL_NONE;
            return this;
        }

        switch (channelName.toUpperCase()) {
            case "BZ": this.idk = (byte) CHANNEL_BZ; break;
            case "EX": this.idk = (byte) CHANNEL_EX; break;
            case "EY": this.idk = (byte) CHANNEL_EY; break;
            case "HX": this.idk = (byte) CHANNEL_HX; break;
            case "HY": this.idk = (byte) CHANNEL_HY; break;
            case "HZ": this.idk = (byte) CHANNEL_HZ; break;
            case "U": this.idk = (byte) CHANNEL_U; break;
            case "I": this.idk = (byte) CHANNEL_I; break;
            case "-":
            case "NONE": this.idk = (byte) CHANNEL_NONE; break;
            default: throw new IllegalArgumentException("Unknown channel name: " + channelName);
        }
        return this;
    }

    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_DataFileHeadCanalLabel {\n")
                .append(t).append("  pkt=").append(pkt).append("\n")
                .append(t).append("  prf=").append(prf).append("\n")
                .append(t).append("  razm=").append(razm).append("\n")
                .append(t).append("  nvt=").append(nvt & 0xFFFF).append("\n")
                .append(t).append("  idk=").append(idk & 0xFF).append(" ('").append(getIdkAsString()).append("')\n")
                .append(t).append("  ttk=").append(ttk & 0xFF).append("\n")
                .append(t).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}
