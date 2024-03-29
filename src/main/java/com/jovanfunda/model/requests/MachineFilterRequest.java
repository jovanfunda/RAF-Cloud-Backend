package com.jovanfunda.model.requests;

import com.jovanfunda.model.enums.Status;

import java.util.Date;
import java.util.List;

public class MachineFilterRequest {

    String name;
    List<Status> status;
    Date dateFrom;
    Date dateTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> statuses) {
        this.status = statuses;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
}
