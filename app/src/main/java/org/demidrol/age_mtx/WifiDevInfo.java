package org.demidrol.age_mtx;

import java.io.Serializable;

public class WifiDevInfo implements Serializable {
    public int DigitalDevType;		// измеритель - тип контроллера: 200 – ARM Cortex-A5 (ATSAMA5D2)
    public int	InfoDevSize;		// Актуальный размер структуры AGE_DEV_INFO (в байтах)
    public int	SoftType;			// Тип ПО, выполняемого в измерителе
    public int AnalogDevType;		// измеритель - тип аналоговой части
    public int SoftRevNum_day;		// Релиз ПО - день
    public int SoftRevNum_month;	// Релиз ПО - месяц
    public int SoftRevNum_year;	// Релиз ПО - год (2000 + SoftRevNum_year)
    public int NandFlashType;		// Тип используемой NAND Flash-памяти
    public int FactoryNum;			// Заводской номер устройства
    public boolean FpgaConfig;		    // Флаг проведения конфигурации FPGA
    public boolean ConstSetting;		// Флаг записи в SPI Flash-памяти системных констант
    public int BoardConnectState;	// Состояние подключения платы АЦП
    public int WifiSerialNum;		// Wifi - сетевой номер устройства (1-254)
    public int WifiChannel;		// WiFi - рабочий канал модуля
    public String WifiPassword;	// WiFi - пароль

    static byte[] create_request() {
        byte[] request = new byte[1456];
        request[0] = 0x02;
        request[1] = 0x06;
        return request;
    }

    public WifiDevInfo(byte[] reply) throws Exception {
        if (reply.length < 48) {
            throw new Exception("Invalid reply size");
        }
        int header_cmd = reply[0];
        int header_subcmd = reply[1];
        int header_transport_len = (((int)(reply[3])) << 8) | ((int)reply[2]);
        int header_real_len = (((int)(reply[5])) << 8) | ((int)reply[4]);
        int header_err = (((int)(reply[7])) << 8) | ((int)reply[6]);

        if (!((header_cmd == 0x00) && (header_subcmd == 0x06))) {
            throw new Exception("Invalid reply");
        }
        DigitalDevType = ((int)reply[8]) & 0xff;
        InfoDevSize = ((int) reply[9]) & 0xff;
        SoftType = ((int) reply[10]) & 0xff;
        AnalogDevType = ((int) reply[11]) & 0xff;
        SoftRevNum_day = ((int) reply[12]) & 0xff;
        SoftRevNum_month = ((int) reply[13]) & 0xff;
        SoftRevNum_year = ((int) reply[14]) & 0xff;
        NandFlashType = ((int) reply[15]) & 0xff;
        FactoryNum = ((((int)reply[17]) << 8) | ((int)reply[16])) & 0xffff;
        FpgaConfig = (reply[18] == 1);
        ConstSetting = (reply[19] == 1);
        BoardConnectState = ((int) reply[20]) & 0xff;
        WifiSerialNum = ((int) reply[21]) & 0xff;
        WifiChannel = ((int) reply[22]) & 0xff;

        int len = 0;
        for (int pos = 23; pos < reply.length; pos++, len++) {
            if (reply[pos] == '\0') {
                break;
            }
        }
        WifiPassword = new String(reply, 23, len);
    }

    @Override
    public String toString() {
        return "WifiDevInfo{" +
                "DigitalDevType=" + DigitalDevType +
                ", InfoDevSize=" + InfoDevSize +
                ", SoftType=" + SoftType +
                ", AnalogDevType=" + AnalogDevType +
                ", SoftRevNum_day=" + SoftRevNum_day +
                ", SoftRevNum_month=" + SoftRevNum_month +
                ", SoftRevNum_year=" + SoftRevNum_year +
                ", NandFlashType=" + NandFlashType +
                ", FactoryNum=" + FactoryNum +
                ", FpgaConfig=" + FpgaConfig +
                ", ConstSetting=" + ConstSetting +
                ", BoardConnectState=" + BoardConnectState +
                ", WifiSerialNum=" + WifiSerialNum +
                ", WifiChannel=" + WifiChannel +
                ", WifiPassword='" + WifiPassword + '\'' +
                '}';
    }
}
