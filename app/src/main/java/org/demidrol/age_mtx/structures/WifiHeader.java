package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;

public class WifiHeader {
    private byte cmd;              // Command code
    private byte subCmd;           // Sub-command code
    private short transportLen;    // Transmitted data size (DMA-aligned, multiple of 8)
    private short realLen;         // Actual useful data size
    private short err;            // Error codes

    public void read(ByteBuffer buffer) {
        cmd = buffer.get();
        subCmd = buffer.get();
        transportLen = buffer.getShort();
        realLen = buffer.getShort();
        err = buffer.getShort();
    }
    public int getCmd() {return cmd & 0xff; }
    public int getSubCmd() {return subCmd & 0xff; }
    public int getTransportLen() {return transportLen & 0xffff; }
    public int getRealLen() {return realLen & 0xffff; }
    public int getErr() {return err & 0xffff; }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WifiHeader {\n");
        sb.append(String.format("  cmd = 0x%02x,\n", getCmd()));
        sb.append(String.format("  subcmd = 0x%02x,\n", getSubCmd()));
        sb.append(String.format("  transportLen = %d,\n", getTransportLen()));
        sb.append(String.format("  realLen = %d,\n", getRealLen()));
        sb.append(String.format("  err = %d\n", getErr()));
        sb.append("}");
        return sb.toString();
    }
}
