package org.foodmonks.backend.persistencia;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
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

}
