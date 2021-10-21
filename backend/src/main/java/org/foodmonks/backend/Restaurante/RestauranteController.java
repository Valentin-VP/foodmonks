package org.foodmonks.backend.Restaurante;

import com.google.gson.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.DtMenu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.jpa.repository.Modifying;
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
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/listarMenu")
    public ResponseEntity<?> listMenu(@RequestHeader("Authorization") String token) {
        String newtoken = "";
        List<DtMenu> listaMenu = new ArrayList<DtMenu>();
        JsonArray jsonArray = new JsonArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            listaMenu = menuService.listarMenu(correo);
            for(int i=0;i<listaMenu.size();i++) {
                JsonObject menu = new JsonObject();
                menu.addProperty("id",listaMenu.get(i).getId());
                menu.addProperty("nombre",listaMenu.get(i).getNombre());
                menu.addProperty("descripcion",listaMenu.get(i).getDescripcion());
                menu.addProperty("price",listaMenu.get(i).getPrice());
                menu.addProperty("visible",listaMenu.get(i).getVisible());
                menu.addProperty("multiplicadorPromocion", listaMenu.get(i).getMultiplicadorPromocion());
                menu.addProperty("imagen", listaMenu.get(i).getImagen());
                menu.addProperty("categoria", listaMenu.get(i).getCategoria().toString());
                jsonArray.add(menu);
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
        JsonObject retorno = new JsonObject();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            DtMenu dtMenu = menuService.infoMenu(menuId, correo);
            retorno.addProperty("nombre", dtMenu.getNombre());
            retorno.addProperty("id", dtMenu.getId());
            retorno.addProperty("categoria", dtMenu.getCategoria().name());
            retorno.addProperty("multiplicadorPromocion", dtMenu.getMultiplicadorPromocion());
            retorno.addProperty("descripcion", dtMenu.getDescripcion());
            retorno.addProperty("price", dtMenu.getPrice());
            retorno.addProperty("imagen", dtMenu.getImagen());

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(retorno, HttpStatus.OK);
    }
/*
    @PutMapping("/cambiarEstado")
    public void modificarEstado(String correo,EstadoRestaurante estado){
        restauranteService.modificarEstado(correo,estado);
    }
*/
}
