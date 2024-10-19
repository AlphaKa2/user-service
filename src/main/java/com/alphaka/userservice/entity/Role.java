package com.alphaka.userservice.entity;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role getRole(String role) {
        if (role.contains("USER")) {
            return USER;
        }
        return ADMIN;
    }
}
