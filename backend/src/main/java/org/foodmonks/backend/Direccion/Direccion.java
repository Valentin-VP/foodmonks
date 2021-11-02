package org.foodmonks.backend.Direccion;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.persistencia.DireccionID;
import javax.persistence.*;


@Getter
@Setter
@Entity
@IdClass(DireccionID.class)
public class Direccion{

    private Integer numero;
    private String calle;
    private String esquina;
    private String detalles;
    @Id
    private String latitud;
    @Id
    private String longitud;

    public Direccion() {
    }

    public Direccion(Integer numero, String calle, String esquina, String detalles, String latitud, String longitud) {
        this.numero = numero;
        this.calle = calle;
        this.esquina = esquina;
        this.detalles = detalles;
        this.latitud = latitud;
        this.longitud = longitud;
    }



}
