package org.foodmonks.backend.authentication;

import org.foodmonks.backend.Admin.AdminRepository;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        //aqui va el try-catch, necesito funciones en los repos correspondientes que busquen a un Usuario por su correo

        Usuario usuario=repo.findByUserName(correo);

        if(null==usuario) {
            throw new UsernameNotFoundException("User Not Found with userName "+correo);
        }
        return usuario;
    }
}
