package org.foodmonks.backend.Direccion;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class Direccion{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Integer numero;
    private String calle;
    private String esquina;
    private String detalles;
    private String latitud;
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
