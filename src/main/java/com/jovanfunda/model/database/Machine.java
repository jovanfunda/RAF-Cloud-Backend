package com.jovanfunda.model.database;

import com.jovanfunda.model.enums.Status;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne
    @JoinColumn(name = "user_email")
    User createdBy;

    Boolean active;

    @Temporal(TemporalType.DATE)
    Date dateCreated;

    @Version
    Integer version = 0;

    Boolean busy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setBusy(Boolean value) {
        this.busy =value;
    }

    public boolean getBusy() {
        return busy;
    }

}
