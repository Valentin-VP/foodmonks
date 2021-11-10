package org.foodmonks.backend.Usuario.Exceptions;

public class UsuarioNoDesbloqueadoException extends Exception {
    public UsuarioNoDesbloqueadoException(String errorMessage) {
        super(errorMessage);
    }
}
