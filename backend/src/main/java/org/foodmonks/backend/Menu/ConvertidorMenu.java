package org.foodmonks.backend.Menu;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConvertidorMenu {

    public List<DtMenu> connvertirMenu(List<Menu> menus){
        List<DtMenu> dtMenus = new ArrayList<>();
            for (Menu menu : menus){
                dtMenus.add(new DtMenu(menu));
            }
        return dtMenus;
    }

}
