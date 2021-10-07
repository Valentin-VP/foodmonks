package org.foodmonks.backend.Reclamo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.foodmonks.backend.Pedido.Pedido;

@Entity
public class Reclamo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String razon;
	private String comentario;
	private LocalDate  fecha;
	@OneToOne
	private Pedido pedido;

	public Reclamo() {
		// TODO Auto-generated constructor stub
	}

	public Reclamo(String razon, String comentario, LocalDate fecha) {
		super();
		this.razon = razon;
		this.comentario = comentario;
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

}
