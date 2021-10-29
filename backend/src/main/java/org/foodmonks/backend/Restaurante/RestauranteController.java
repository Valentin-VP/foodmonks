package org.foodmonks.backend.Restaurante;

import com.google.gson.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    @Operation(summary = "Crear Restaurante", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping//CREAR RESTAURANTE
    public void createRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.createRestaurante(restaurante);
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
        String aux;
        String newToken = "";

        String nombreMenu = "";
        Float precioMenu;
        String descripcionMenu = "";
        Boolean visibilidadMenu = false;
        Float multiplicadorMenu;
        String imagenMenu = "";
        CategoriaMenu categoriaMenu = null;
        String correoRestaurante = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            correoRestaurante = tokenHelp.getUsernameFromToken(newToken);

            JSONObject jsonMenu = new JSONObject(infoMenu);
            nombreMenu = jsonMenu.getString("nombre");

            aux = jsonMenu.getString("price");
            precioMenu = Float.valueOf(aux);

            descripcionMenu = jsonMenu.getString("descripcion");
            visibilidadMenu = jsonMenu.getBoolean("visibilidad");

            aux = jsonMenu.getString("multiplicador");
            multiplicadorMenu = Float.valueOf(aux);

            imagenMenu = jsonMenu.getString("imagen");
            categoriaMenu = CategoriaMenu.valueOf(jsonMenu.getString("categoria"));

            menuService.altaMenu(nombreMenu, precioMenu, descripcionMenu, visibilidadMenu, multiplicadorMenu, imagenMenu, categoriaMenu, correoRestaurante);
        } catch(JSONException e) {
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
        List<JsonObject> listaMenu = new ArrayList<JsonObject>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            listaMenu = menuService.listarMenu(correo);
            for(JsonObject jsonMenu : listaMenu) {
                jsonArray.add(jsonMenu);
            }
        } catch (JsonIOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        JsonObject jsonMenu = new JsonObject();

        String nombreMenu = "";
        Float priceMenu;
        String descripcionMenu = "";
        Boolean visibilidadMenu = false;
        Float multiplicadorMenu;
        String imagenMenu = "";
        CategoriaMenu categoriaMenu = null;
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);

            // Transformar json string en JsonObject
            jsonMenu = new Gson().fromJson(updatedMenu, JsonObject.class);

            nombreMenu = jsonMenu.get("nombre").getAsString();
            priceMenu = jsonMenu.get("price").getAsFloat();
            descripcionMenu = jsonMenu.get("descripcion").getAsString();
            visibilidadMenu = jsonMenu.get("visibilidad").getAsBoolean();
            multiplicadorMenu = jsonMenu.get("multiplicador").getAsFloat();
            imagenMenu = jsonMenu.get("imagen").getAsString();
            categoriaMenu = CategoriaMenu.valueOf(jsonMenu.get("categoria").getAsString());

            menuService.modificarMenu(menuId, nombreMenu, priceMenu, descripcionMenu, visibilidadMenu, multiplicadorMenu, imagenMenu, categoriaMenu, correo);
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

}
