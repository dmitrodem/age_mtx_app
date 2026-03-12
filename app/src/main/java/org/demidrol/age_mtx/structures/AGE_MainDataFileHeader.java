package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class AGE_MainDataFileHeader {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 3664;
    private AGE_DataFileHeadSys_Main sys = new AGE_DataFileHeadSys_Main();
    private AGE_DataFileHeadAdc_Main adc = new AGE_DataFileHeadAdc_Main();
    private AGE_DataFileHeadGgInfo gg = new AGE_DataFileHeadGgInfo();
    private AGE_DataFileHeadGnss gnss = new AGE_DataFileHeadGnss();
    private AGE_DataFileHeadVoltage voltage = new AGE_DataFileHeadVoltage();
    private AGE_DataFileHeadGeneralLabel gLabel = new AGE_DataFileHeadGeneralLabel();
    private ArrayList<AGE_DataFileHeadCanalLabel> cLabel = new ArrayList<>(5);

    public AGE_MainDataFileHeader read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);
        sys.read(buffer);
        adc.read(buffer);
        gg.read(buffer);
        gnss.read(buffer);
        voltage.read(buffer);
        gLabel.read(buffer);
        cLabel.clear();
        for (int i = 0; i < 5; i++) {
            AGE_DataFileHeadCanalLabel label = new AGE_DataFileHeadCanalLabel();
            label.read(buffer);
            cLabel.add(label);
        }
        return this;
    }
    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        ByteBuffer buffer = ByteBuffer
                .wrap(result)
                .order(BYTE_ORDER);

        buffer.put(sys.serialize());
        buffer.put(adc.serialize());
        buffer.put(gg.serialize());
        buffer.put(gnss.serialize());
        buffer.put(voltage.serialize());
        buffer.put(gLabel.serialize());

        // Serialize 5 cLabel instances
        for (int i = 0; i < 5; i++) {
            if (cLabel != null && i < cLabel.size()) {
                buffer.put(cLabel.get(i).serialize());
            } else {
                // Handle missing labels - write zeros or default values
                AGE_DataFileHeadCanalLabel emptyLabel = new AGE_DataFileHeadCanalLabel();
                buffer.put(emptyLabel.serialize());
            }
        }
        return result;
    }

    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        int o = offset+1;
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_MainDataFileHeader {\n")
                .append(t).append("  sys=").append("\n")
                .append(sys.toStringWithOffset(o)).append("\n")
                .append(t).append("  adc=\n")
                .append(adc.toStringWithOffset(o)).append("\n")
                .append(t).append("  gg=\n")
                .append(gg.toStringWithOffset(o)).append("\n")
                .append(t).append("  gnss=\n")
                .append(gnss.toStringWithOffset(o)).append("\n")
                .append(t).append("  voltage=\n")
                .append(voltage.toStringWithOffset(o)).append("\n")
                .append(t).append("  gLabel=\n")
                .append(gLabel.toStringWithOffset(o)).append("\n")
                .append(t).append("  cLabel=[\n");
        if (cLabel != null) {
            for (int i = 0; i < Math.min(cLabel.size(), 5); i++) {
                sb
                        .append(t).append("    [").append(i).append("]=\n")
                        .append(t).append(cLabel.get(i).toStringWithOffset(o+1)).append("\n");
            }
        }
        sb
                .append(t).append("  ]\n}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}
