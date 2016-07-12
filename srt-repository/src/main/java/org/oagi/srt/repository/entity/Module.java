package org.oagi.srt.repository.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "module")
public class Module implements Serializable {

    public static final String SEQUENCE_NAME = "MODULE_ID_SEQ";

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
    private int moduleId;

    @Column(length = 100, nullable = false)
    private String module;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "namespace_id", nullable = false)
    private Namespace namespace;

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module1 = (Module) o;

        if (moduleId != module1.moduleId) return false;
        if (module != null ? !module.equals(module1.module) : module1.module != null) return false;
        if (release != null ? !release.equals(module1.release) : module1.release != null) return false;
        return namespace != null ? namespace.equals(module1.namespace) : module1.namespace == null;

    }

    @Override
    public int hashCode() {
        int result = moduleId;
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = 31 * result + (release != null ? release.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Module{" +
                "moduleId=" + moduleId +
                ", module='" + module + '\'' +
                ", release=" + release +
                ", namespace=" + namespace +
                '}';
    }
}