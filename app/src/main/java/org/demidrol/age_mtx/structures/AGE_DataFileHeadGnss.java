package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AGE_DataFileHeadGnss {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 32;
    public AGE_DataFileHeadGnss read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);
        buffer.position(buffer.position() + SIZEOF);
        return this;
    }
    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        return result;
    }
    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_DataFileHeadGnss {\n")
                .append(t).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}
