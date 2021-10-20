package org.foodmonks.backend.Usuario;

public class UsuarioNoEncontradoException extends Exception {
    public UsuarioNoEncontradoException(String errorMessage) {
        super(errorMessage);
    }
}
