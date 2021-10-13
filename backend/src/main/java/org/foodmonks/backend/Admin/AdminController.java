package org.foodmonks.backend.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public void createAdmin(@RequestBody Admin admin) {
        System.out.println("Entro al post");
        adminService.crearAdmin(admin);
    }

    @GetMapping
    //@GetMapping("/rutaEspecifica")
    public List<Admin> listarAdmin(){
        return adminService.listarAdmin();
    }

    @GetMapping("/buscar")
    public void buscarAdmin(@RequestParam String correo) {
        System.out.println(correo);
        adminService.buscarAdmin(correo);
    }

    @DeleteMapping
    public void elimiarAdmin(@RequestParam Long id) {
        System.out.println(id);
        adminService.eliminarAdmin(id);
    }

    @PutMapping
    public void modificarAdmin(@RequestBody Admin admin) {
        if (admin == null) {
            System.out.println("no existe el cliente");
        }else {
            System.out.println(admin.getNombre());
            adminService.modificarAdmin(admin);
        }

    }

}
