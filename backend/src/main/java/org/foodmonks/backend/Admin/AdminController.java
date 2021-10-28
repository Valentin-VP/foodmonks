package org.foodmonks.backend.Admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteService;
import org.foodmonks.backend.Menu.DtMenu;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;
    private final UsuarioService usuarioService;

    @Autowired
    AdminController(AdminService adminService, ClienteService clienteService, RestauranteService restauranteService, UsuarioService usuarioService) {
        this.adminService = adminService;
        this.clienteService = clienteService;
        this.restauranteService = restauranteService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public void createAdmin(@RequestBody Admin admin) {
        adminService.crearAdmin(admin);
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
    public ResponseEntity<?> listarUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        JsonArray jsonArray = new JsonArray();
        try {
            //listaUsuarios = usuarioService.listarUsuarios();//falta una funcion en UsuarioService que devuelva todos los usuarios
            for (Usuario listaUsuario : listaUsuarios) {
                if(clienteService.buscarCliente(listaUsuario.getCorreo()) != null || restauranteService.buscarRestaurante(listaUsuario.getCorreo()) != null) {
                    JsonObject usuario = new JsonObject();
                    usuario.addProperty("correo", listaUsuario.getCorreo());
                    usuario.addProperty("nombre", listaUsuario.getNombre());
                    usuario.addProperty("apellido", listaUsuario.getApellido());
                    usuario.addProperty("fechaRegistro", listaUsuario.getFechaRegistro().toString());
                    if (clienteService.buscarCliente(listaUsuario.getCorreo()) != null) {
                        usuario.addProperty("rol", "CLIENTE");
                    } else if (restauranteService.buscarRestaurante(listaUsuario.getCorreo()) != null) {
                        usuario.addProperty("rol", "RESTAURANTE");
                    }
                    jsonArray.add(usuario);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping(path = "/cambiarEstado/{id}")
    public ResponseEntity<?> cambiarEstadoUsuario(@RequestBody String estado, @PathVariable String correo) {
        switch (estado) {
            case "BLOQUEADO":
                usuarioService.bloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "ELIMINADO":
                //service de eliminar usuario
                return new ResponseEntity<>(HttpStatus.OK);
            case "DESBLOQUEADO":
                usuarioService.desbloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "RECHAZADO":
                restauranteService.modificarEstado(correo, EstadoRestaurante.valueOf(estado));
                return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//estado no corresponde
    }

}
