package org.foodmonks.backend.Admin;


import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository ) {
        this.adminRepository = adminRepository; this.passwordEncoder = passwordEncoder; this.usuarioRepository = usuarioRepository;
    }

    public void crearAdmin(String correo, String nombre, String apellido, String password) throws UsuarioExisteException {
        if (usuarioRepository.findByCorreo(correo) != null){
            throw new UsuarioExisteException("Ya existe un Usuario con el correo " + correo);
        }
        Admin admin = new Admin(correo, nombre, apellido, passwordEncoder.encode(password),LocalDate.now());
        adminRepository.save(admin);
    }

    public List<Admin> listarAdmin(){
        return adminRepository.findAll();
    }

    public Admin buscarAdmin(String correo) {
        Admin aux = adminRepository.findByCorreo(correo);
        return aux;
    }

    public void modificarAdmin(Admin admin) {
        adminRepository.save(admin);
    }

}
