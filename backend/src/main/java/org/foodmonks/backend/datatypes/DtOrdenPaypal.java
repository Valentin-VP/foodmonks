package org.foodmonks.backend.datatypes;

import java.io.Serializable;

public class DtOrdenPaypal implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ordenId;
	private String linkAprobacion;
	private String linkDevolucion;
	
	
	public DtOrdenPaypal(String ordenId, String linkAprobacion, String linkDevolucion) {
		super();
		this.ordenId = ordenId;
		this.linkAprobacion = linkAprobacion;
		this.linkDevolucion = linkDevolucion;
	}


	public String getOrdenId() {
		return ordenId;
	}


	public void setOrdenId(String ordenId) {
		this.ordenId = ordenId;
	}


	public String getLinkAprobacion() {
		return linkAprobacion;
	}


	public void setLinkAprobacion(String linkAprobacion) {
		this.linkAprobacion = linkAprobacion;
	}


	public String getLinkDevolucion() {
		return linkDevolucion;
	}


	public void setLinkDevolucion(String linkDevolucion) {
		this.linkDevolucion = linkDevolucion;
	}
	
}
