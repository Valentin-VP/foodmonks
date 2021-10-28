package org.foodmonks.backend.Admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
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
    private final TokenHelper tokenHelp;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;

    @Autowired
    AdminController(AdminService adminService, TokenHelper tokenHelp, UsuarioService usuarioService, ClienteService clienteService, RestauranteService restauranteService) {
        this.adminService = adminService;
        this.tokenHelp = tokenHelp;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.restauranteService = restauranteService;
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
        //String newtoken = "";
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        JsonArray jsonArray = new JsonArray();
        try {
            //if ( token != null && token.startsWith("Bearer ")) {
            //    newtoken = token.substring(7);
            //}
            //String correo = tokenHelp.getUsernameFromToken(newtoken);

            //listaUsuarios = usuarioService.listarUsuarios();//falta una funcion en UsuarioService que devuelva todos los usuarios
            for(int i=0;i<listaUsuarios.size();i++) {
                JsonObject usuario = new JsonObject();
                usuario.addProperty("correo", listaUsuarios.get(i).getCorreo());
                usuario.addProperty("nombre", listaUsuarios.get(i).getNombre());
                usuario.addProperty("apellido", listaUsuarios.get(i).getApellido());
                usuario.addProperty("fechaRegistro", listaUsuarios.get(i).getFechaRegistro().toString());
                if(clienteService.buscarCliente(listaUsuarios.get(i).getCorreo()) != null) {
                    usuario.addProperty("rol", "CLIENTE");
                } else if(restauranteService.buscarRestaurante(listaUsuarios.get(i).getCorreo()) != null) {
                    usuario.addProperty("rol", "RESTAURANTE");
                }
                jsonArray.add(usuario);
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @PutMapping(path = "/cambiarEstado/{id}")
    public ResponseEntity<?> cambiarEstadoUsuario(@RequestBody String estado, @PathVariable String correo) {
        if(clienteService.buscarCliente(correo) != null) {
            Cliente cliente = clienteService.buscarCliente(correo);
            if(cliente.getEstado() != EstadoCliente.valueOf(estado)) {
                cliente.setEstado(EstadoCliente.valueOf(estado));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//estado actual es igual a "estado"
        } else if(restauranteService.buscarRestaurante(correo) != null) {
            Restaurante restaurante = restauranteService.buscarRestaurante(correo);
            if(restaurante.getEstado() != EstadoRestaurante.valueOf(estado)) {
                restaurante.setEstado(EstadoRestaurante.valueOf(estado));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//estado actual es igual a "estado"
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//si no entra a ningun if, entonces no encontro al usuario
    }

}
