package org.foodmonks.backend.MenuCompra;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.persistencia.MenuID;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class MenuCompra {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String nombre;
    private Float price;
    private String descripcion;
    private Float multiplicadorPromocion;
    private String imagen;
    @Enumerated(value = EnumType.STRING)
    private CategoriaMenu categoria;

    public MenuCompra() {
    }

    public MenuCompra(String nombre, Float price, String descripcion, Float multiplicadorPromocion, String imagen, CategoriaMenu categoria) {
        this.nombre = nombre;
        this.price = price;
        this.descripcion = descripcion;
        this.multiplicadorPromocion = multiplicadorPromocion;
        this.imagen = imagen;
        this.categoria = categoria;
    }


}
