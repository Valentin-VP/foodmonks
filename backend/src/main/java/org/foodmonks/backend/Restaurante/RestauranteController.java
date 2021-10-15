package org.foodmonks.backend.Restaurante;

import netscape.javascript.JSObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurante")
public class RestauranteController {

    private final  RestauranteService restauranteService;
    private final MenuService menuService;

    @Autowired
    RestauranteController(RestauranteService restauranteService, MenuService menuService) {
        this.menuService = menuService;
        this.restauranteService = restauranteService;
    }

    @PostMapping//CREAR RESTAURANTE
    public void createRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.createRestaurante(restaurante);
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

    @GetMapping//LISTAR RESTAURANTES
    //@GetMapping("/rutaEspecifica")
    public List<Restaurante> listarRestaurante(){
        return restauranteService.listarRestaurante();
    }

    @GetMapping("/buscar")
    public void buscarRestaurante(@RequestParam String correo) {
        restauranteService.buscarRestaurante(correo);
    }

    @DeleteMapping//ELIMINAR RESTAURANTE
    public void elimiarRestaurante(@RequestParam Long id) {
        //restauranteService.eliminarRestaurante(id);
    }

    @PutMapping//EDITAR RESTAURANTE
    public void modificarRestaurante(@RequestBody Restaurante restaurante) {
        restauranteService.editarRestaurante(restaurante);

    }

}
