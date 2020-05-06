package com.leovegas.wallet.api.entities;

public class Response {
    private ResponseStatusType status;
    private String error;
    public Response(ResponseStatusType status) {
        this.status = status;
    }

    public Response(ResponseStatusType status, String error) {
        this.status = status;
        this.error = error;
    }

    public ResponseStatusType getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}