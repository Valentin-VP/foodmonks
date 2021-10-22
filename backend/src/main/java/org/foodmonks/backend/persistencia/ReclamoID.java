package org.foodmonks.backend.persistencia;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class ReclamoID implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long pedido;

    public ReclamoID() {
        super();
    }

    public ReclamoID(Long id, Long pedido) {
        this.id = id;
        this.pedido = pedido;
    }

}
