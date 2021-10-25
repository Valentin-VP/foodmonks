package org.foodmonks.backend.Usuario;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    public void cambiarPassword(String correo, String password){

    }

    public String generarTokenResetPassword() {
        return UUID.randomUUID().toString();
    }
}
