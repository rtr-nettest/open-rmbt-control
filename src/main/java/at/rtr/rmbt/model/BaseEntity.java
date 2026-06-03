package at.rtr.rmbt.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Base entity class.
 */
@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    /**
     * Pre insert.
     */
    @PrePersist
    protected void preInsert() {
        this.createdDate = new Date();
        this.modifiedDate = new Date();
    }

    /**
     * Pre update.
     */
    @PreUpdate
    protected void preUpdate() {
        this.modifiedDate = new Date();
    }

}
