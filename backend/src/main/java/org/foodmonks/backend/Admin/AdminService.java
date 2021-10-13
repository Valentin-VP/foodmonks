package org.foodmonks.backend.Admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void crearAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public List<Admin> listarAdmin(){
        return adminRepository.findAll();
    }

    public Admin buscarAdmin(String correo) {
        Admin aux = adminRepository.findByCorreo(correo);
        if (aux == null) {
            return null;
        }else{
            return aux;
        }
    }

    public void modificarAdmin(Admin admin) {
        adminRepository.save(admin);
    }

}
