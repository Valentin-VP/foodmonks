package org.foodmonks.backend.EmailService;

public class EmailNoEnviadoException extends Exception {
    public EmailNoEnviadoException(String errorMessage) {
        super(errorMessage);
    }
}
