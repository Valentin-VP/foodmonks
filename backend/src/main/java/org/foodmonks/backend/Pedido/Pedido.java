package org.foodmonks.backend.Pedido;


import javax.persistence.*;

import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.DtCalificacion;
import org.foodmonks.backend.datatypes.DtOrdenPaypal;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.boot.context.properties.bind.Name;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido  {

 	@Id
 	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Enumerated(value = EnumType.STRING)
    private EstadoPedido estado;
    private DtCalificacion calificacionCliente;
    private DtCalificacion calificacionRestaurante;
    private LocalDate fechaHoraProcesado;
    private Float total;
    @Enumerated(value = EnumType.STRING)
    private MedioPago medioPago;
    private LocalDate fechaHoraEntrega;
	@ManyToOne
    private Direccion direccion;
    private DtOrdenPaypal ordenPaypal;
    @ManyToOne
	private  Cliente cliente;
    @ManyToOne
	private  Restaurante restaurante;
    @ManyToMany(cascade= {CascadeType.PERSIST,CascadeType.MERGE})
	private List<Menu> menus = new ArrayList<>();
    @OneToOne(mappedBy="pedido", cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    private Reclamo reclamo;
    
    public Pedido () {
    	
    }
    
    public Pedido(String nombre, EstadoPedido estado, LocalDate fechaHoraProcesado, Float total,
				  MedioPago medioPago, LocalDate fechaHoraEntrega, Direccion direccion, DtOrdenPaypal ordenPaypal) {
		super();
		this.nombre = nombre;
		this.estado = estado;
		this.fechaHoraProcesado = fechaHoraProcesado;
		this.total = total;
		this.medioPago = medioPago;
		this.fechaHoraEntrega = fechaHoraEntrega;
		this.direccion = direccion;
		this.ordenPaypal = ordenPaypal;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public EstadoPedido getEstado() {
		return estado;
	}

	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}

	public LocalDate getFechaHoraProcesado() {
		return fechaHoraProcesado;
	}

	public void setFechaHoraProcesado(LocalDate fechaHoraProcesado) {
		this.fechaHoraProcesado = fechaHoraProcesado;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public MedioPago getMedioPago() {
		return medioPago;
	}

	public void setMedioPago(MedioPago medioPago) {
		this.medioPago = medioPago;
	}

	public LocalDate getFechaHoraEntrega() {
		return fechaHoraEntrega;
	}

	public void setFechaHoraEntrega(LocalDate fechaHoraEntrega) {
		this.fechaHoraEntrega = fechaHoraEntrega;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	public DtOrdenPaypal getOrdenPaypal() {
		return ordenPaypal;
	}

	public void setOrdenPaypal(DtOrdenPaypal ordenPaypal) {
		this.ordenPaypal = ordenPaypal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public Reclamo getReclamo() {
		return reclamo;
	}

	public void setReclamo(Reclamo reclamo) {
		this.reclamo = reclamo;
	}
	
	public DtCalificacion getCalificacionCliente() {
		return calificacionCliente;
	}

	public void setCalificacionCliente(DtCalificacion calificacionCliente) {
		this.calificacionCliente = calificacionCliente;
	}

	public DtCalificacion getCalificacionRestaurante() {
		return calificacionRestaurante;
	}

	public void setCalificacionRestaurante(DtCalificacion calificacionRestaurante) {
		this.calificacionRestaurante = calificacionRestaurante;
	}

		//PARA LA BIDIRECCION
		public void agregarReclamo(Reclamo reclamo) {
			reclamo.setPedido(this);
			this.reclamo=reclamo;
		}
		public void eliminarReclamo() {
			if(reclamo!=null) {
				reclamo.setPedido(null);
				this.reclamo=null;
			}
		}
	
	
	
    
    

   

    
}
