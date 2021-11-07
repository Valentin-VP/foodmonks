package org.foodmonks.backend.Admin;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
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
        Admin admin = new Admin(nombre, apellido, correo, passwordEncoder.encode(password),LocalDate.now());
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

    public JsonArray listaRestaurantesPorEstado(String estadoRestaurante) {
        // se comunica con RestauranteService para obtener los datos
        return new JsonArray();
    }

    public JsonObject cambiarEstadoRestaurante(String correoRestaurante, String estadoRestaurante) {
        JsonObject respuesta = new JsonObject();
        respuesta.addProperty("resultadoCambioEstado", "Cambio exitoso");
        return respuesta;
    }

    public void enviarCorreo(String correoRestaurante, String resultadoCambioEstado, String comentariosCambioEstado) throws EmailNoEnviadoException {
        // respecto a la falla de enviar el correo, el mensaje de la excepción,
        // podría incluir mencionar que el cambio fue realizado con éxito, pero falló el envío del correo
    }


}
