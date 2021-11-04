package org.foodmonks.backend.Admin;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Base64;
import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")

public class AdminController {

    private final AdminService adminService;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;
    private final UsuarioService usuarioService;
    private final TokenHelper tokenHelp;

    @Autowired
    AdminController(AdminService adminService, ClienteService clienteService, RestauranteService restauranteService, UsuarioService usuarioService, TokenHelper tokenHelp) {
        this.adminService = adminService;
        this.clienteService = clienteService;
        this.restauranteService = restauranteService;
        this.usuarioService = usuarioService;
        this.tokenHelp = tokenHelp;
    }


    @Operation(summary = "Crea un nuevo Administrador",
            description = "Alta de un nuevo Administrador",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "administrador" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })

    @PostMapping(path = "/altaAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody String admin) {
        try{
            JsonObject jsonAdmin = new Gson().fromJson(admin, JsonObject.class);
            adminService.crearAdmin(
                    jsonAdmin.get("email").getAsString(),
                    jsonAdmin.get("nombre").getAsString(),
                    jsonAdmin.get("apellido").getAsString(),
                    new String (Base64.getDecoder().decode(jsonAdmin.get("password").getAsString()))
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    //@GetMapping("/rutaEspecifica")
    public List<Admin> listarAdmin(){
        return adminService.listarAdmin();
    }

    @GetMapping("/buscar")
    public void buscarAdmin(@RequestParam String correo) {
        adminService.buscarAdmin(correo);
    }

    @DeleteMapping
    public void elimiarAdmin(@RequestParam Long id) {
        //adminService.eliminarAdmin(id);
    }

    @PutMapping
    public void modificarAdmin(@RequestBody Admin admin) {
        adminService.modificarAdmin(admin);
    }

    @GetMapping(path = "/listarUsuarios")
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false, name = "correo") String correo, @RequestParam(required = false, name = "tipoUser") String tipoUser,
                                            @RequestParam(required = false, name = "fechaReg") String fechaInicio, @RequestParam(required = false, name = "fechafin") String fechaFin,
                                            @RequestParam(required = false, name = "estado") String estado, @RequestParam(required = false, name = "orden") boolean orden) {
        List<Usuario> listaUsuarios = new ArrayList<>();
        JsonArray jsonArray = new JsonArray();
        try {
            listaUsuarios = usuarioService.listarUsuarios(correo, tipoUser, fechaInicio, fechaFin, estado, orden);

            for (Usuario listaUsuario : listaUsuarios) {
                JsonObject usuario = new JsonObject();
                usuario.addProperty("correo", listaUsuario.getCorreo());
                usuario.addProperty("fechaRegistro", listaUsuario.getFechaRegistro().toString());
                if (listaUsuario instanceof Cliente) {//si es cliente
                    Cliente cliente = clienteService.buscarCliente(listaUsuario.getCorreo());//lo consigo como cliente
                    usuario.addProperty("rol", "CLIENTE");
                    usuario.addProperty("estado", cliente.getEstado().toString());
                    usuario.addProperty("nombre", cliente.getNombre());
                    usuario.addProperty("apellido", cliente.getApellido());
                    usuario.addProperty("calificacion", cliente.getCalificacion().toString());
                    jsonArray.add(usuario);
                } else if(listaUsuario instanceof Restaurante){//si es restaurante
                    Restaurante restaurante = restauranteService.buscarRestaurante(listaUsuario.getCorreo());//lo consigo como restaurante
                    usuario.addProperty("rol", "RESTAURANTE");
                    usuario.addProperty("estado", restaurante.getEstado().toString());
                    usuario.addProperty("RUT", restaurante.getRut().toString());
                    usuario.addProperty("descripcion", restaurante.getDescripcion());
                    usuario.addProperty("nombre", restaurante.getNombreRestaurante());
                    usuario.addProperty("telefono", restaurante.getTelefono());
                    usuario.addProperty("calificacion", restaurante.getCalificacion().toString());
                    jsonArray.add(usuario);
                } else if(listaUsuario instanceof Admin) {
                    Admin admin = adminService.buscarAdmin(listaUsuario.getCorreo());
                    usuario.addProperty("nombre", admin.getNombre());
                    usuario.addProperty("rol", "ADMIN");
                    usuario.addProperty("apellido", admin.getApellido());
                    jsonArray.add(usuario);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping(path = "/cambiarEstado/{correo}")
    public ResponseEntity<?> cambiarEstadoUsuario(@RequestHeader("Authorization") String token, @RequestBody String estado, @PathVariable String correo) {
        JsonObject JsonEstado = new JsonObject();
        JsonEstado = new Gson().fromJson(estado, JsonObject.class);

        String state = JsonEstado.get("estado").getAsString();
        System.out.println("estado: " + state);
        switch (JsonEstado.get("estado").getAsString()) {
            case "BLOQUEAR":
                usuarioService.bloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "ELIMINAR":
                if ( token != null && token.startsWith("Bearer ")) {
                    String newToken = token.substring(7);
                    if (tokenHelp.getUsernameFromToken(newToken).equals(correo)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede eliminar este usuario.");
                    }
                }
                usuarioService.eliminarUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "DESBLOQUEAR":
                usuarioService.desbloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "RECHAZAR":
                restauranteService.modificarEstado(correo, EstadoRestaurante.valueOf(estado));
                return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
