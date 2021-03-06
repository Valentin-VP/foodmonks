package org.foodmonks.backend.Menu;

import com.google.gson.JsonArray;
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

    public JsonArray arrayJsonMenu (List<Menu> menus){
        JsonArray arrayJsonMenu = new JsonArray();
        for (Menu menu : menus){
            arrayJsonMenu.add(jsonMenu(menu));
        }
        return arrayJsonMenu;
    }

    public JsonObject jsonMenu(Menu menu) {
        JsonObject jsonMenu = new JsonObject();
        jsonMenu.addProperty("id", menu.getId());
        jsonMenu.addProperty("nombre", menu.getNombre());
        jsonMenu.addProperty("price", menu.getPrice());
        jsonMenu.addProperty("descripcion", menu.getDescripcion());
        jsonMenu.addProperty("visible", menu.getVisible());
        jsonMenu.addProperty("multiplicadorPromocion", menu.getMultiplicadorPromocion());
        jsonMenu.addProperty("imagen", menu.getImagen());
        jsonMenu.addProperty("categoria", menu.getCategoria().name());
        jsonMenu.addProperty("restaurante", menu.getRestaurante().getCorreo());
        return jsonMenu;
    }

}
