package org.foodmonks.backend.persistencia;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class DireccionID implements Serializable {

    private static final long serialVersionUID = 1L;
    private String latitud;
    private String longitud;

    public DireccionID() {
        super();
    }

    public DireccionID(String latitud, String longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
