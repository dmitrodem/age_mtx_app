package org.demidrol.age_mtx.constants;

public enum WifiSubcmdNand {
    wSC_Nand_PREPARE_HEADER_DATA_FILE(0),
    wSC_Nand_OPEN_CLOSE_WR_DATA_FILE(1);
    final private byte value;
    WifiSubcmdNand(int value) {
        this.value = (byte) value;
    }
    public byte get() {
        return value;
    }
}
