package org.oagi.srt.repository.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bdt_pri_restri")
public class BusinessDataTypePrimitiveRestriction implements Serializable {

    public static final String SEQUENCE_NAME = "BDT_PRI_RESTRI_ID_SEQ";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME, strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
            name = SEQUENCE_NAME,
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = SEQUENCE_NAME),
                    @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            }
    )
    private int bdtPriRestriId;

    @Column(nullable = false)
    private int bdtId;

    @Column
    private Integer cdtAwdPriXpsTypeMapId;

    @Column
    private Integer codeListId;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column
    private Integer agencyIdListId;

    public BusinessDataTypePrimitiveRestriction() {}

    public BusinessDataTypePrimitiveRestriction(int bdtPriRestriId) {
        this.bdtPriRestriId = bdtPriRestriId;
    }

    public int getBdtPriRestriId() {
        return bdtPriRestriId;
    }

    public void setBdtPriRestriId(int bdtPriRestriId) {
        this.bdtPriRestriId = bdtPriRestriId;
    }

    public int getBdtId() {
        return bdtId;
    }

    public void setBdtId(int bdtId) {
        this.bdtId = bdtId;
    }

    public int getCdtAwdPriXpsTypeMapId() {
        return (cdtAwdPriXpsTypeMapId == null) ? 0 : cdtAwdPriXpsTypeMapId;
    }

    public void setCdtAwdPriXpsTypeMapId(int cdtAwdPriXpsTypeMapId) {
        if (cdtAwdPriXpsTypeMapId > 0) {
            this.cdtAwdPriXpsTypeMapId = cdtAwdPriXpsTypeMapId;
        }
    }

    public int getCodeListId() {
        return (codeListId == null) ? 0 : codeListId;
    }

    public void setCodeListId(int codeListId) {
        if (codeListId > 0) {
            this.codeListId = codeListId;
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getAgencyIdListId() {
        return (agencyIdListId == null) ? 0 : agencyIdListId;
    }

    public void setAgencyIdListId(int agencyIdListId) {
        if (agencyIdListId > 0) {
            this.agencyIdListId = agencyIdListId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessDataTypePrimitiveRestriction that = (BusinessDataTypePrimitiveRestriction) o;

        if (bdtPriRestriId != that.bdtPriRestriId) return false;
        if (bdtId != that.bdtId) return false;
        if (isDefault != that.isDefault) return false;
        if (cdtAwdPriXpsTypeMapId != null ? !cdtAwdPriXpsTypeMapId.equals(that.cdtAwdPriXpsTypeMapId) : that.cdtAwdPriXpsTypeMapId != null)
            return false;
        if (codeListId != null ? !codeListId.equals(that.codeListId) : that.codeListId != null) return false;
        return agencyIdListId != null ? agencyIdListId.equals(that.agencyIdListId) : that.agencyIdListId == null;

    }

    @Override
    public int hashCode() {
        int result = bdtPriRestriId;
        result = 31 * result + bdtId;
        result = 31 * result + (cdtAwdPriXpsTypeMapId != null ? cdtAwdPriXpsTypeMapId.hashCode() : 0);
        result = 31 * result + (codeListId != null ? codeListId.hashCode() : 0);
        result = 31 * result + (isDefault ? 1 : 0);
        result = 31 * result + (agencyIdListId != null ? agencyIdListId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BusinessDataTypePrimitiveRestriction{" +
                "bdtPriRestriId=" + bdtPriRestriId +
                ", bdtId=" + bdtId +
                ", cdtAwdPriXpsTypeMapId=" + cdtAwdPriXpsTypeMapId +
                ", codeListId=" + codeListId +
                ", isDefault=" + isDefault +
                ", agencyIdListId=" + agencyIdListId +
                '}';
    }
}