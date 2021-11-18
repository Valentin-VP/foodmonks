package org.foodmonks.backend.MenuCompra;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Menu.Menu;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuCompraConverter {

    public List<MenuCompra> listaMenuCompra(List<Menu> menus){
        List<MenuCompra> menusComprados = new ArrayList<>();
        for (Menu menu : menus){
            menusComprados.add(menuCompra(menu));
        }
        return menusComprados;
    }

    public JsonArray arrayJsonMenuCompra (List<MenuCompra> menusComprados){
        JsonArray arrayJsonClientes = new JsonArray();
        for (MenuCompra menuComprado : menusComprados){
            arrayJsonClientes.add(jsonMenuCompra(menuComprado));
        }
        return arrayJsonClientes;
    }

    public MenuCompra menuCompra(Menu menu) {
        MenuCompra menuCompra = new MenuCompra();
        menuCompra.setNombre(menu.getNombre());
        menuCompra.setPrice(menu.getPrice());
        menuCompra.setDescripcion(menu.getDescripcion());
        menuCompra.setMultiplicadorPromocion(menu.getMultiplicadorPromocion());
        menuCompra.setImagen(menu.getImagen());
        menuCompra.setCategoria(menu.getCategoria());
        return menuCompra;
    }

    public JsonObject jsonMenuCompra(MenuCompra menuCompra){
        JsonObject jsonMenuCompra = new JsonObject();
        jsonMenuCompra.addProperty("nombre",menuCompra.getNombre());
        jsonMenuCompra.addProperty("price",menuCompra.getPrice());
        jsonMenuCompra.addProperty("descripcion",menuCompra.getDescripcion());
        jsonMenuCompra.addProperty("multiplicador",menuCompra.getMultiplicadorPromocion());
        jsonMenuCompra.addProperty("imagen",menuCompra.getImagen());
        jsonMenuCompra.addProperty("categoria",menuCompra.getCategoria().toString());
        return jsonMenuCompra;
    }

}
