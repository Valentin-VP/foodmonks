package org.foodmonks.backend.Restaurante;

import com.google.gson.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.foodmonks.backend.Cliente.ClienteService;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.foodmonks.backend.datatypes.EstadoRestaurante;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/restaurante")
@Tag(name = "restaurante", description = "API de Restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final MenuService menuService;
    private final RestauranteHelper restauranteHelper;
    private final DireccionService direccionService;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    @Autowired
    RestauranteController(RestauranteService restauranteService, MenuService menuService,
                          DireccionService direccionService, ClienteService clienteService,
                          PedidoService pedidoService, RestauranteHelper restauranteHelper) {
        this.menuService = menuService;
        this.restauranteService = restauranteService;
        this.restauranteHelper = restauranteHelper;
        this.direccionService = direccionService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Crea un nuevo Restaurante",
            description = "Registra un pedido de alta de un nuevo Restaurante con sus Menús",
            tags = { "restaurante" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Su solicitud de alta fue recibida con éxito"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PostMapping(path = "/crearSolicitudAltaRestaurante")//CREAR RESTAURANTE
    public ResponseEntity<?> crearSolicitudAltaRestaurante(
            @Parameter(description = "Nuevo Restaurante con sus Menús", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Restaurante.class)))
            @RequestBody String restaurante) {
        try{
            JsonObject jsonRestaurante = new Gson().fromJson(restaurante, JsonObject.class);
            System.out.println("jsonRestaurante "+jsonRestaurante);
            // Obtener direccion
            JsonObject jsonDireccion = jsonRestaurante.get("direccion").getAsJsonObject();
            System.out.println("jsonDireccion "+jsonDireccion);
            Direccion direccion = direccionService.crearDireccion(jsonDireccion);

            // Obtener los menus
            JsonArray jsonMenusRequest = jsonRestaurante.get("menus").getAsJsonArray();
            System.out.println("jsonMenusRequest " + jsonMenusRequest);
            ArrayList<JsonObject> jsonMenus = new ArrayList<>();
            for (JsonElement json: jsonMenusRequest) {
                JsonObject jsonMenu = json.getAsJsonObject();
                jsonMenus.add(jsonMenu);
            }

            restauranteService.createSolicitudAltaRestaurante(
                    jsonRestaurante.get("nombre").getAsString(),
                    jsonRestaurante.get("apellido").getAsString(),
                    jsonRestaurante.get("correo").getAsString(),
                    new String(Base64.getDecoder().decode(jsonRestaurante.get("password").getAsString())),
                    LocalDate.now(),
                    5.0f,
                    jsonRestaurante.get("nombreRestaurante").getAsString(),
                    jsonRestaurante.get("rut").getAsString(),
                    direccion,
                    EstadoRestaurante.valueOf("PENDIENTE"),
                    jsonRestaurante.get("telefono").getAsString(),
                    jsonRestaurante.get("descripcion").getAsString(),
                    jsonRestaurante.get("cuentaPaypal").getAsString(),
                    jsonRestaurante.get("url").getAsString(),
                    jsonMenus
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
            // "Su solicitud de alta fue recibida con éxito"
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
  

    @Operation(summary = "Listar Restaurantes", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping//LISTAR RESTAURANTES
    //@GetMapping("/rutaEspecifica")
    public List<Restaurante> listarRestaurante(){
        return restauranteService.listarRestaurante();
    }

    @Operation(summary = "Buscar Restaurante", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/buscar")
    public void buscarRestaurante(@RequestParam String correo) {
        restauranteService.buscarRestaurante(correo);
    }

    @Operation(summary = "Modificar Restaurante", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping//MODIFICAR RESTAURANTE
    public void modificarRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.editarRestaurante(restaurante);

    }

    @Operation(summary = "Eliminar Restaurante", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping//ELIMINAR RESTAURANTE
    public void elimiarRestaurante(@RequestParam Long id) {
        //restauranteService.eliminarRestaurante(id);
    }

    @Operation(summary = "Crea un nuevo Menu",
            description = "Agrega un nuevo Menu al Restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PostMapping(path = "/agregarMenu")
    public ResponseEntity<?> createMenu(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Crea un nuevo Menu en el Restaurante", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Menu.class)))
            @RequestBody String infoMenu) {
        try {
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            JsonObject jsonMenu = new Gson().fromJson(infoMenu, JsonObject.class);
            menuService.altaMenu(jsonMenu, correoRestaurante);
        } catch(JsonParseException | MenuPrecioException | MenuMultiplicadorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MenuNombreExistente menuNombreExistente) {
            return new ResponseEntity<>(menuNombreExistente, HttpStatus.CONFLICT);
        } catch (UsuarioNoRestaurante usuarioNoRestaurante) {
            return new ResponseEntity<>(usuarioNoRestaurante, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar los Menus",
            description = "Lista de los Menus de un restaurantes",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Menu.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarMenu")
    public ResponseEntity<?> listMenu(@RequestHeader("Authorization") String token) {
        String newtoken = "";
        List<JsonObject> listaMenu;
        JsonArray jsonArray = new JsonArray();
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            listaMenu = menuService.listarMenu(correo);
            for(JsonObject jsonMenu : listaMenu) {
                if(jsonMenu.get("multiplicadorPromocion").getAsString().equals("0.0")) {
                    jsonArray.add(jsonMenu);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Listar las Promociones",
            description = "Lista de las Promociones de un restaurantes",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "promocion" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Menu.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarPromocion")
    public ResponseEntity<?> listPromo(@RequestHeader("Authorization") String token) {
        List<JsonObject> listaPromo;
        JsonArray jsonArray = new JsonArray();
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            listaPromo = menuService.listarMenu(correo);
            for(JsonObject jsonPromo : listaPromo) {
                if(!jsonPromo.get("multiplicadorPromocion").getAsString().equals("0.0")) {
                    jsonArray.add(jsonPromo);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(ExpiredJwtException a) {
            return new ResponseEntity<>(a.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Modificar un Menu",
            description = "Modifica un menu de un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PutMapping(path = "/modificarMenu/{menuId}")
    public ResponseEntity<?> updateMenu(@RequestHeader("Authorization") String token, @PathVariable Long menuId, @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Menu.class)))
    @RequestBody String updatedMenu) {
        try {

            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            // Transformar json string en JsonObject
            JsonObject jsonMenu = new Gson().fromJson(updatedMenu, JsonObject.class);
            jsonMenu.addProperty("id", menuId);
            menuService.modificarMenu(jsonMenu, correo);
        } catch(JsonParseException | MenuPrecioException | MenuMultiplicadorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MenuNoEncontradoException menuNoEncontradoException) {
            return new ResponseEntity<>(menuNoEncontradoException, HttpStatus.NOT_FOUND);
        } catch (UsuarioNoRestaurante usuarioNoRestaurante) {
            return new ResponseEntity<>(usuarioNoRestaurante, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un Menu",
            description = "Eliminar un menu de un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @DeleteMapping(path = "/eliminarMenu/{menuId}")
    public ResponseEntity<?> deleteMenu(@RequestHeader("Authorization") String token, @PathVariable Long menuId) {
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            menuService.eliminarMenu(menuId, correo);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Obtener un Menu",
              description = "Obtener un Menu de un restaurante",
              security = @SecurityRequirement(name = "bearerAuth"),
              tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(schema = @Schema(implementation = Menu.class))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error", content = @Content)
    })
    @GetMapping(path = "getInfoMenu/{menuId}")
    public ResponseEntity<?> getMenuInfo(@RequestHeader("Authorization") String token, @PathVariable Long menuId) {
        JsonObject retorno;
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            retorno = menuService.infoMenu(menuId, correo);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(retorno, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un Restaurante",
            description = "Obtener un Restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "restaurante" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error", content = @Content)
    })
    @GetMapping(path = "/getInfoRestaurante")
    public ResponseEntity<?> getRestauranteInfo(@RequestHeader("Authorization") String token) {
        JsonObject retorno;
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            retorno = restauranteService.obtenerJsonRestaurante(correo);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(retorno, HttpStatus.OK);
    }

    @Operation(summary = "Modificar el estado de un Menu",
            description = "Modifica el estado de un menu de un restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PutMapping(path = "/modificarEstado/{estado}")
    public ResponseEntity<?> modificarEstado(@RequestHeader("Authorization") String token, @PathVariable String estado){
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            restauranteService.modificarEstado(correo, EstadoRestaurante.valueOf(estado));
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Listar los Pedidos Pendientes",
            description = "Lista de los pedidos pendientes de confirmación.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "menu" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarPedidosPendientes")
    public ResponseEntity<?> listarPedidosPendientes(@RequestHeader("Authorization") String token) {
        List<JsonObject> listaMenu = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            listaMenu = restauranteService.listarPedidosPendientes(correo);
            for(JsonObject jsonMenu : listaMenu) {
                jsonArray.add(jsonMenu);
            }
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Listar los Pedidos en Efectivo sin cobrar",
            description = "Lista de los Pedidos en efectivo con EstadoPedido = COMPLETADO y MedioPago = EFECTIVO.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedido" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarPedidosEfectivoCompletado")
    public ResponseEntity<?> listarPedidosEfectivoCompletado(@RequestHeader("Authorization") String token) {
        List<JsonObject> listaPedidos;
        JsonArray jsonArray = new JsonArray();
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            listaPedidos = restauranteService.listarPedidosEfectivoConfirmados(correo);
            for(JsonObject jsonMenu : listaPedidos) {
                jsonArray.add(jsonMenu);
            }
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Listar Historico Pedidos",
            description = "Lista de los pedidos realizados (finalizados o rechazados) al Restaurante",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedidos" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping(path = "/listarHistoricoPedidos")
    public ResponseEntity<?> listarHistoricoPedidos(@RequestHeader("Authorization") String token,
                                                    @RequestParam(required = false, name = "estadoPedido") String estadoPedido,
                                                    @RequestParam(required = false, name = "medioPago") String medioPago,
                                                    @RequestParam(required = false, name = "orden") String orden,
                                                    @RequestParam(required = false, name = "fecha") String fecha,
                                                    @RequestParam(required = false, name = "total") String total,
                                                    @RequestParam(defaultValue = "0",required = false, name = "page") String page,
                                                    @RequestParam(defaultValue = "5", required = false, name = "size") String size) {
        List<JsonObject> listaPedidos = new ArrayList<JsonObject>();
        JsonObject jsonObject = new JsonObject();
        try {
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            jsonObject = restauranteService.listarHistoricoPedidos(correo, estadoPedido, medioPago, orden, fecha, total, page, size);
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

  
    @Operation(summary = "Cambia el estado del pedido",
            description = "Cambia el estado del pedido al estado necesario.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedido" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PutMapping(path = "/actualizarEstadoPedido/{idPedido}")
    public ResponseEntity<?> actualizarEstadoPedido(@RequestHeader("Authorization") String token, @PathVariable String idPedido, @RequestBody String nuevoEstado) {
        JsonObject jsonPedido = new JsonObject();
        try {
            String estado = "";
            String correo = restauranteHelper.obtenerCorreoDelToken(token);
            jsonPedido = new Gson().fromJson(nuevoEstado, JsonObject.class);
            estado = jsonPedido.get("estado").getAsString();
            if (estado!=null){
                if (estado.equals("FINALIZADO")) {
                    try {
                        restauranteService.registrarPagoEfectivo(correo, Long.valueOf(idPedido));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID debe ser numérico.");
                    }
                }else if(estado.equals("CONFIRMADO") || estado.equals("RECHAZADO")){
                    String minutos = jsonPedido.get("minutos").getAsString();
                    try {
                        restauranteService.actualizarEstadoPedido(correo, Long.valueOf(idPedido), estado, Integer.valueOf(minutos));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud. Estado incorrecto.");
                }
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud. Falta estado.");
            }
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (PedidoNoExisteException | RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Se cambió el estado del pedido.");
    }

    @Operation(summary = "Calificar a un Cliente",
            description = "Agrega una Calificación a un Cliente a través de un Pedido",
            tags = { "restaurante", "pedido", "calificacion" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Calificacion creada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PostMapping(path = "/calificarCliente")
    public ResponseEntity<?> calificarCliente(
            @RequestHeader("Authorization") String token,
            @RequestBody String pedido){
        try{
            // Obtener correo del restaurante
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            JsonObject jsonRequest = new Gson().fromJson(pedido, JsonObject.class);
            clienteService.calificarCliente(correoRestaurante, jsonRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Modificar una Calificacion realizada a un Cliente",
            description = "Modifica (reemplaza) una Calificación realizada a un Cliente a través de un Pedido",
            tags = { "restaurante", "pedido", "calificacion" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificacion modificada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @PutMapping(path = "/modificarCalificacionCliente")
    public ResponseEntity<?> modificarCalificacionCliente(
            @RequestHeader("Authorization") String token,
            @RequestBody String pedido){
        try{
            // Obtener correo del restaurante
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            JsonObject jsonRequest = new Gson().fromJson(pedido, JsonObject.class);
            clienteService.modificarCalificacionCliente(correoRestaurante, jsonRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Elimina una Calificacion realizada a un Cliente",
            description = "Elimina una Calificación realizada a un Cliente a través de un Pedido",
            tags = { "restaurante", "pedido", "calificacion" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificacion eliminada"),
            @ApiResponse(responseCode = "400", description = "Ha courrido un error")
    })
    @DeleteMapping(path = "/eliminarCalificacionCliente")
    public ResponseEntity<?> eliminarCalificacionCliente(
            @RequestHeader("Authorization") String token,
            @RequestParam (name= "idPedido") String idPedido){
        try{
            // Obtener correo del restaurante
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            clienteService.eliminarCalificacionCliente(correoRestaurante, idPedido);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    //listar buscar reclamos hechos por clientes
    @GetMapping(path = "/listarReclamos")
    public ResponseEntity<?> listarReclamos(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false, name = "orden") boolean orden,
            @RequestParam(required = false, name = "cliente") String correoCliente,
            @RequestParam(required = false, name = "razon") String razon
    ) {
        JsonArray jsonArray = new JsonArray();
        try {
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            jsonArray = restauranteService.listarReclamos(correoRestaurante, orden, correoCliente, razon);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
    }

    @Operation(summary = "Obtiene detalles de un Pedido",
            description = "Obtiene un Pedido con su información",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "pedido"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @GetMapping("/obtenerPedido")
    public ResponseEntity<?> obtenerPedido(
            @RequestParam(name = "id") String idPedido){
        JsonObject pedidoResponse;
        try{
            Long id = Long.valueOf(idPedido);
            pedidoResponse = pedidoService.buscarPedidoById(id);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(pedidoResponse, HttpStatus.OK);
    }

    @PostMapping("/realizarDevolucion")
    public ResponseEntity<?> realizarDevolucion(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "idPedido") String idPedido,
            @RequestParam(name = "estadoDevolucion") boolean estadoDevolucion,
            @RequestBody String motivoDevolucion){
        JsonObject response = new JsonObject();
        try {
            JsonObject jsonMotivoDevolucion = new Gson().fromJson(motivoDevolucion, JsonObject.class);
            String motivo = jsonMotivoDevolucion.get("motivoDevolucion").getAsString();
            String correoRestaurante = restauranteHelper.obtenerCorreoDelToken(token);
            response = restauranteService.realizarDevolucion(correoRestaurante, idPedido, motivo, estadoDevolucion);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
