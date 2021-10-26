package org.foodmonks.backend.Admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void crearAdmin(String correo, String nombre, String apellido, String password) {
        Admin admin = new Admin(correo, nombre, apellido, password, LocalDate.now());
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
