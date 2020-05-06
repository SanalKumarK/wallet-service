package com.leovegas.wallet.api.entities;

public enum ApiError {
    UNKNOWN_ACCOUNT("No Player associated with the given account number."),
    NOT_ENOUGH_BALANCE("Amount to be debited is greater than the available balance."),
    UNKNOWN_REASON("Unknown Reason"),
    PLAYER_NOT_FOUND("Player with given id not found."),
    INVALID_PLAYER("Please provide a valid player id."),
    INVALID_TRANSACTION("Transaction with given id not found"),
    DUPLICATE_PLAYER_ID("Player with same userId already exists."),
    DUPLICATE_ACCOUNT_NUM("Player with given account number already exists."),
    DUPLICATE_TRANSACTION_ID("Duplicate transaction with same ID exists."),
    INVALID_ACCOUNT("Please provide a valid account information.");

    private final String msg;
    ApiError(String s) {
        this.msg = s;
    }
    public String message() {
        return msg;
    }
}
