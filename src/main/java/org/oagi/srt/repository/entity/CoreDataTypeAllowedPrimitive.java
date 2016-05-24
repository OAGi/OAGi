package org.oagi.srt.repository.entity;

import java.io.Serializable;

public class CoreDataTypeAllowedPrimitive implements Serializable {

    private int cdtAwdPriId;
    private int cdtId;
    private int cdtPriId;
    private boolean isDefault;

    public int getCdtAwdPriId() {
        return cdtAwdPriId;
    }

    public void setCdtAwdPriId(int cdtAwdPriId) {
        this.cdtAwdPriId = cdtAwdPriId;
    }

    public int getCdtId() {
        return cdtId;
    }

    public void setCdtId(int cdtId) {
        this.cdtId = cdtId;
    }

    public int getCdtPriId() {
        return cdtPriId;
    }

    public void setCdtPriId(int cdtPriId) {
        this.cdtPriId = cdtPriId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "CoreDataTypeAllowedPrimitive{" +
                "cdtAwdPriId=" + cdtAwdPriId +
                ", cdtId=" + cdtId +
                ", cdtPriId=" + cdtPriId +
                ", isDefault=" + isDefault +
                '}';
    }
}