package at.rtr.rmbt.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    @PrePersist
    protected void preInsert() {
        this.createdDate = new Date();
        this.modifiedDate = new Date();
    }

    @PreUpdate
    protected void preUpdate() {
        this.modifiedDate = new Date();
    }

}
