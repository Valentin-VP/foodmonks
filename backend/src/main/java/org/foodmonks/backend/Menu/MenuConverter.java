package org.foodmonks.backend.Menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuConverter {

    public List<JsonObject> listaJsonMenu(List<Menu> menus){
        List<JsonObject> gsonMenus = new ArrayList<>();
            for (Menu menu : menus){
                gsonMenus.add(jsonMenu(menu));
            }
        return gsonMenus;
    }

    public JsonObject jsonMenu(Menu menu) {
        JsonObject jsonMenu = new JsonObject();
        jsonMenu.addProperty("nombre", menu.getNombre());
        jsonMenu.addProperty("id", menu.getId());
        jsonMenu.addProperty("categoria", menu.getCategoria().name());
        jsonMenu.addProperty("multiplicadorPromocion", menu.getMultiplicadorPromocion());
        jsonMenu.addProperty("descripcion", menu.getDescripcion());
        jsonMenu.addProperty("price", menu.getPrice());
        jsonMenu.addProperty("imagen", menu.getImagen());
        return jsonMenu;
    }

}
