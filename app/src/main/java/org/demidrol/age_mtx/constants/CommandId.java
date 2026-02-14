package org.demidrol.age_mtx.constants;

public enum CommandId {
    CmdId_ACK(0),
    CmdId_WiFiInt(1), //
    CmdId_Mpu_WiFi(2),
    CmdId_Mpu_Time(3),
    CmdId_Mpu_Adc(4),
    CmdId_Mpu_Flash(5),
    CmdId_ABS(-2),
    CmdId_NAK(-1);

    private final byte value;
    CommandId(int value) {
        this.value = (byte) value;
    }
}
