package org.foodmonks.backend.datatypes;

import java.io.Serializable;

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

	public Float getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	

}
