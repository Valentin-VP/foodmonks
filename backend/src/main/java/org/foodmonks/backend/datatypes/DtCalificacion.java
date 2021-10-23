package org.foodmonks.backend.datatypes;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DtCalificacion  implements Serializable {
	private static final long serialVersionUID = 1L;
	private Float puntaje;
	private String comentario;
	
	public DtCalificacion()  {
		
	}

	public DtCalificacion(Float puntaje, String comentario) {
		super();
		this.puntaje = puntaje;
		this.comentario = comentario;
	}

}
