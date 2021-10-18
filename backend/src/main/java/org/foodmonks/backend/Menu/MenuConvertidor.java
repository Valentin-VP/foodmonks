package org.foodmonks.backend.Menu;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuConvertidor {

    public List<DtMenu> connvertirMenu(List<Menu> menus){
        List<DtMenu> dtMenus = new ArrayList<>();
            for (Menu menu : menus){
                System.out.println("------------------------------>" + menu.getNombre());
                dtMenus.add(new DtMenu(menu));
            }
        return dtMenus;
    }

}
