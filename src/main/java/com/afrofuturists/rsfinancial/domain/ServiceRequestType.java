package com.afrofuturists.rsfinancial.domain;

/**
 * The "other tasks" list from the problem statement, plus OTHER as an
 * escape hatch for anything not yet worth its own named type. Each
 * value implies a different expected shape for ServiceRequest.details,
 * which the frontend (not this enum) is responsible for knowing.
 */
public enum ServiceRequestType {
    CHANGE_OF_ADDRESS,
    CHANGE_OF_BANK_DETAILS,
    POLICY_DOCUMENT_REQUEST,
    BROKER_LETTER_REQUEST,
    IRP5_REQUEST,
    CONSULTATION_REQUEST,
    CLIENT_INFO_COLLECTION,
    BALANCE_SHEET_REQUEST,
    INCOME_STATEMENT_REQUEST,
    OTHER
}
