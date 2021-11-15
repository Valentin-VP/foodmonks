package org.foodmonks.backend.Admin;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Admin.Exceptions.AdminNoEncontradoException;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Restaurante.RestauranteConverter;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Usuario.Usuario;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminConverter adminConverter;
    private final RestauranteRepository restauranteRepository;
    private final RestauranteConverter restauranteConverter;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailService emailService;

    @Autowired
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                        UsuarioRepository usuarioRepository, AdminConverter adminConverter,
                        RestauranteRepository restauranteRepository,
                        RestauranteConverter restauranteConverter) {
        this.adminRepository = adminRepository; this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository; this.adminConverter = adminConverter;
        this.restauranteRepository = restauranteRepository; this.restauranteConverter = restauranteConverter;
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
        return adminRepository.findByCorreo(correo);
    }

    public void modificarAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public JsonArray listarRestaurantesPorEstado(String estadoRestaurante) {

        List<Restaurante> restauranteAux = restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.valueOf(estadoRestaurante));
        JsonArray resultado = restauranteConverter.arrayJsonRestaurantes(restauranteAux);

        return resultado;
    }

    public JsonObject cambiarEstadoRestaurante(String correoRestaurante, String estadoRestaurante) throws RestauranteNoEncontradoException {

        Restaurante restauranteAux = restauranteRepository.findByCorreo(correoRestaurante);

        if (restauranteAux == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correoRestaurante);
        }
        restauranteAux.setEstado(EstadoRestaurante.valueOf(estadoRestaurante));
        restauranteRepository.save(restauranteAux);
        JsonObject response = new JsonObject();
        if (estadoRestaurante.equals("CERRADO")){
            estadoRestaurante = "APROBADA";
        }
        response.addProperty("resultadoCambioEstado", estadoRestaurante);
        return response;
    }

    public void enviarCorreo(String correoRestaurante, String resultadoCambioEstado, String comentariosCambioEstado) throws EmailNoEnviadoException {

        Usuario usuarioAux = usuarioRepository.findByCorreo(correoRestaurante);

        String nombre = usuarioAux.getNombre() + ", " + usuarioAux.getApellido();

        Context context = new Context();
        context.setVariable("user", nombre);
        String contenido = "Estimado usuario, su solicitud de alta fue " + resultadoCambioEstado;
                if(!comentariosCambioEstado.isEmpty()) {
                    contenido = contenido + " por el siguiente motivo : " + comentariosCambioEstado;
                }
        System.out.println(contenido);
        context.setVariable("contenido",contenido);
        String htmlContent = templateEngine.process("aprobar-rechazar", context);
        try {
            emailService.enviarMail(correoRestaurante, "Resultado solicitud nuevo restaurante", htmlContent, null);
        }catch (EmailNoEnviadoException e) {
            System.out.println(e.getMessage());
            throw new EmailNoEnviadoException(e.getMessage());
        }
    }

    public JsonObject obtenerJsonAdmin (String correo) throws AdminNoEncontradoException {
        Admin admin = adminRepository.findByCorreo(correo);
        if (admin == null) {
            throw new AdminNoEncontradoException("No existe el Admin " + correo);
        }
        return adminConverter.jsonAdmin(admin);

    }

}
