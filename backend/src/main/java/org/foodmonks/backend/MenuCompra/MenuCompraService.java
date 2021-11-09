package org.foodmonks.backend.MenuCompra;

import com.google.gson.JsonObject;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuCompraService {

    private final MenuCompraRepository menuCompraRepository;

    @Autowired
    public MenuCompraService (MenuCompraRepository menuCompraRepository){
        this.menuCompraRepository = menuCompraRepository;
    }

    public MenuCompra crearMenuCompra (JsonObject jsonMenuCompra){
        MenuCompra menuCompra = new MenuCompra(
                jsonMenuCompra.get("nombre").getAsString(),
                jsonMenuCompra.get("price").getAsFloat(),
                jsonMenuCompra.get("descripcion").getAsString(),
                jsonMenuCompra.get("multiplicador").getAsFloat(),
                jsonMenuCompra.get("imagen").getAsString(),
                CategoriaMenu.valueOf(jsonMenuCompra.get("categoria").getAsString()));
        menuCompraRepository.save(menuCompra);
        return menuCompra;
    }

}
