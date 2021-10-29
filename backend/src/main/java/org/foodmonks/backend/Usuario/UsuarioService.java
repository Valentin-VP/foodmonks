package org.foodmonks.backend.Usuario;

import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder)
    { this.usuarioRepository = usuarioRepository; this.passwordEncoder = passwordEncoder;}


    public void cambiarPassword(String correo, String password) throws UsuarioNoEncontradoException {
       Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No existe el Usuario " + correo);
        }
        usuario.setContrasenia(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    public String generarTokenResetPassword() {
        return UUID.randomUUID().toString();
    }

}
