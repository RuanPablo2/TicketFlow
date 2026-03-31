package com.RuanPablo2.TicketFlow.exceptions;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("TF-1000"),
    VALIDATION_ERROR("TF-1001"),
    BUSINESS_RULE_VIOLATION("TF-1002"),
    UNAUTHORIZED_ACCESS("TF-1003");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}