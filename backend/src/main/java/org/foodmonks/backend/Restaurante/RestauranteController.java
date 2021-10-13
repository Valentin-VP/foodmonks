package org.foodmonks.backend.Restaurante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("restaurante")
public class RestauranteController {

    private final  RestauranteService restauranteService;

    @Autowired
    RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PostMapping//CREAR RESTAURANTE
    public void createRestaurante(@RequestBody Restaurante restaurante) {
        System.out.println("Entro al post");
        restauranteService.createRestaurante(restaurante);
    }

    @GetMapping//LISTAR RESTAURANTES
    //@GetMapping("/rutaEspecifica")
    public List<Restaurante> listarRestaurante(){
        return restauranteService.listarRestaurante();
    }

    @GetMapping("/buscar")
    public void buscarRestaurante(@RequestParam String correo) {
        System.out.println(correo);
        restauranteService.buscarRestaurante(correo);
    }

    @DeleteMapping//ELIMINAR RESTAURANTE
    public void elimiarRestaurante(@RequestParam Long id) {
        System.out.println(id);
        restauranteService.eliminarRestaurante(id);
    }

    @PutMapping//EDITAR RESTAURANTE
    public void modificarRestaurante(@RequestBody Restaurante restaurante) {
        if (restaurante == null) {
            System.out.println("no existe el restaurante");
        }else {
            System.out.println(restaurante.getNombre());
            restauranteService.editarRestaurante(restaurante);
        }

    }

}
