package com.jovanfunda.model;

public enum Permission {
    CAN_CREATE_USERS("C"), CAN_READ_USERS("R"), CAN_UPDATE_USERS("U"), CAN_DELETE_USERS("D");

    private final String label;

    Permission(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
