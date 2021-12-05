package org.foodmonks.backend.Menu;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Menu.Exceptions.*;
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
    private final MenuConverter menuConverter;

    @Autowired
    public MenuService(MenuRepository menuRepository, RestauranteRepository restauranteRepository, MenuConverter menuConverter)
    { this.menuRepository = menuRepository; this.restauranteRepository = restauranteRepository; this.menuConverter = menuConverter; }

    public void altaMenu(JsonObject jsonMenu, String correoRestaurante) throws UsuarioNoRestaurante, MenuNombreExistente, MenuPrecioException, MenuMultiplicadorException, MenuNombreException {

            Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correoRestaurante);
            if (restaurante == null) {
                throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
            }
            if (menuRepository.existsMenuByNombreIgnoreCaseAndRestaurante(jsonMenu.get("nombre").getAsString(), restaurante)){
                throw new MenuNombreExistente("Ya existe un menu con el nombre " + jsonMenu.get("nombre").getAsString() +
                        " para el restaurante " + correoRestaurante);
            }
            verificarJsonMenu(jsonMenu);
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

    public void eliminarMenu(Long idMenu, String correoRestaurante) throws MenuNoEncontradoException, MenuCantidadException {

        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correoRestaurante);
        Menu menu = menuRepository.findByIdAndRestaurante(idMenu, restaurante);
        if (menu == null){
            throw new MenuNoEncontradoException("No se encontro el Menu con id "+ idMenu + " para el Restuarante "
                    + correoRestaurante);
        }
        if (restaurante.getMenus().size() == 3){
            throw new MenuCantidadException("No puede eliminar el menu, el restaurante tiene el minimo de cantidad de menus");
        }
        restaurante.getMenus().remove(menu);
        menuRepository.delete(menu);
    }

    public void modificarMenu(JsonObject jsonMenu, String correoRestaurante) throws UsuarioNoRestaurante, MenuNoEncontradoException, MenuPrecioException, MenuMultiplicadorException, MenuNombreException, MenuNombreExistente {

        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correoRestaurante);
        if (restaurante == null) {
            throw new UsuarioNoRestaurante("El correo "+ correoRestaurante + " no pertenece a un restaurante");
        }
        Menu menuAux = menuRepository.findByIdAndRestaurante(jsonMenu.get("id").getAsLong(),restaurante);
        if (menuAux == null){
            throw new MenuNoEncontradoException("No existe el Menu con id " + jsonMenu.get("id").getAsLong()  + " para el Restaurante "
                    + correoRestaurante);
        }
        if (!menuAux.getNombre().equals(jsonMenu.get("nombre").getAsString())){
            if (menuRepository.existsMenuByNombreIgnoreCaseAndRestaurante(jsonMenu.get("nombre").getAsString(),restaurante)){
                throw new MenuNombreExistente("Ya existe un menu con el nombre " + jsonMenu.get("nombre").getAsString());
            }
        }
        verificarJsonMenu(jsonMenu);
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
        return menuConverter.jsonMenu(menuRepository.findByIdAndRestaurante(id,
                restauranteRepository.findRestaurantesByCorreoIgnoreCase(correo)));
    }

    public List<JsonObject> listarMenu(String correoRestaurante){
        return menuConverter.listaJsonMenu(menuRepository.findMenusByRestaurante(
                restauranteRepository.findRestaurantesByCorreoIgnoreCase(correoRestaurante)));
    }

    public Boolean existeCategoriaMenu(Restaurante restaurante, CategoriaMenu categoriaMenu){
        return menuRepository.existsMenuByRestauranteAndCategoria(restaurante,categoriaMenu);
    }

    public Menu obtenerMenu(Long id, Restaurante restaurante){
        return menuRepository.findByIdAndRestaurante(id,restaurante);
    }

    public void verificarJsonMenu(JsonObject jsonMenu) throws MenuPrecioException, MenuMultiplicadorException, MenuNombreException {
        if (!jsonMenu.get("price").getAsString().matches("^\\d+(.\\d+)*$") || jsonMenu.get("price").getAsString().isBlank()) {
            throw new MenuPrecioException("El precio debe ser un numero real");
        }
        if (!jsonMenu.get("multiplicador").getAsString().matches("^\\d+(.\\d+)*$") || jsonMenu.get("multiplicador").getAsString().isBlank()) {
            throw new MenuMultiplicadorException("El multiplicador debe ser un numero real");
        }
        if (jsonMenu.get("nombre").getAsString().isBlank()){
            throw new MenuNombreException("El nombre del menu no puede ser vacio");
        }
    }

    public List<JsonObject> listMenuRestauranteCategoria(Restaurante restaurante, CategoriaMenu categoriaMenu){
        return menuConverter.listaJsonMenu(menuRepository.findMenuByRestauranteAndCategoria(restaurante,categoriaMenu));
    }

}
