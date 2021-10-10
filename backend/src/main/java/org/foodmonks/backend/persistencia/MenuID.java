package org.foodmonks.backend.persistencia;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MenuID implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long restaurante;

    public MenuID() {
    }

    public MenuID(Long id, Long restaurante) {
        this.id = id;
        this.restaurante = restaurante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Long restaurante) {
        this.restaurante = restaurante;
    }
}
