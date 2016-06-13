package org.oagi.srt.repository.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cdt_sc_awd_pri")
public class CoreDataTypeSupplementaryComponentAllowedPrimitive implements Serializable {

    @Id
    @GeneratedValue(generator = "CDT_SC_AWD_PRI_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "CDT_SC_AWD_PRI_ID_SEQ", sequenceName = "CDT_SC_AWD_PRI_ID_SEQ", allocationSize = 1)
    private int cdtScAwdPriId;

    @Column(nullable = false)
    private int cdtScId;

    @Column(nullable = false)
    private int cdtPriId;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public int getCdtScAwdPriId() {
        return cdtScAwdPriId;
    }

    public void setCdtScAwdPriId(int cdtScAwdPriId) {
        this.cdtScAwdPriId = cdtScAwdPriId;
    }

    public int getCdtScId() {
        return cdtScId;
    }

    public void setCdtScId(int cdtScId) {
        this.cdtScId = cdtScId;
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
        return "CoreDataTypeSupplementaryComponentAllowedPrimitive{" +
                "cdtScAwdPriId=" + cdtScAwdPriId +
                ", cdtScId=" + cdtScId +
                ", cdtPriId=" + cdtPriId +
                ", isDefault=" + isDefault +
                '}';
    }
}
