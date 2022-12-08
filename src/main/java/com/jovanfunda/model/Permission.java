package com.jovanfunda.model;

public enum Permission {
    CREATE("C"), READ("R"), UPDATE("U"), DELETE("D");

    private final String label;

    Permission(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
