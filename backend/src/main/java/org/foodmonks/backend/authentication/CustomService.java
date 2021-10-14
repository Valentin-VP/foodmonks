package org.foodmonks.backend.authentication;

import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Admin.AdminRepository;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
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

        try {

            Admin admin = adminRepository.findByCorreo(correo);
            if(null==admin) {
                throw new UsernameNotFoundException("User Not Found with userName " + correo);
            }
            return admin;

        } catch(Exception a) {

            try {
                Restaurante restaurante = restauranteRepository.findByCorreo(correo);
                if(null==restaurante) {
                    throw new UsernameNotFoundException("User Not Found with userName " + correo);
                }
                return restaurante;

            } catch(Exception r) {

                Cliente cliente = clienteRepository.findByCorreo(correo);
                if(null==cliente) {
                    throw new UsernameNotFoundException("User Not Found with userName " + correo);
                }
                return cliente;
            }
        }
    }

}
