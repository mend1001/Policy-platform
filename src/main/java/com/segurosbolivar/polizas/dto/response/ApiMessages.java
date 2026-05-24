package com.segurosbolivar.polizas.dto.response;

public final class ApiMessages {

    private ApiMessages() {}

    public static final String POLICY_FOUND     = "Policy retrieved successfully";
    public static final String POLICIES_LISTED  = "Policies listed successfully";
    public static final String POLICY_RENEWED   = "Policy renewed successfully";
    public static final String POLICY_CANCELLED = "Policy cancelled successfully";
    public static final String RISK_ADDED       = "Risk added successfully";
    public static final String RISK_CANCELLED   = "Risk cancelled successfully";
    public static final String RISKS_LISTED     = "Risks listed successfully";
    public static final String CORE_NOTIFIED    = "Event registered in CORE";
    public static final String POLICY_NOT_FOUND = "Policy not found";
    public static final String RISK_NOT_FOUND   = "Risk not found";
    public static final String POLICY_CANCELLED_ERROR = "Cannot renew a cancelled policy";
    public static final String INVALID_TYPE     = "Only collective policies can have risks";
    public static final String INVALID_API_KEY  = "Invalid or missing API Key";
}
