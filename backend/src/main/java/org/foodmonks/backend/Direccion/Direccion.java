package org.foodmonks.backend.Direccion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.persistencia.DireccionID;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToMany
    private List<Cliente> cliente = new ArrayList<>();
    @OneToOne(mappedBy="direccion")
    private Restaurante restaurante;
    @OneToMany(mappedBy="direccion")
    private List<Pedido> pedido = new ArrayList<>();

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
