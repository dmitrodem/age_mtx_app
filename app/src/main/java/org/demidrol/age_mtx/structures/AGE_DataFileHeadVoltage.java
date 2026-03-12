package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AGE_DataFileHeadVoltage {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 32;

    private float bat12v;      // Измерение внешнего аккумуляторного питания (+12V)
    private float mpu5v;        // Измерение основного напряжения питания (+5V)
    private float mpu3v3;       // Измерение собственного напряжения питания (+3.3V)
    private float mpu1v8;       // Измерение напряжения питания IODDR (+1.8V)
    private float mpu1v2;       // Измерение напряжения питания VDDCORE (+1.2V)
    private float adc3v3;       // Измерение цифрового питания модуля внешнего ADC (+3.3V)
    private float adcP5v;       // Измерение положительного аналогового питания модуля внешнего ADC (+5V)
    private float adcN5v;       // Измерение отрицательного аналогового питания модуля внешнего ADC (-5V)

    public AGE_DataFileHeadVoltage read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);

        bat12v = buffer.getFloat();
        mpu5v = buffer.getFloat();
        mpu3v3 = buffer.getFloat();
        mpu1v8 = buffer.getFloat();
        mpu1v2 = buffer.getFloat();
        adc3v3 = buffer.getFloat();
        adcP5v = buffer.getFloat();
        adcN5v = buffer.getFloat();

        return this;
    }

    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        ByteBuffer buffer = ByteBuffer
                .wrap(result)
                .order(BYTE_ORDER);

        buffer.putFloat(bat12v);
        buffer.putFloat(mpu5v);
        buffer.putFloat(mpu3v3);
        buffer.putFloat(mpu1v8);
        buffer.putFloat(mpu1v2);
        buffer.putFloat(adc3v3);
        buffer.putFloat(adcP5v);
        buffer.putFloat(adcN5v);

        return result;
    }

    // Getters and setters
    public float getBat12v() {
        return bat12v;
    }

    public AGE_DataFileHeadVoltage setBat12v(float bat12v) {
        this.bat12v = bat12v;
        return this;
    }

    public float getMpu5v() {
        return mpu5v;
    }

    public AGE_DataFileHeadVoltage setMpu5v(float mpu5v) {
        this.mpu5v = mpu5v;
        return this;
    }

    public float getMpu3v3() {
        return mpu3v3;
    }

    public AGE_DataFileHeadVoltage setMpu3v3(float mpu3v3) {
        this.mpu3v3 = mpu3v3;
        return this;
    }

    public float getMpu1v8() {
        return mpu1v8;
    }

    public AGE_DataFileHeadVoltage setMpu1v8(float mpu1v8) {
        this.mpu1v8 = mpu1v8;
        return this;
    }

    public float getMpu1v2() {
        return mpu1v2;
    }

    public AGE_DataFileHeadVoltage setMpu1v2(float mpu1v2) {
        this.mpu1v2 = mpu1v2;
        return this;
    }

    public float getAdc3v3() {
        return adc3v3;
    }

    public AGE_DataFileHeadVoltage setAdc3v3(float adc3v3) {
        this.adc3v3 = adc3v3;
        return this;
    }

    public float getAdcP5v() {
        return adcP5v;
    }

    public AGE_DataFileHeadVoltage setAdcP5v(float adcP5v) {
        this.adcP5v = adcP5v;
        return this;
    }

    public float getAdcN5v() {
        return adcN5v;
    }

    public AGE_DataFileHeadVoltage setAdcN5v(float adcN5v) {
        this.adcN5v = adcN5v;
        return this;
    }

    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_DataFileHeadVoltage {\n")
                .append(t).append("  bat12v=").append(String.format("%.3f", bat12v)).append("V\n")
                .append(t).append("  mpu5v=").append(String.format("%.3f", mpu5v)).append("V\n")
                .append(t).append("  mpu3v3=").append(String.format("%.3f", mpu3v3)).append("V\n")
                .append(t).append("  mpu1v8=").append(String.format("%.3f", mpu1v8)).append("V\n")
                .append(t).append("  mpu1v2=").append(String.format("%.3f", mpu1v2)).append("V\n")
                .append(t).append("  adc3v3=").append(String.format("%.3f", adc3v3)).append("V\n")
                .append(t).append("  adcP5v=").append(String.format("%.3f", adcP5v)).append("V\n")
                .append(t).append("  adcN5v=").append(String.format("%.3f", adcN5v)).append("V\n")
                .append(t).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}