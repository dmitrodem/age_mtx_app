package org.demidrol.age_mtx.structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AGE_DataFileHeadGeneralLabel {
    private static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static int SIZEOF = 112;

    private float tok;                 // Сила тока (амперы)
    private byte[] com = new byte[48];  // Строка комментария
    private byte[] geo = new byte[20];  // Район проводимых работ
    private byte[] fam = new byte[20];  // Фамилия оператора
    private short ab1;                  // Размер_1 AB (метры)
    private short ab2;                  // Номер AB
    private short ab3;                  // Размер_2 AB (метры)
    private short nst;                  // Номер станции
    private short nom;                  // Номер работы - сквозной номер "записи"
    private byte met;                    // Код метода регистрации: 1-ЗС, 2-ВП, 3-МТЗ, 4-ЧЗ
    private byte ist;                    // Тип установки: 1-скважина, 2-наземный диполь, 3-генераторная петпя
    private byte tpi;                    // Тип привязки начала AD-преобразования (0 - MPU, 1 - GNSS, 2 - EXT...)
    // 7 bytes of reserve follow automatically from buffer position

    public AGE_DataFileHeadGeneralLabel read(ByteBuffer buffer) {
        buffer.order(BYTE_ORDER);

        tok = buffer.getFloat();
        buffer.get(com);
        buffer.get(geo);
        buffer.get(fam);
        ab1 = buffer.getShort();
        ab2 = buffer.getShort();
        ab3 = buffer.getShort();
        nst = buffer.getShort();
        nom = buffer.getShort();
        met = buffer.get();
        ist = buffer.get();
        tpi = buffer.get();

        // Skip 7 bytes of reserve
        buffer.position(buffer.position() + 7);

        return this;
    }

    public byte[] serialize() {
        byte[] result = new byte[SIZEOF];
        ByteBuffer buffer = ByteBuffer
                .wrap(result)
                .order(BYTE_ORDER);

        buffer.putFloat(tok);
        buffer.put(com, 0, 48);
        buffer.put(geo, 0, 20);
        buffer.put(fam, 0, 20);
        buffer.putShort(ab1);
        buffer.putShort(ab2);
        buffer.putShort(ab3);
        buffer.putShort(nst);
        buffer.putShort(nom);
        buffer.put(met);
        buffer.put(ist);
        buffer.put(tpi);

        // Add 7 bytes of reserve (padding)
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);
        buffer.put((byte)0);

        return result;
    }

    // Getters and setters
    public float getTok() {
        return tok;
    }

    public AGE_DataFileHeadGeneralLabel setTok(float tok) {
        this.tok = tok;
        return this;
    }

    public byte[] getCom() {
        return com;
    }

    public AGE_DataFileHeadGeneralLabel setCom(byte[] com) {
        if (com.length != 48) {
            throw new IllegalArgumentException("COM array must be exactly 48 bytes");
        }
        this.com = com.clone();
        return this;
    }

    public AGE_DataFileHeadGeneralLabel setCom(String com) {
        byte[] bytes = com.getBytes(StandardCharsets.UTF_8);
        Arrays.fill(this.com, (byte)0);
        System.arraycopy(bytes, 0, this.com, 0, Math.min(bytes.length, 48));
        return this;
    }

    public String getComAsString() {
        // Find first null byte to terminate string
        int len = 0;
        while (len < com.length && com[len] != 0) {
            len++;
        }
        return new String(com, 0, len, StandardCharsets.UTF_8);
    }

    public byte[] getGeo() {
        return geo;
    }

    public AGE_DataFileHeadGeneralLabel setGeo(byte[] geo) {
        if (geo.length != 20) {
            throw new IllegalArgumentException("GEO array must be exactly 20 bytes");
        }
        this.geo = geo.clone();
        return this;
    }

    public AGE_DataFileHeadGeneralLabel setGeo(String geo) {
        byte[] bytes = geo.getBytes(StandardCharsets.UTF_8);
        Arrays.fill(this.geo, (byte)0);
        System.arraycopy(bytes, 0, this.geo, 0, Math.min(bytes.length, 20));
        return this;
    }

    public String getGeoAsString() {
        int len = 0;
        while (len < geo.length && geo[len] != 0) {
            len++;
        }
        return new String(geo, 0, len, StandardCharsets.UTF_8);
    }

    public byte[] getFam() {
        return fam;
    }

    public AGE_DataFileHeadGeneralLabel setFam(byte[] fam) {
        if (fam.length != 20) {
            throw new IllegalArgumentException("FAM array must be exactly 20 bytes");
        }
        this.fam = fam.clone();
        return this;
    }

    public AGE_DataFileHeadGeneralLabel setFam(String fam) {
        byte[] bytes = fam.getBytes(StandardCharsets.UTF_8);
        Arrays.fill(this.fam, (byte)0);
        System.arraycopy(bytes, 0, this.fam, 0, Math.min(bytes.length, 20));
        return this;
    }

    public String getFamAsString() {
        int len = 0;
        while (len < fam.length && fam[len] != 0) {
            len++;
        }
        return new String(fam, 0, len, StandardCharsets.UTF_8);
    }

    public int getAb1() {
        return ab1 & 0xFFFF;
    }

    public AGE_DataFileHeadGeneralLabel setAb1(int ab1) {
        this.ab1 = (short) ab1;
        return this;
    }

    public int getAb2() {
        return ab2 & 0xFFFF;
    }

    public AGE_DataFileHeadGeneralLabel setAb2(int ab2) {
        this.ab2 = (short) ab2;
        return this;
    }

    public int getAb3() {
        return ab3 & 0xFFFF;
    }

    public AGE_DataFileHeadGeneralLabel setAb3(int ab3) {
        this.ab3 = (short) ab3;
        return this;
    }

    public int getNst() {
        return nst & 0xFFFF;
    }

    public AGE_DataFileHeadGeneralLabel setNst(int nst) {
        this.nst = (short) nst;
        return this;
    }

    public int getNom() {
        return nom & 0xFFFF;
    }

    public AGE_DataFileHeadGeneralLabel setNom(int nom) {
        this.nom = (short) nom;
        return this;
    }

    public int getMet() {
        return met & 0xFF;
    }

    public AGE_DataFileHeadGeneralLabel setMet(int met) {
        this.met = (byte) met;
        return this;
    }

    public int getIst() {
        return ist & 0xFF;
    }

    public AGE_DataFileHeadGeneralLabel setIst(int ist) {
        this.ist = (byte) ist;
        return this;
    }

    public int getTpi() {
        return tpi & 0xFF;
    }

    public AGE_DataFileHeadGeneralLabel setTpi(int tpi) {
        this.tpi = (byte) tpi;
        return this;
    }

    public String toStringWithOffset(int offset) {
        String t = "  ".repeat(offset);
        StringBuilder sb = new StringBuilder()
                .append(t).append("AGE_DataFileHeadGeneralLabel {\n")
                .append(t).append("  tok=").append(tok).append("\n")
                .append(t).append("  com='").append(getComAsString()).append("'\n")
                .append(t).append("  geo='").append(getGeoAsString()).append("'\n")
                .append(t).append("  fam='").append(getFamAsString()).append("'\n")
                .append(t).append("  ab1=").append(ab1 & 0xFFFF).append("\n")
                .append(t).append("  ab2=").append(ab2 & 0xFFFF).append("\n")
                .append(t).append("  ab3=").append(ab3 & 0xFFFF).append("\n")
                .append(t).append("  nst=").append(nst & 0xFFFF).append("\n")
                .append(t).append("  nom=").append(nom & 0xFFFF).append("\n")
                .append(t).append("  met=").append(met & 0xFF).append("\n")
                .append(t).append("  ist=").append(ist & 0xFF).append("\n")
                .append(t).append("  tpi=").append(tpi & 0xFF).append("\n")
                .append(t).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStringWithOffset(0);
    }
}