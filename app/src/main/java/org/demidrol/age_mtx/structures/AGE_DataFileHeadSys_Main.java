package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AGE_DataFileHeadSys_Main {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 56;
    private byte headType;			    // Тип заголовка в зависимости от типа данных (HEAD_FILE_ID)
    private byte digitalDevType;			// измеритель - тип контроллера: - 200 – ARM Cortex-A5 (ATSAMA5D2)
    private byte analogDevType;		    // измеритель - тип аналоговой части
    private byte serialNum;			    // Сетевой номер устройства
    private byte fileRecordInitiator;	// Инициатор начала записи файла (в соотв. с ADC_RECORD_INIT)
    private short factoryNum;			    // Заводской номер устройства (1-65534)
    private short fileID;				    // ID файла, соответствует его номеру записи в Flash-памяти MPU (0,1,...65534max)
    private byte startupMode;		    // режим синхронизации (запуска) начала AD-преобразования в соотв. с ADC_START_EVENT
    private byte fileType;			    // Тип файла данных (DATA_FILE_TYPE)
    private byte chNumG1;	            // Кол-во используемых аналоговых каналов в группах G1/G2
    private byte chNumG2;	            //
    private byte dimensionDataG1;	    // размерность записанных данных в группе G1 (16p,...,32р)
    private byte dimensionDataG2;	    // размерность записанных данных в группе G2 (16p,...,32р) или (u8)(-1), если группа каналов G2 не используется
    private int gnssTime;    		    // время начала преобразования/записи в формате GNSS при синхронизации по сек. метке GNSS
    private short constF12TuningCode;     // Код подстройки частоты генератора, записанный в системных константах SPI-Flash
    private short framF12TuningCode;      // Код подстройки частоты генератора, записанный в SPI-Fram
    private short headSize;    		    // размер заголовка файла в байтах (кратен размеру стр. Flash-памяти)
    private short postScriptSize;    	    // размер эпилога файла в байтах (кратен размеру стр. Flash-памяти)
    private short nandPageSize;    	    // размер стр. Flash-памяти в байтах
    private short nandHeadPageSize;    	// размер заголовка стр. Flash-памяти в байтах
    private long declaredDataSize;		// Предполагаемый/заявленный объем записываемых данных в байтах
    private long actualFileSize;		    // Фактический объем записанных данных в байтах

    public AGE_DataFileHeadSys_Main read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);
        headType = buffer.get();

        // Skip 3 bytes of reserve
        buffer.position(buffer.position() + 3);

        digitalDevType = buffer.get();
        analogDevType = buffer.get();
        serialNum = buffer.get();
        fileRecordInitiator = buffer.get();
        factoryNum = buffer.getShort();
        fileID = buffer.getShort();
        startupMode = buffer.get();
        fileType = buffer.get();
        chNumG1 = buffer.get();
        chNumG2 = buffer.get();
        dimensionDataG1 = buffer.get();
        dimensionDataG2 = buffer.get();

        // Skip 2 bytes of reserve1
        buffer.position(buffer.position() + 2);

        gnssTime = buffer.getInt();
        constF12TuningCode = buffer.getShort();
        framF12TuningCode = buffer.getShort();
        headSize = buffer.getShort();
        postScriptSize = buffer.getShort();
        nandPageSize = buffer.getShort();
        nandHeadPageSize = buffer.getShort();

        // Skip 4 bytes of reserve2
        buffer.position(buffer.position() + 4);

        declaredDataSize = buffer.getLong();
        actualFileSize = buffer.getLong();
        return this;
    }

    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        ByteBuffer buffer = ByteBuffer
                .wrap(result)
                .order(BYTE_ORDER);
        buffer.put(headType);

        // Add 3 bytes of reserve (padding)
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);

        buffer.put(digitalDevType);
        buffer.put(analogDevType);
        buffer.put(serialNum);
        buffer.put(fileRecordInitiator);
        buffer.putShort(factoryNum);
        buffer.putShort(fileID);
        buffer.put(startupMode);
        buffer.put(fileType);
        buffer.put(chNumG1);
        buffer.put(chNumG2);
        buffer.put(dimensionDataG1);
        buffer.put(dimensionDataG2);

        // Add 2 bytes of reserve1 (padding)
        buffer.put((byte)0);
        buffer.put((byte)0);

        buffer.putInt(gnssTime);
        buffer.putShort(constF12TuningCode);
        buffer.putShort(framF12TuningCode);
        buffer.putShort(headSize);
        buffer.putShort(postScriptSize);
        buffer.putShort(nandPageSize);
        buffer.putShort(nandHeadPageSize);

        // Add 4 bytes of reserve2 (padding)
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);

        buffer.putLong(declaredDataSize);
        buffer.putLong(actualFileSize);
        return result;
    }

    // Getters and setters
    public int getHeadType() {
        return headType & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setHeadType(int headType) {
        this.headType = (byte) headType;
        return this;
    }

    public int getDigitalDevType() {
        return digitalDevType & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setDigitalDevType(int digitalDevType) {
        this.digitalDevType = (byte) digitalDevType;
        return this;
    }

    public int getAnalogDevType() {
        return analogDevType & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setAnalogDevType(int analogDevType) {
        this.analogDevType = (byte) analogDevType;
        return this;
    }

    public int getSerialNum() {
        return serialNum & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setSerialNum(int serialNum) {
        this.serialNum = (byte) serialNum;
        return this;
    }

    public int getFileRecordInitiator() {
        return fileRecordInitiator & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setFileRecordInitiator(int fileRecordInitiator) {
        this.fileRecordInitiator = (byte) fileRecordInitiator;
        return this;
    }

    public int getFactoryNum() {
        return factoryNum & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setFactoryNum(int factoryNum) {
        this.factoryNum = (short) factoryNum;
        return this;
    }

    public int getFileID() {
        return fileID & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setFileID(int fileID) {
        this.fileID = (short) fileID;
        return this;
    }

    public int getStartupMode() {
        return startupMode & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setStartupMode(int startupMode) {
        this.startupMode = (byte) startupMode;
        return this;
    }

    public int getFileType() {
        return fileType & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setFileType(int fileType) {
        this.fileType = (byte) fileType;
        return this;
    }

    public int getChNumG1() {
        return chNumG1 & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setChNumG1(int chNumG1) {
        this.chNumG1 = (byte) chNumG1;
        return this;
    }

    public int getChNumG2() {
        return chNumG2 & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setChNumG2(int chNumG2) {
        this.chNumG2 = (byte) chNumG2;
        return this;
    }

    public int getDimensionDataG1() {
        return dimensionDataG1 & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setDimensionDataG1(int dimensionDataG1) {
        this.dimensionDataG1 = (byte) dimensionDataG1;
        return this;
    }

    public int getDimensionDataG2() {
        return dimensionDataG2 & 0xFF;
    }

    public AGE_DataFileHeadSys_Main setDimensionDataG2(int dimensionDataG2) {
        this.dimensionDataG2 = (byte) dimensionDataG2;
        return this;
    }

    public long getGnssTime() {
        return gnssTime & 0xffffffffL;
    }

    public AGE_DataFileHeadSys_Main setGnssTime(long gnssTime) {
        this.gnssTime = (int)gnssTime;
        return this;
    }

    public int getConstF12TuningCode() {
        return constF12TuningCode;
    }

    public AGE_DataFileHeadSys_Main setConstF12TuningCode(int constF12TuningCode) {
        this.constF12TuningCode = (short) constF12TuningCode;
        return this;
    }

    public int getFramF12TuningCode() {
        return framF12TuningCode;
    }

    public AGE_DataFileHeadSys_Main setFramF12TuningCode(int framF12TuningCode) {
        this.framF12TuningCode = (short) framF12TuningCode;
        return this;
    }

    public int getHeadSize() {
        return headSize & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setHeadSize(int headSize) {
        this.headSize = (short) headSize;
        return this;
    }

    public int getPostScriptSize() {
        return postScriptSize & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setPostScriptSize(int postScriptSize) {
        this.postScriptSize = (short) postScriptSize;
        return this;
    }

    public int getNandPageSize() {
        return nandPageSize & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setNandPageSize(int nandPageSize) {
        this.nandPageSize = (short) nandPageSize;
        return this;
    }

    public int getNandHeadPageSize() {
        return nandHeadPageSize & 0xFFFF;
    }

    public AGE_DataFileHeadSys_Main setNandHeadPageSize(int nandHeadPageSize) {
        this.nandHeadPageSize = (short) nandHeadPageSize;
        return this;
    }

    public long getDeclaredDataSize() {
        return declaredDataSize;
    }

    public AGE_DataFileHeadSys_Main setDeclaredDataSize(long declaredDataSize) {
        this.declaredDataSize = declaredDataSize;
        return this;
    }

    public long getActualFileSize() {
        return actualFileSize;
    }

    public AGE_DataFileHeadSys_Main setActualFileSize(long actualFileSize) {
        this.actualFileSize = actualFileSize;
        return this;
    }

    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_DataFileHeadSys_Main {\n")
                .append(t).append("  headType=").append(headType & 0xFF).append("\n")
                .append(t).append("  digitalDevType=").append(digitalDevType & 0xFF).append("\n")
                .append(t).append("  analogDevType=").append(analogDevType & 0xFF).append("\n")
                .append(t).append("  serialNum=").append(serialNum & 0xFF).append("\n")
                .append(t).append("  fileRecordInitiator=").append(fileRecordInitiator & 0xFF).append("\n")
                .append(t).append("  factoryNum=").append(factoryNum & 0xFFFF).append("\n")
                .append(t).append("  fileID=").append(fileID & 0xFFFF).append("\n")
                .append(t).append("  startupMode=").append(startupMode & 0xFF).append("\n")
                .append(t).append("  fileType=").append(fileType & 0xFF).append("\n")
                .append(t).append("  chNumG1=").append(chNumG1 & 0xFF).append("\n")
                .append(t).append("  chNumG2=").append(chNumG2 & 0xFF).append("\n")
                .append(t).append("  dimensionDataG1=").append(dimensionDataG1 & 0xFF).append("\n")
                .append(t).append("  dimensionDataG2=").append(dimensionDataG2 & 0xFF).append("\n")
                .append(t).append("  gnssTime=").append(gnssTime).append("\n")
                .append(t).append("  constF12TuningCode=").append(constF12TuningCode).append("\n")
                .append(t).append("  framF12TuningCode=").append(framF12TuningCode).append("\n")
                .append(t).append("  headSize=").append(headSize & 0xFFFF).append("\n")
                .append(t).append("  postScriptSize=").append(postScriptSize & 0xFFFF).append("\n")
                .append(t).append("  nandPageSize=").append(nandPageSize & 0xFFFF).append("\n")
                .append(t).append("  nandHeadPageSize=").append(nandHeadPageSize & 0xFFFF).append("\n")
                .append(t).append("  declaredDataSize=").append(Long.toUnsignedString(declaredDataSize)).append("\n")
                .append(t).append("  actualFileSize=").append(Long.toUnsignedString(actualFileSize)).append("\n")
                .append(t).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}
