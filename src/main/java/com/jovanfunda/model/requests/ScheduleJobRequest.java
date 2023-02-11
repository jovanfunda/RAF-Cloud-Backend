package com.jovanfunda.model.requests;

public class ScheduleJobRequest {
    Long machineID;
    String job;
    String scheduleTime;

    public Long getMachineID() {
        return machineID;
    }

    public void setMachineID(Long machineID) {
        this.machineID = machineID;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}
