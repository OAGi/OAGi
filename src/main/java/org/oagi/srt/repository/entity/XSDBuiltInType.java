package org.oagi.srt.repository.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "xbt")
public class XSDBuiltInType implements Serializable {

    public static final String SEQUENCE_NAME = "XBT_ID_SEQ";

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
    private int xbtId;

    @Column
    private String name;

    @Column(name = "builtin_type")
    private String builtInType;

    @Column
    private Integer subtypeOfXbtId;

    public int getXbtId() {
        return xbtId;
    }

    public void setXbtId(int xbtId) {
        this.xbtId = xbtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuiltInType() {
        return builtInType;
    }

    public void setBuiltInType(String builtInType) {
        this.builtInType = builtInType;
    }

    public int getSubtypeOfXbtId() {
        return (subtypeOfXbtId == null) ? 0 : subtypeOfXbtId;
    }

    public void setSubtypeOfXbtId(int subtypeOfXbtId) {
        this.subtypeOfXbtId = subtypeOfXbtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XSDBuiltInType that = (XSDBuiltInType) o;

        if (xbtId != that.xbtId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (builtInType != null ? !builtInType.equals(that.builtInType) : that.builtInType != null) return false;
        return subtypeOfXbtId != null ? subtypeOfXbtId.equals(that.subtypeOfXbtId) : that.subtypeOfXbtId == null;

    }

    @Override
    public int hashCode() {
        int result = xbtId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (builtInType != null ? builtInType.hashCode() : 0);
        result = 31 * result + (subtypeOfXbtId != null ? subtypeOfXbtId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "XSDBuiltInType{" +
                "xbtId=" + xbtId +
                ", name='" + name + '\'' +
                ", builtInType='" + builtInType + '\'' +
                ", subtypeOfXbtId=" + subtypeOfXbtId +
                '}';
    }
}
