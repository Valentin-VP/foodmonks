package org.foodmonks.backend.Menu;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.datatypes.CategoriaMenu;
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

    public void altaMenu(String nombre, Float price, String descripcion, Boolean visible,
                            Float multiplicadorPromocion, String imagen, CategoriaMenu categoria,
                            String correoRestaurante) throws UsuarioNoRestaurante, MenuNombreExistente{

            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            if (restaurante == null) {
                throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
            }
            if (menuRepository.existsByNombreAndRestaurante(nombre, restaurante)){
                throw new MenuNombreExistente("Ya existe un menu con el nombre " + nombre +
                        " para el restaurante " + correoRestaurante);
            }
            Menu menu = new Menu(nombre, price, descripcion, visible, multiplicadorPromocion, imagen, categoria, restaurante);
            menuRepository.save(menu);
    }

    public void eliminarMenu(Long idMenu, String correoRestaurante) throws MenuNoEncontradoException {

        Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
        Menu menu = menuRepository.findByIdAndRestaurante(idMenu, restaurante);
        if (menu == null){
            throw new MenuNoEncontradoException("No se encontro el Menu con id "+ idMenu + " para el Restuarante "
                    + correoRestaurante);
        }
        menuRepository.delete(menu);
    }

    public void modificarMenu(Long id, String nombre, Float price, String descripcion, Boolean visible,
                                 Float multiplicadorPromocion, String imagen, CategoriaMenu categoria,
                         String correoRestaurante) throws UsuarioNoRestaurante, MenuNoEncontradoException {

        Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
        if (restaurante == null) {
            throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
        }
        Menu menuAux = menuRepository.findByIdAndRestaurante(id,restaurante);
        if (menuAux == null){
            throw new MenuNoEncontradoException("No existe el Menu con id " + id  + " para el Restaurante "
                    + correoRestaurante);
        }
        menuAux.setNombre(nombre);
        menuAux.setPrice(price);
        menuAux.setDescripcion(descripcion);
        menuAux.setVisible(visible);
        menuAux.setMultiplicadorPromocion(multiplicadorPromocion);
        menuAux.setImagen(imagen);
        menuAux.setCategoria(categoria);
        menuRepository.save(menuAux);
    }

    public JsonObject infoMenu(Long id, String correo) {
        return menuConvertidor.jsonMenu(menuRepository.findByIdAndRestaurante(id,
                restauranteRepository.findById(correo).get()));
    }

    public List<JsonObject> listarMenu(String correoRestaurante){

        return menuConvertidor.listaJsonMenu(menuRepository.findMenusByRestaurante(
                restauranteRepository.findById(correoRestaurante).get()));

    }
}
