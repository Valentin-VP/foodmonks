package org.foodmonks.backend.MenuCompra;

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

}
