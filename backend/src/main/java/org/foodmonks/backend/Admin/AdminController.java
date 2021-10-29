package org.foodmonks.backend.Admin;

import com.google.gson.Gson;
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
import org.springframework.util.comparator.ComparableComparator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false, name = "correo") String correo, @RequestParam(required = false, name = "tipoUser") String tipoUser,
                                            @RequestParam(required = false, name = "fechaReg") String fechaInicio, @RequestParam(required = false, name = "fechafin") String fechaFin,
                                            @RequestParam(required = false, name = "estado") String estado, @RequestParam(required = false, name = "orden") boolean orden) {
        System.out.println("correo" + correo);
        System.out.println("user" + tipoUser);
        System.out.println("inicio" + fechaInicio);
        System.out.println("fin" + fechaFin);
        System.out.println("estado" + estado);
        System.out.println("orden" + orden);
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        JsonArray jsonArray = new JsonArray();
        try {
            listaUsuarios = usuarioService.listarUsuarios();

            //filtros:
            if(!correo.isEmpty()) {//filtro por correo(cliente, restaurante o admin)
                System.out.println("entra al filtro correo");
                List<Usuario> auxList = new ArrayList<Usuario>();
                for(Usuario user: listaUsuarios) {
                    if(user.getCorreo().equals(correo)) {
                        auxList.add(user);
                    }
                }
                listaUsuarios = auxList;
            }
            if(!fechaInicio.isEmpty()) {//filtro por fecha de registro(cliente, restaurante o admin)
                List<Usuario> auxListInicio = new ArrayList<Usuario>();
                List<Usuario> auxListFin = new ArrayList<Usuario>();
                for(Usuario user: listaUsuarios) {
                    if(user.getFechaRegistro().isAfter(LocalDate.parse(fechaInicio))) {
                        auxListInicio.add(user);
                    }
                }
                listaUsuarios = auxListInicio;
                if(!fechaFin.isEmpty()) {
                    for(Usuario user: listaUsuarios) {
                        if(user.getFechaRegistro().isBefore(LocalDate.parse(fechaFin))) {
                            auxListFin.add(user);
                        }
                    }
                    listaUsuarios = auxListFin;
                }
            }
            if(!tipoUser.isEmpty()) {//filtro por tipo de usuario(cliente o restaurante)
                List<Usuario> auxList = new ArrayList<Usuario>();
                for(Usuario user: listaUsuarios) {
                    if (tipoUser.equals("cliente")) {//filtro por cliente
                        if (user instanceof Cliente) {
                            auxList.add(user);
                        }
                    } else {//filtro por restaurante
                        if (user instanceof Restaurante) {
                            auxList.add(user);
                        }
                    }
                }
                listaUsuarios = auxList;
                if(orden) {//ordenamiento por calificacion(cliente o restaurante)
                    List<Usuario> auxListOrden = new ArrayList<Usuario>();
                    if(tipoUser.equals("cliente")) {
                        for(Usuario user: listaUsuarios) {
                            Cliente cliente = clienteService.buscarCliente(user.getCorreo());
                            auxListOrden.add(cliente);
                            //ordenamiento por calificacion global
                        }
                    } else {
                        for(Usuario user: listaUsuarios) {
                            Restaurante restaurante = restauranteService.buscarRestaurante(user.getCorreo());
                        }
                    }
                    listaUsuarios = auxListOrden;
                }
            }
            if(!estado.isEmpty()) {//filtro por estado(cliente o restaurante)
                List<Usuario> auxList = new ArrayList<Usuario>();
                for (Usuario user : listaUsuarios) {
                    if (estado.equals("BLOQUEADO") || estado.equals("ELIMINADO")) {
                        if (user instanceof Cliente) {
                            Cliente cliente = clienteService.buscarCliente(user.getCorreo());
                            if(cliente.getEstado().equals(EstadoCliente.valueOf(estado))) {
                                auxList.add(user);
                            }
                        } else if(user instanceof Restaurante) {
                            Restaurante restaurante = restauranteService.buscarRestaurante(user.getCorreo());
                            if(restaurante.getEstado().equals(EstadoRestaurante.valueOf(estado))) {
                                auxList.add(user);
                            }
                        }
                    } else if (estado.equals("DESBLOQUEADO")) {
                        if (user instanceof Cliente) {
                            Cliente cliente = clienteService.buscarCliente(user.getCorreo());
                            if(cliente.getEstado().equals(EstadoCliente.valueOf("ACTIVO"))) {
                                auxList.add(user);
                            }
                        } else if(user instanceof Restaurante) {
                            Restaurante restaurante = restauranteService.buscarRestaurante(user.getCorreo());
                            if(restaurante.getEstado().equals(EstadoRestaurante.valueOf("ABIERTO")) || restaurante.getEstado().equals(EstadoRestaurante.valueOf("CERRADO"))) {
                                auxList.add(user);
                            }
                        }
                    }
                }
                listaUsuarios = auxList;
            }

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
                    jsonArray.add(usuario);
                } else if(listaUsuario instanceof Restaurante){//si es restaurante
                    Restaurante restaurante = restauranteService.buscarRestaurante(listaUsuario.getCorreo());//lo consigo como restaurante
                    usuario.addProperty("rol", "RESTAURANTE");
                    usuario.addProperty("estado", restaurante.getEstado().toString());
                    usuario.addProperty("RUT", restaurante.getRut().toString());
                    usuario.addProperty("descripcion", restaurante.getDescripcion());
                    usuario.addProperty("nombre", restaurante.getNombreRestaurante());
                    usuario.addProperty("telefono", restaurante.getTelefono());
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
    public ResponseEntity<?> cambiarEstadoUsuario(@RequestBody String estado, @PathVariable String correo) {
        JsonObject JsonEstado = new JsonObject();
        JsonEstado = new Gson().fromJson(estado, JsonObject.class);

        String state = JsonEstado.get("estado").getAsString();
        System.out.println("estado: " + state);
        switch (JsonEstado.get("estado").getAsString()) {
            case "BLOQUEAR":
                usuarioService.bloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "ELIMINAR":
                //service de eliminar usuario
                return new ResponseEntity<>(HttpStatus.OK);
            case "DESBLOQUEAR":
                usuarioService.desbloquearUsuario(correo);
                return new ResponseEntity<>(HttpStatus.OK);
            case "RECHAZAR":
                restauranteService.modificarEstado(correo, EstadoRestaurante.valueOf(estado));
                return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//estado no corresponde
    }

}
