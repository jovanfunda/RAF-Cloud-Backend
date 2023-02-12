package com.jovanfunda.model.database;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class ErrorHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Date date;
    Long machineID;
    String failedOperation;
    String message;

    public ErrorHistory() {}

    public ErrorHistory(Date date, Long machineID, String failedOperation, String message) {
        this.date = date;
        this.machineID = machineID;
        this.failedOperation = failedOperation;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getMachineID() {
        return machineID;
    }

    public void setMachineID(Long machineID) {
        this.machineID = machineID;
    }

    public String getFailedOperation() {
        return failedOperation;
    }

    public void setFailedOperation(String failedOperation) {
        this.failedOperation = failedOperation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
