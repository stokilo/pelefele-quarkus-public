package com.sstec.qpelefele.model.exceptions;

public class BusinessException extends RuntimeException {
    public final String details;

    public BusinessException(String details) {
        this.details = details;
    }
}
