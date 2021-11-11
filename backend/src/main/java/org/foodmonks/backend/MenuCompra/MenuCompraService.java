package org.foodmonks.backend.MenuCompra;

import org.foodmonks.backend.Menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuCompraService {

    private final MenuCompraRepository menuCompraRepository;
    private final MenuCompraConverter menuCompraConverter;

    @Autowired
    public MenuCompraService (MenuCompraRepository menuCompraRepository, MenuCompraConverter menuCompraConverter){
        this.menuCompraRepository = menuCompraRepository; this.menuCompraConverter = menuCompraConverter;
    }

    public MenuCompra crearMenuCompraMenu (Menu menu, int cantidad){
        MenuCompra menuCompra = menuCompraConverter.menuCompra(menu);
        menuCompra.setCantidad(cantidad);
        menuCompraRepository.save(menuCompra);
        return menuCompra;
    }

}
