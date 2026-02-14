package org.demidrol.age_mtx.constants;

public enum WifiSubcmdSys {
    wSC_Sys_GET_WIFI_CONFIG(0),
    wSC_Sys_SWAP_TEST(1),
    wSC_Sys_READ_MEM(2),
    wSC_Sys_WRITE_MEM(3),
    wSC_Sys_GET_INTADC_DATA(4),
    wSC_Sys_GET_STATE(5),
    wSC_Sys_GET_INFO(6),
    wSubCmd_MPU_MSG_ERR(-3),
    wSubCmd_MPU_ERR(-2),
    wSubCmd_PROTOCOL_ERR(-1);

    final private byte value;
    WifiSubcmdSys(int value) {
        this.value = (byte) value;
    }
    public byte get() {
        return value;
    }
}
