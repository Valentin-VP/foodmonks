package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, RestauranteRepository restauranteRepository)
    { this.menuRepository = menuRepository; this.restauranteRepository = restauranteRepository; }

    public boolean altaMenu(String nombre, Float precio, String descripcion, Boolean visible,
                            Float multiplicadorPromocion, String imagen, CategoriaMenu categoria,
                            String correoRestaurante) {

        try{
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            Menu menu = new Menu(nombre, precio, descripcion, visible, multiplicadorPromocion, imagen, categoria);
            if (!menuRepository.existsByNombreAndRestaurante(menu.getNombre(),restaurante)){
                menu.setRestaurante(restaurante);
                menuRepository.save(menu);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean eliminarMenu(Long idMenu, String correoRestaurante) {
        try {
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            Menu menu = menuRepository.findByIdAndRestaurante(idMenu, restaurante);
            menuRepository.delete(menu);
            return  true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean modificarMenu(Long id, String nombre, Float precio, String descripcion, Boolean visible,
                                 Float multiplicadorPromocion, String imagen, CategoriaMenu categoria, String correoRestaurante){
        try {
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            Menu menuAux = menuRepository.findByIdAndRestaurante(id,restaurante);
            if ( menuAux != null && !menuRepository.existsByNombreAndRestaurante(nombre,restaurante)) {
                menuAux.setNombre(nombre);
                menuAux.setPrecio(precio);
                menuAux.setDescripcion(descripcion);
                menuAux.setVisible(visible);
                menuAux.setMultiplicadorPromocion(multiplicadorPromocion);
                menuAux.setImagen(imagen);
                menuAux.setCategoria(categoria);
                menuRepository.save(menuAux);
                return true;
            }
            return false;
        } catch (Exception e) {
            return  false;
        }
    }

}
