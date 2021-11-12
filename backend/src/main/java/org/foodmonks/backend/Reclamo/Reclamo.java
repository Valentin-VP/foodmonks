package org.foodmonks.backend.Reclamo;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.persistencia.ReclamoID;

@Getter
@Setter
@Entity
@IdClass(ReclamoID.class)
public class Reclamo {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
	private String razon;
	private String comentario;
	private LocalDate fecha;
	@Id
	@OneToOne
	@JoinColumn(
			insertable=false,
			updatable=false
	)
	private Pedido pedido;

	public Reclamo() {
		// TODO Auto-generated constructor stub
	}

	public Reclamo(String razon, String comentario, LocalDate fecha, Pedido pedido) {
		super();
		this.razon = razon;
		this.comentario = comentario;
		this.fecha = fecha;
		this.pedido = pedido;
	}

}
