package org.foodmonks.backend.Usuario;

import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository;}


    public void cambiarPassword(String correo, String password) throws UsuarioNoEncontradoException {
       Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No existe el Usuario " + correo);
        }
        usuario.setContrasenia(password);
        usuarioRepository.save(usuario);
    }

    public String generarTokenResetPassword() {
        return UUID.randomUUID().toString();
    }
}
