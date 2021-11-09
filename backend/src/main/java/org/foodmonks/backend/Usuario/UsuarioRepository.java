package org.foodmonks.backend.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository  extends JpaRepository<Usuario, String> {

    Usuario findByCorreo(String correo);

}
