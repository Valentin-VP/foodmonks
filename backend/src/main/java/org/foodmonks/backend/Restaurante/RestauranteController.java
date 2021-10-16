package org.foodmonks.backend.Restaurante;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.DtMenu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurante")
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final MenuService menuService;

    @Autowired
    TokenHelper tokenHelp;

    @Autowired
    RestauranteController(RestauranteService restauranteService, MenuService menuService) {
        this.menuService = menuService;
        this.restauranteService = restauranteService;
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
    public ResponseEntity<?> createMenu(String infoMenu) {
        String nombreMenu = "";
        Double auxDouble;
        Float precioMenu;
        String descripcionMenu = "";
        Boolean visibilidadMenu = false;
        Float multiplicadorMenu;
        String imagenMenu = "";
        CategoriaMenu categoriaMenu = null;
        String correoRestaurante = "";
        try {
            JSONObject jsonMenu = new JSONObject(infoMenu);
            nombreMenu = jsonMenu.getString("nombre");

            auxDouble = jsonMenu.getDouble("price");
            precioMenu = auxDouble.floatValue();

            descripcionMenu = jsonMenu.getString("descripcion");
            visibilidadMenu = jsonMenu.getBoolean("visibilidad");

            auxDouble = jsonMenu.getDouble("multiplicador");
            multiplicadorMenu = auxDouble.floatValue();

            imagenMenu = jsonMenu.getString("imagen");
            categoriaMenu = CategoriaMenu.valueOf(jsonMenu.getString("categoria"));
            correoRestaurante = jsonMenu.getString("restaurante");

            menuService.altaMenu(nombreMenu, precioMenu, descripcionMenu, visibilidadMenu, multiplicadorMenu, imagenMenu, categoriaMenu, correoRestaurante);
        } catch(JSONException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/listarMenu")
    public ResponseEntity<?> listMenu(@RequestHeader("Authorization") String token) {
        String newtoken = "";
        JSONArray menus = new JSONArray();
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            List<DtMenu> listaMenu = menuService.listarMenu(correo);
            for(int i=0;i<listaMenu.size();i++) {
                JSONObject menu = new JSONObject();
                menu.put("id",listaMenu.get(i).getId());
                menu.put("nombre",listaMenu.get(i).getNombre());
                menu.put("descripcion",listaMenu.get(i).getDescripcion());
                menu.put("precio",listaMenu.get(i).getPrice());
                menu.put("visible",listaMenu.get(i).getVisible());
                menu.put("multiplicadorPromocion", listaMenu.get(i).getMultiplicadorPromocion());
                menu.put("imagen", listaMenu.get(i).getImagen());
                menu.put("categoria", listaMenu.get(i).getCategoria());
                menus.put(menu);
            }
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<JSONArray>(menus, HttpStatus.OK);
    }

    @PutMapping(path = "/modificarMenu")
    public ResponseEntity<?> updateMenu(@RequestHeader("Authorization") String token, String updatedMenu) {
        String newtoken = "";
        Long id;
        String nombreMenu = "";
        Double auxDouble;
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
            JSONObject jsonMenu = new JSONObject(updatedMenu);

            id = jsonMenu.getLong("id");
            nombreMenu = jsonMenu.getString("nombre");
            auxDouble = jsonMenu.getDouble("price");
            priceMenu = auxDouble.floatValue();
            descripcionMenu = jsonMenu.getString("descripcion");
            visibilidadMenu = jsonMenu.getBoolean("visibilidad");
            auxDouble = jsonMenu.getDouble("multiplicador");
            multiplicadorMenu = auxDouble.floatValue();
            imagenMenu = jsonMenu.getString("imagen");
            categoriaMenu = CategoriaMenu.valueOf(jsonMenu.getString("categoria"));

            menuService.modificarMenu(id, nombreMenu, priceMenu, descripcionMenu, visibilidadMenu, multiplicadorMenu, imagenMenu, categoriaMenu, correo);
        } catch(JSONException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/eliminarMenu")
    public ResponseEntity<?> deleteMenu(@RequestHeader("Authorization") String token, Long id) {
        String newtoken = "";
        try {
            if ( token != null && token.startsWith("Bearer ")) {
                newtoken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newtoken);
            menuService.eliminarMenu(id, correo);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
