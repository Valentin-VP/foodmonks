package org.foodmonks.backend.Cliente;


import com.google.gson.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import lombok.SneakyThrows;
import org.foodmonks.backend.Cliente.Exceptions.ClientePedidoNoCoincideException;
import org.foodmonks.backend.Direccion.Direccion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoIdException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoSinRestauranteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoComentarioException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoRazonException;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
@Tag(name = "cliente", description = "API de Cliente")
@Slf4j
public class ClienteController {

    private final ClienteHelper clienteHelp;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;

    @Autowired
    ClienteController(ClienteService clienteService, ClienteHelper clienteHelp, RestauranteService restauranteService) {
        this.clienteService = clienteService;
        this.clienteHelp = clienteHelp;
        this.restauranteService = restauranteService;
    }

    @Operation(summary = "Crea un nuevo Cliente",
            description = "Alta de un nuevo Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
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
                    new String (Base64.getDecoder().decode(jsonCliente.get("correo").getAsString())),
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

    @Operation(summary = "Elimina cuenta propia de Cliente",
            description = "Baja logica de Cliente, se cierra sesion al finalizar",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa. Se ha dado de baja"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @DeleteMapping(path = "eliminarCuenta")//ELIMINAR CLIENTE
    public ResponseEntity<?> eliminarCuentaPropiaCliente(
            @RequestHeader("Authorization") String token) {
        try {
            String correo = clienteHelp.obtenerCorreoDelToken(token);
            clienteService.modificarEstadoCliente(correo, EstadoCliente.ELIMINADO);
            log.debug("Cliente eliminado, enviando a cerrar sesion");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Modificar informacion del cliente",
            description = "Se modifica nombre y apellido del cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
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
            String correo = clienteHelp.obtenerCorreoDelToken(token);

            clienteService.modificarCliente(correo, nombre, apellido);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "Agregar una Direccion",
            description = "Agrega una nueva Direccion al Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
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
            String correo = clienteHelp.obtenerCorreoDelToken(token);

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
            String correo = clienteHelp.obtenerCorreoDelToken(token);

            clienteService.eliminarDireccionCliente(correo, Long.valueOf(id));

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Modificar una Direccion",
            description = "Se modifica una direccion del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
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
            String correo = clienteHelp.obtenerCorreoDelToken(token);

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
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarAbiertos")
    public ResponseEntity<?> listarRestaurantesAbiertos(@RequestHeader("Authorization") String token, @RequestParam(required = false, name = "nombre") String nombre,
                                                        @RequestParam(required = false, name = "categoria") String categoria, @RequestParam(required = false, name = "orden") boolean orden,
                                                        @RequestParam(name = "direccion") String direccion) {
        JsonArray jsonArray = new JsonArray();
        try {
            List<JsonObject> restaurantesAbiertos = restauranteService.listaRestaurantesAbiertos(nombre.toLowerCase(), categoria, orden, direccion);

            for (JsonObject restaurante : restaurantesAbiertos) {
                jsonArray.add(restaurante);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Listar Pedidos Realizados",
            description = "Lista de los pedidos realizados del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
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
        JsonObject jsonObject = new JsonObject();
        try {
            String correo = clienteHelp.obtenerCorreoDelToken(token);
            jsonObject = clienteService.listarPedidosRealizados(correo, estadoPedido, nombreMenu.toLowerCase(), nombreRestaurante.toLowerCase(), medioPago, orden, fecha, total, page, size);
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
            tags = { "cliente" })
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
            List<JsonObject> listarProductosRestaurante = clienteService.listarMenus(new String(Base64.getDecoder().decode(restauranteCorreo)), categoria, precioInicial, precioFinal);

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
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PostMapping(path = "/realizarPedido")
    public ResponseEntity<?> realizarPedido(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Datos del nuevo Pedido", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pedido.class)))
            @RequestBody String pedido){
        try{
            String correo = clienteHelp.obtenerCorreoDelToken(token);
            JsonObject jsonRequestPedido = new Gson().fromJson(pedido, JsonObject.class);
            JsonObject jsonResponsePedido = clienteService.crearPedido(correo, jsonRequestPedido);
            return new ResponseEntity<>(jsonResponsePedido, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Agregar un Reclamo",
            description = "Agrega un nuevo Reclamo a un Pedido del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reclamo agregado"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PostMapping(path = "/agregarReclamo")
    public ResponseEntity<?> realizarReclamo(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Datos del nuevo Reclamo", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Reclamo.class)))
            @RequestBody String reclamo) {
        try {
            String correo = clienteHelp.obtenerCorreoDelToken(token);
            JsonObject jsonReclamo = new Gson().fromJson(reclamo, JsonObject.class);
            JsonObject jsonResponse = clienteService.agregarReclamo(correo, jsonReclamo);
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch(JsonIOException | PedidoNoExisteException | EmailNoEnviadoException | PedidoIdException
                | ReclamoComentarioException | ReclamoRazonException | ReclamoNoFinalizadoException
                | ReclamoExisteException | ClienteNoEncontradoException | ClientePedidoNoCoincideException
                | PedidoSinRestauranteException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Calificar a un Restaurante",
            description = "Agrega una Calificación a un Restaurante a través de un Pedido",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Calificacion creada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PostMapping(path = "/calificarRestaurante")
    public ResponseEntity<?> calificarRestaurante(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Id del pedido, puntaje y comentario", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(name = "ejemplo calificacion a restaurante", value = "{\"idPedido\": \"1\","
                                    + "\"puntaje\": \"2.5\","
                                    + "\"comentario\": \"muy rico todo\""
                                    + "}")}))
            @RequestBody String pedido){
        try{
            String correoCliente = clienteHelp.obtenerCorreoDelToken(token);
            JsonObject jsonRequestPedido = new Gson().fromJson(pedido, JsonObject.class);
            restauranteService.calificarRestaurante(correoCliente, jsonRequestPedido);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Modificar una Calificacion realizada a un Restaurante",
            description = "Modifica (reemplaza) una Calificación realizada a un Restaurante a través de un Pedido",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificacion modificada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PutMapping(path = "/modificarCalificacionRestaurante")
    public ResponseEntity<?> modificarCalificacionRestaurante(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Id del pedido, puntaje y comentario", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(name = "ejemplo calificacion a restaurante", value = "{\"idPedido\": \"1\","
                                    + "\"puntaje\": \"2.5\","
                                    + "\"comentario\": \"muy rico todo\""
                                    + "}")}))
            @RequestBody String pedido){
        try{
            String correoCliente = clienteHelp.obtenerCorreoDelToken(token);
            JsonObject jsonRequestPedido = new Gson().fromJson(pedido, JsonObject.class);
            restauranteService.modificarCalificacionRestaurante(correoCliente, jsonRequestPedido);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Elimina una Calificacion realizada a un Restaurante",
            description = "Elimina una Calificación realizada a un Restaurante a través de un Pedido",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificacion eliminada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @DeleteMapping(path = "/eliminarCalificacionRestaurante")
    public ResponseEntity<?> eliminarCalificacionRestaurante(
            @RequestHeader("Authorization") String token,
            @RequestParam (name= "idPedido") String idPedido){
        try{
            String correoCliente = clienteHelp.obtenerCorreoDelToken(token);
            restauranteService.eliminarCalificacionRestaurante(correoCliente, idPedido);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}