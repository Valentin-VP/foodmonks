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

    public void altaMenu(JsonObject jsonMenu, String correoRestaurante) throws UsuarioNoRestaurante, MenuNombreExistente{

            Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
            if (restaurante == null) {
                throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
            }

            if (menuRepository.existsByNombreAndRestaurante(jsonMenu.get("nombre").getAsString(), restaurante)){
                throw new MenuNombreExistente("Ya existe un menu con el nombre " + jsonMenu.get("nombre").getAsString() +
                        " para el restaurante " + correoRestaurante);
            }
            Menu menu = new Menu(
                    jsonMenu.get("nombre").getAsString(),
                    jsonMenu.get("price").getAsFloat(),
                    jsonMenu.get("descripcion").getAsString(),
                    jsonMenu.get("visibilidad").getAsBoolean(),
                    jsonMenu.get("multiplicador").getAsFloat(),
                    jsonMenu.get("imagen").getAsString(),
                    CategoriaMenu.valueOf(jsonMenu.get("categoria").getAsString()),
                    restaurante);
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

    public void modificarMenu(JsonObject jsonMenu, String correoRestaurante) throws UsuarioNoRestaurante, MenuNoEncontradoException {

        Restaurante restaurante = restauranteRepository.findByCorreo(correoRestaurante);
        if (restaurante == null) {
            throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
        }
        Menu menuAux = menuRepository.findByIdAndRestaurante(jsonMenu.get("id").getAsLong(),restaurante);
        if (menuAux == null){
            throw new MenuNoEncontradoException("No existe el Menu con id " + jsonMenu.get("id").getAsLong()  + " para el Restaurante "
                    + correoRestaurante);
        }
        menuAux.setNombre(jsonMenu.get("nombre").getAsString());
        menuAux.setPrice(jsonMenu.get("price").getAsFloat());
        menuAux.setDescripcion(jsonMenu.get("descripcion").getAsString());
        menuAux.setVisible(jsonMenu.get("visibilidad").getAsBoolean());
        menuAux.setMultiplicadorPromocion(jsonMenu.get("multiplicador").getAsFloat());
        menuAux.setImagen(jsonMenu.get("imagen").getAsString());
        menuAux.setCategoria(CategoriaMenu.valueOf(jsonMenu.get("categoria").getAsString()));
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

    public Boolean existeCategoriaMenu(Restaurante restaurante, CategoriaMenu categoriaMenu){
        return menuRepository.existsMenuByRestauranteAndCategoria(restaurante,categoriaMenu);
    }
}
