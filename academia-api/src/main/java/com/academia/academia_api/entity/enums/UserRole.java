package com.academia.academia_api.entity.enums;

public enum UserRole {

    SUPER_ADMIN("superAdmin"),
    ADMIN("admin"),
    PERSONAL("personal"),
    ALUNO("aluno");

    private String role;
    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
