package com.irctc.irctc_backend.entity;

import java.util.Arrays;

public enum ClassType {
    SL("SL"),
    _3A("3A"),
    _2A("2A"),
    _1A("1A"),
    CC("CC");

    private final String label;

    ClassType(String label) {
        this.label = label;
    }

    public static ClassType from(String value) {
        return Arrays.stream(values())
                .filter(c -> c.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }
}

