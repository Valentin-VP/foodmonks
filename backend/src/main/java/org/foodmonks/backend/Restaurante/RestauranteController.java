package org.foodmonks.backend.Restaurante;

import com.google.gson.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
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
    private final TokenHelper tokenHelp;

    @Autowired
    RestauranteController(RestauranteService restauranteService, MenuService menuService, TokenHelper tokenHelper) {
        this.menuService = menuService;
        this.restauranteService = restauranteService;
        this.tokenHelp = tokenHelper;
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
            Direccion direccion = new Direccion(
                    jsonDireccion.get("numero").getAsInt(),
                    jsonDireccion.get("calle").getAsString(),
                    jsonDireccion.get("esquina").getAsString(),
                    jsonDireccion.get("detalles").getAsString(),
                    jsonDireccion.get("latitud").getAsString(),
                    jsonDireccion.get("longitud").getAsString()
            );

            // Obtener los menus
            JsonArray jsonMenusRequest = jsonRestaurante.get("menus").getAsJsonArray();
            System.out.println("jsonMenusRequest " + jsonMenusRequest);
            ArrayList<JsonObject> jsonMenus = new ArrayList<JsonObject>();
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
        String newToken = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correoRestaurante = tokenHelp.getUsernameFromToken(newToken);
            JsonObject jsonMenu = new Gson().fromJson(infoMenu, JsonObject.class);
            menuService.altaMenu(jsonMenu, correoRestaurante);
        } catch(JsonParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String correo = "";
        List<JsonObject> listaMenu = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            correo = tokenHelp.getUsernameFromToken(newtoken);
            listaMenu = menuService.listarMenu(correo);
            for(JsonObject jsonMenu : listaMenu) {
                if(jsonMenu.get("multiplicadorPromocion").getAsString().equals("0.0")) {
                    jsonArray.add(jsonMenu);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String newtoken = "";
        List<JsonObject> listaPromo = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            listaPromo = menuService.listarMenu(correo);
            for(JsonObject jsonPromo : listaPromo) {
                if(!jsonPromo.get("multiplicadorPromocion").getAsString().equals("0.0")) {
                    jsonArray.add(jsonPromo);
                }
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(ExpiredJwtException a) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        String newtoken = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);

            // Transformar json string en JsonObject
            JsonObject jsonMenu = new Gson().fromJson(updatedMenu, JsonObject.class);
            jsonMenu.addProperty("id", menuId);
            menuService.modificarMenu(jsonMenu, correo);
        } catch(JsonParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String newtoken = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            menuService.eliminarMenu(menuId, correo);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String newtoken = "";
        JsonObject retorno;
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            retorno = menuService.infoMenu(menuId, correo);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String newtoken = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            restauranteService.modificarEstado(correo, EstadoRestaurante.valueOf(estado));
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String newtoken = "";
        String correo = "";
        List<JsonObject> listaMenu = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            correo = tokenHelp.getUsernameFromToken(newtoken);
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
                                                    @RequestParam(required = false, name = "page") String page,
                                                    @RequestParam(required = false, name = "size") String size) {
        String newtoken = "";
        String correo = "";
        List<JsonObject> listaPedidos = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            correo = tokenHelp.getUsernameFromToken(newtoken);
            String[] _total = (!total.isEmpty() && total.contains(",")) ? total.split(",") : null;
            String[] _fecha = (!fecha.isEmpty() && fecha.contains(",")) ? fecha.split(",") : null;
            //String[] _order = (!orden.isEmpty() && orden.contains(",")) ? orden.split(",") : null;
            Float[] totalFinal = new Float[2];
            LocalDateTime[] fechaFinal = new LocalDateTime[2];

            EstadoPedido estado = null;
            MedioPago pago = null;
            int pageFinal = 0;
            int sizeFinal = 10;
            if (!estadoPedido.equals("")) {
                try {
                    estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
                }catch(IllegalArgumentException e){
                    estado = null;
                }
            }
            if (!medioPago.equals("")) {
                try{
                    pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
                }catch(IllegalArgumentException e){
                    pago = null;
                }
            }

            if (_total != null){
                try{
                    totalFinal[0] = Math.abs(Float.valueOf(_total[0]));
                    totalFinal[1] = Math.abs(Float.valueOf(_total[1]));
                }catch(NumberFormatException e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
                }
            }

            if (_fecha != null){
                try{
                    fechaFinal[0] = LocalDateTime.from(LocalDate.parse(_fecha[0]));
                    fechaFinal[1] = LocalDateTime.from(LocalDate.parse(_fecha[1]));
                }catch(DateTimeException e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
                }
            }
            try{
                pageFinal = Integer.parseInt(page);
                sizeFinal = Integer.parseInt(size);
            }catch(NumberFormatException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
            }
            listaPedidos = restauranteService.listarHistoricoPedidos(correo, estado, pago, orden, fechaFinal, totalFinal, pageFinal, sizeFinal);
            for(JsonObject jsonPedido : listaPedidos) {
                jsonArray.add(jsonPedido);
            }
        } catch (JsonIOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la solicitud.");
        } catch (RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(jsonArray, HttpStatus.OK);
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
        String newtoken = "";
        JsonObject jsonPedido = new JsonObject();


        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String estado = "";
            String correo = tokenHelp.getUsernameFromToken(newtoken);
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
}
