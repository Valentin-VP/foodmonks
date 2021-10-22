package org.foodmonks.backend.datatypes;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DtOrdenPaypal implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ordenId;
	private String linkAprobacion;
	private String linkDevolucion;

	public DtOrdenPaypal() {
	}

	public DtOrdenPaypal(String ordenId, String linkAprobacion, String linkDevolucion) {
		super();
		this.ordenId = ordenId;
		this.linkAprobacion = linkAprobacion;
		this.linkDevolucion = linkDevolucion;
	}
}
