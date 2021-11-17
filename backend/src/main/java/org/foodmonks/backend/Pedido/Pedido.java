package org.foodmonks.backend.Pedido;


import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.DtCalificacion;
import org.foodmonks.backend.datatypes.DtOrdenPaypal;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Pedido  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private EstadoPedido estado;
    private DtCalificacion calificacionCliente;
    private DtCalificacion calificacionRestaurante;
    private LocalDateTime fechaHoraProcesado;
    private Float total;
    @Enumerated(value = EnumType.STRING)
    private MedioPago medioPago;
    private LocalDateTime fechaHoraEntrega;
    @ManyToOne
    private Direccion direccion;
    private DtOrdenPaypal ordenPaypal;
    @ManyToOne
    private  Cliente cliente;
    @ManyToOne
    private Restaurante restaurante;
    @OneToMany//(mappedBy="pedido", cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    private List<MenuCompra> menusCompra = new ArrayList<>();
    @OneToOne //(mappedBy="pedido", cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    private Reclamo reclamo;

    public Pedido(EstadoPedido estado, LocalDateTime fechaHoraProcesado, Float total,
                  MedioPago medioPago, LocalDateTime fechaHoraEntrega, Direccion direccion, DtOrdenPaypal ordenPaypal) {
        this.estado = estado;
        this.fechaHoraProcesado = fechaHoraProcesado;
        this.total = total;
        this.medioPago = medioPago;
        this.fechaHoraEntrega = fechaHoraEntrega;
        this.direccion = direccion;
        this.ordenPaypal = ordenPaypal;
    }

    public Pedido () {}


    public Pedido(EstadoPedido estado, Float total, MedioPago medioPago) {
        this.estado = estado;
        this.total = total;
        this.medioPago = medioPago;
    }

}

