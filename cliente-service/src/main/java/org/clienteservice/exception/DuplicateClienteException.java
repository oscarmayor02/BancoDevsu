package org.clienteservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateClienteException extends RuntimeException {
    public DuplicateClienteException(String mensaje) {
        super(mensaje);
    }
}
