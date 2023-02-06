package com.jovanfunda.model.database;

import com.jovanfunda.model.enums.Status;

import javax.persistence.*;

@Entity
@Table
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_email")
    User createdBy;

    Boolean active;

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
}
