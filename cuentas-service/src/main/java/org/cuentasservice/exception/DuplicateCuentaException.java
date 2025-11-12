package org.cuentasservice.exception;
public class DuplicateCuentaException extends RuntimeException {
    public DuplicateCuentaException(String message) {
        super(message);
    }
}
