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

    public Admin buscarAdmin(Long id) {
        Optional<Admin> aux = adminRepository.findById(id);
        if (aux.isEmpty()) {
            System.out.println("No existe ese administrador");
            return null;
        }else{
            System.out.println(aux.get().getNombre());
            return aux.get();
        }
    }

    public void eliminarAdmin(Long id) {
        Optional<Admin> aux = adminRepository.findById(id);
        if (aux.isEmpty())
            System.out.println("No existe ese administrador");
        else {
            System.out.println(aux.get().getNombre());
            adminRepository.delete(aux.get());
        }
    }

    public void modificarAdmin(Admin admin) {
        adminRepository.save(admin);
    }

}
