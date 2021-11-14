package org.foodmonks.backend.Usuario.Exceptions;

public class UsuarioNoBloqueadoException extends Exception {
    public UsuarioNoBloqueadoException(String errorMessage) {
        super(errorMessage);
    }

    public static class UsuarioNoEncontradoException extends Exception {
        public UsuarioNoEncontradoException(String errorMessage) {
            super(errorMessage);
        }
    }
}
