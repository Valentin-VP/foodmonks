package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.persistencia.MenuID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;
    private final MenuConvertidor menuConvertidor;

    @Autowired
    public MenuService(MenuRepository menuRepository, RestauranteRepository restauranteRepository, MenuConvertidor menuConvertidor)
    { this.menuRepository = menuRepository; this.restauranteRepository = restauranteRepository; this.menuConvertidor = menuConvertidor; }

    public boolean altaMenu(String nombre, Float price, String descripcion, Boolean visible,
                            Float multiplicadorPromocion, String imagen, CategoriaMenu categoria,
                            String correoRestaurante) {

        try{
            Restaurante restaurante = restauranteRepository.findById(correoRestaurante).orElseGet(null);
            System.out.println("########### " + restauranteRepository.findById(correoRestaurante).orElseGet(null).getNombreRestaurante());
            Menu menu = new Menu(nombre, price, descripcion, visible, multiplicadorPromocion, imagen, categoria);
            //if (!menuRepository.existsById(new MenuID(menu.getId(), restaurante.getCorreo()))){
                menu.setRestaurante(restaurante);
                menuRepository.save(menu);
                return true;
            //}
            //return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean eliminarMenu(Long idMenu, String correoRestaurante) {
        try {
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            Menu menu = menuRepository.findByIdAndRestaurante(idMenu, restaurante);
            menuRepository.delete(menu);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean modificarMenu(Long id, String nombre, Float price, String descripcion, Boolean visible,
                                 Float multiplicadorPromocion, String imagen, CategoriaMenu categoria, String correoRestaurante){
        try {
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            Menu menuAux = menuRepository.findByIdAndRestaurante(id,restaurante);
            if ( menuAux != null && !menuRepository.existsByNombreAndRestaurante(nombre,restaurante)) {
                menuAux.setNombre(nombre);
                menuAux.setPrice(price);
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

    public List<DtMenu> listarMenu(String correoRestaurante){

        return menuConvertidor.connvertirMenu(menuRepository.findByRestaurante(
                restauranteRepository.findByCorreo(correoRestaurante)));

    }
}
