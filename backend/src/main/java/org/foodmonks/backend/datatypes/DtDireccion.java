package org.foodmonks.backend.datatypes;

import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.io.Serializable;

public class DtDireccion implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer numero;
    private String calle;
    private String esquina;
    private String detalles;
    private String latitud;
    private String longitud;

    public DtDireccion(Integer numero, String calle, String esquina, String detalles, String latitud, String longitud) {
        this.numero = numero;
        this.calle = calle;
        this.esquina = esquina;
        this.detalles = detalles;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getEsquina() {
        return esquina;
    }

    public void setEsquina(String esquina) {
        this.esquina = esquina;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }


}
