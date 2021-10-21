package org.foodmonks.backend.Menu;

import ch.qos.logback.core.net.SyslogOutputStream;
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
            System.out.println("Entro a crear");
            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            if (restaurante != null){
                if (!menuRepository.existsByNombreAndRestaurante(nombre, restaurante)){
                    Menu menu = new Menu(nombre, price, descripcion, visible, multiplicadorPromocion, imagen, categoria, restaurante);
                    menuRepository.save(menu);
                    System.out.println("Creo");
                    return true;
                }
                System.out.println("Menu ya creado");
                return false;
           // System.out.println("########### " + restauranteRepository.findById(correoRestaurante).orElseGet(null).getNombreRestaurante());
            }
            System.out.println("Restaurante null");
            return false;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
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
            if ( menuAux != null && menuRepository.existsByNombreAndRestaurante(nombre,restaurante)) {
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

    public DtMenu infoMenu(Long id, String correo) {
        return menuConvertidor.getDtMenu(menuRepository.findByIdAndRestaurante(id,
                restauranteRepository.findById(correo).get()));
    }

    public List<DtMenu> listarMenu(String correoRestaurante){

        return menuConvertidor.connvertirMenu(menuRepository.findMenusByRestaurante(
                restauranteRepository.findById(correoRestaurante).get()));

    }
}
