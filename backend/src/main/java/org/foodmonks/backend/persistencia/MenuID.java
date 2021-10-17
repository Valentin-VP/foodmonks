package org.foodmonks.backend.persistencia;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MenuID implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String restaurante;

    public MenuID() {
        super();
    }

    public MenuID(Long id, String restaurante) {
        this.id = id;
        this.restaurante = restaurante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }
}
