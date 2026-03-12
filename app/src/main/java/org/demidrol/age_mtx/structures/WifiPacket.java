package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WifiPacket {
    private static int CAPACITY = 2048;
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private byte cmd;
    private byte subCmd;
    private short transportLen;
    private short realLen;
    private short err;
    private byte[] data;

    public WifiPacket read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);
        cmd = buffer.get();
        subCmd = buffer.get();
        transportLen = buffer.getShort();
        realLen = buffer.getShort();
        err = buffer.getShort();
        if (realLen != 0) {
            data = new byte[realLen];
            buffer.get(data, 0, realLen);
        }
        return this;
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        buffer.order(BYTE_ORDER);
        buffer.put(cmd);
        buffer.put(subCmd);
        buffer.putShort(transportLen);
        buffer.putShort(realLen);
        buffer.putShort(err);
        if (data != null) {
            buffer.put(data);
        }
        byte[] result = new byte[buffer.position()];
        buffer.flip();
        buffer.get(result);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("WifiPacket {\n")
                .append("  cmd=").append(cmd).append("\n")
                .append("  subCmd=").append(subCmd).append("\n")
                .append("  transportLen=").append(transportLen).append("\n")
                .append("  realLen=").append(realLen).append("\n")
                .append("  err=").append(err).append("\n")
                .append("  data=");
        for (byte b: data) {
            sb.append(String.format("%02x", b));
        }
        sb.append("\n").append("}");
        return sb.toString();
    }
    public int getCmd() {
        return (int)cmd;
    }
    public <T extends Enum<T>> WifiPacket setCmd(T cmd) {
        this.cmd = (byte) (cmd.ordinal() & 0xff);
        return this;
    }
    public int getSubCmd() {
        return (int)subCmd;
    }
    public <T extends Enum<T>> WifiPacket setSubCmd(T subCmd) {
        this.subCmd = (byte) (subCmd.ordinal() & 0xff);
        return this;
    }

    public short getTransportLen() {
        return transportLen;
    }

    public WifiPacket setTransportLen(int transportLen) {
        this.transportLen = (short) (transportLen & 0xffff);
        return this;
    }

    public int getRealLen() {
        return (int)realLen;
    }
    public WifiPacket setRealLen(int realLen) {
        this.realLen = (short) (realLen & 0xffff);
        return this;
    }
    public int getErr() {
        return (int)err;
    }
    public WifiPacket setErr(int err) {
        this.err = (short) (err & 0xffff);
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public WifiPacket setData(byte[] data) {
        this.data = data;
        setRealLen(data.length);
        setTransportLen(data.length);
        return this;
    }
}
