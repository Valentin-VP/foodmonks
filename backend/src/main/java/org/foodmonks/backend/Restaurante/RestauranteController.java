package org.foodmonks.backend.Restaurante;

import com.google.gson.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.foodmonks.backend.datatypes.EstadoRestaurante;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurante")
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

    @PostMapping//CREAR RESTAURANTE
    public void createRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.createRestaurante(restaurante);
    }

    @GetMapping//LISTAR RESTAURANTES
    //@GetMapping("/rutaEspecifica")
    public List<Restaurante> listarRestaurante(){
        return restauranteService.listarRestaurante();
    }

    @GetMapping("/buscar")
    public void buscarRestaurante(@RequestParam String correo) {
        restauranteService.buscarRestaurante(correo);
    }

    @PutMapping//EDITAR RESTAURANTE
    public void modificarRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.editarRestaurante(restaurante);

    }

    @DeleteMapping//ELIMINAR RESTAURANTE
    public void elimiarRestaurante(@RequestParam Long id) {
        //restauranteService.eliminarRestaurante(id);
    }

    @PostMapping(path = "/agregarMenu")
    public ResponseEntity<?> createMenu(@RequestHeader("Authorization") String token, @RequestBody String infoMenu) {
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

    @PutMapping(path = "/modificarMenu/{menuId}")
    public ResponseEntity<?> updateMenu(@RequestHeader("Authorization") String token, @PathVariable Long menuId, @RequestBody String updatedMenu) {
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
        } catch (MenuNombreExistente menuNombreExistente) {
            return new ResponseEntity<>(menuNombreExistente, HttpStatus.CONFLICT);
        } catch (MenuNoEncontradoException menuNoEncontradoException) {
            return new ResponseEntity<>(menuNoEncontradoException, HttpStatus.NOT_FOUND);
        } catch (UsuarioNoRestaurante usuarioNoRestaurante) {
            return new ResponseEntity<>(usuarioNoRestaurante, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

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
