package org.foodmonks.backend.Usuario;

public class UsuarioNoDesbloqueadoException extends Exception {
    public UsuarioNoDesbloqueadoException(String errorMessage) {
        super(errorMessage);
    }
}
