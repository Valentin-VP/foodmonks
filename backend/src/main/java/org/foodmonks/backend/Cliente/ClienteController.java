package org.foodmonks.backend.Cliente;


import com.google.gson.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.Direccion.Direccion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import lombok.SneakyThrows;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
@Slf4j
public class ClienteController {

    private final TokenHelper tokenHelp;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;

    @Autowired
    ClienteController(ClienteService clienteService, TokenHelper tokenHelp, RestauranteService restauranteService) {
        this.clienteService = clienteService;
        this.tokenHelp = tokenHelp;
        this.restauranteService = restauranteService;
    }

    @Operation(summary = "Crea un nuevo Cliente",
            description = "Alta de un nuevo Cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro exitoso"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PostMapping(path = "/altaCliente")//CREAR CLIENTE
    public ResponseEntity<?> crearCliente(
            @Parameter(description = "Datos del nuevo Cliente", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class)))
            @RequestBody String cliente) {
        try {
            JsonObject jsonCliente = new Gson().fromJson(cliente, JsonObject.class);
            JsonObject jsonDireccion = jsonCliente.get("direccion").getAsJsonObject();

            clienteService.crearCliente(
                    jsonCliente.get("nombre").getAsString(),
                    jsonCliente.get("apellido").getAsString(),
                    jsonCliente.get("correo").getAsString(),
                    new String (Base64.getDecoder().decode(jsonCliente.get("password").getAsString())),
                    LocalDate.now(),
                    5.0f,
                    jsonDireccion,
                    EstadoCliente.valueOf("ACTIVO")
                    // pedidos se crea el array vacio en el back
                    // y mobileToken es null hasta que instale la aplicacion
            );

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping//LISTAR CLIENTE
    //@GetMapping("/rutaEspecifica")
    public List<Cliente> listarCliente(){
        return clienteService.listarCliente();
    }

    @GetMapping("/buscar")
    public void buscarCliente(@RequestParam String correo) {
        clienteService.buscarCliente(correo);
    }

    @Operation(summary = "Elimina cuenta propia de Cliente",
            description = "Baja logica de Cliente, se cierra sesion al finalizar",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa. Se ha dado de baja."),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    @DeleteMapping(path = "eliminarCuenta")//ELIMINAR CLIENTE
    public ResponseEntity<?> eliminarCuentaPropiaCliente(
            @RequestHeader("Authorization") String token) {
        try {
            String newToken = null;
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);
            clienteService.modificarEstadoCliente(correo, EstadoCliente.ELIMINADO);
            log.debug("Cliente eliminado, enviando a cerrar sesion");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Modificar informacion del cliente",
            description = "Se modifica nombre y apellido del cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Informacion modificada"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PutMapping(path = "/modificarCliente")
    public ResponseEntity<?> modificarCliente(@RequestHeader("Authorization") String token,
                                              @RequestParam(name = "nombre") String nombre,
                                              @RequestParam(name = "apellido") String apellido) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            clienteService.modificarCliente(correo, nombre, apellido);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "Agregar una Direccion",
            description = "Agrega una nueva Direccion al Cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Direccion agregada"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @SneakyThrows
    @PostMapping(path = "/agregarDireccion")
    public ResponseEntity<?> agregarDireccion(@RequestHeader("Authorization") String token,
                                              @Parameter(description = "Datos del nuevo Cliente", required = true)
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                      content = @Content(mediaType = "application/json",
                                                              schema = @Schema(implementation = Direccion.class)))
                                              @RequestBody String direccion) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            JsonObject jsonDireccion = new Gson().fromJson(direccion, JsonObject.class);

            JsonObject id = clienteService.agregarDireccionCliente(correo, jsonDireccion);

            return new ResponseEntity<>(id,HttpStatus.CREATED);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Elimina direccion de Cliente",
            description = "Se elimina una de las direcciones del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa. Se agrega la direccion"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @DeleteMapping(path = "/eliminarDireccion")
    public ResponseEntity<?> eliminarDireccion(@RequestHeader("Authorization") String token,
                                               @RequestParam(name = "id") String id) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            clienteService.eliminarDireccionCliente(correo, Long.valueOf(id));

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Modificar una Direccion",
            description = "Se modifica una direccion del Cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direccion modificada"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PutMapping(path = "/modificarDireccion")
    public ResponseEntity<?> modificarDireccion(@RequestHeader("Authorization") String token,
                                                @RequestParam(name = "id") String id,
                                                @Parameter(description = "Datos del nuevo Cliente", required = true)
                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                            content = @Content(mediaType = "application/json",
                                                                    schema = @Schema(implementation = Direccion.class)))
                                                @RequestBody String direccion) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            JsonObject jsonDireccion = new Gson().fromJson(direccion, JsonObject.class);

            clienteService.modificarDireccionCliente(correo, Long.valueOf(id), jsonDireccion);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @Operation(summary = "Listar los Restaurantes abiertos",
            description = "Lista de los restaurantes que actualmente estan abiertos al publico",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "restaurante" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarAbiertos")
    public ResponseEntity<?> listarRestaurantesAbiertos(@RequestHeader("Authorization") String token, @RequestParam(required = false, name = "nombre") String nombre,
                                                        @RequestParam(required = false, name = "categoria") String categoria, @RequestParam(required = false, name = "orden") boolean orden) {
        //voy a querer el token para la ubicacion del cliente(mostrar restaurantes cercanos a dicha ubicacion)
        JsonArray jsonArray = new JsonArray();
        try {
            List<JsonObject> restaurantesAbiertos = restauranteService.listaRestaurantesAbiertos(nombre, categoria, orden);

            for (JsonObject restaurante : restaurantesAbiertos) {
                jsonArray.add(restaurante);
            }
        } catch(JsonIOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Listar Pedidos Realizados",
            description = "Lista de los pedidos realizados del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedidos" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarPedidosRealizados")
    public ResponseEntity<?> listarPedidosRealizados(@RequestHeader("Authorization") String token,
                                                     @RequestParam(required = false, name = "estadoPedido") String estadoPedido,
                                                     @RequestParam(required = false, name = "nombreMenu") String nombreMenu,
                                                    @RequestParam(required = false, name = "nombreRestaurante") String nombreRestaurante,
                                                    @RequestParam(required = false, name = "medioPago") String medioPago,
                                                    @RequestParam(required = false, name = "orden") String orden,
                                                    @RequestParam(required = false, name = "fecha") String fecha,
                                                    @RequestParam(required = false, name = "total") String total,
                                                    @RequestParam(defaultValue = "0",required = false, name = "page") String page,
                                                    @RequestParam(defaultValue = "1000", required = false, name = "size") String size) {
        String newtoken = "";
        String correo = "";
        List<JsonObject> listaPedidos = new ArrayList<JsonObject>();
        JsonObject jsonObject = new JsonObject();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            correo = tokenHelp.getUsernameFromToken(newtoken);
            jsonObject = clienteService.listarPedidosRealizados(correo, estadoPedido, nombreMenu, nombreRestaurante, medioPago, orden, fecha, total, page, size);
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (ClienteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

    @Operation(summary = "Listar los Menús y Promociones ofrecidos por un Restaurante",
            description = "Lista de los Menús y Promociones que ofrece un Restaurante, aplicando búsqueda opcional por filtros",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedido", "cliente", "menú", "promoción" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Menu.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarProductosRestaurante")
    public ResponseEntity<?> listarProductosRestaurante(
            @RequestParam(name = "id") String restauranteCorreo,
            @RequestParam(required = false, name = "categoria") String categoria,
            @RequestParam(required = false, name = "precioInicial") Float precioInicial,
            @RequestParam(required = false, name = "precioFinal") Float precioFinal
            ) {

        JsonArray jsonArray = new JsonArray();
        try {
            //jsonArray = clienteService.listarMenus(restauranteCorreo, categoria, precioInicial, precioFinal);
            List<JsonObject> listarProductosRestaurante = clienteService.listarMenus(restauranteCorreo, categoria, precioInicial, precioFinal);

            for (JsonObject restaurante : listarProductosRestaurante) {
                jsonArray.add(restaurante);
            }

        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }
  
  
    @Operation(summary = "Realizar un nuevo Pedido a un Restaurante",
            description = "Realizar un nuevo Pedido a un Restaurante",
            tags = { "cliente", "pedido" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PostMapping(path = "/realizarPedido")
    public ResponseEntity<?> realizarPedido(
            @RequestHeader("Authorization") String token,
            @RequestBody String pedido){
        try{
            // Obtener correo del cliente
            String strToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                strToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(strToken);

            // Obtener detalles del pedido
            JsonObject jsonRequestPedido = new Gson().fromJson(pedido, JsonObject.class);
            JsonObject jsonResponsePedido = clienteService.crearPedido(correo, jsonRequestPedido);
            return new ResponseEntity<>(jsonResponsePedido, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
