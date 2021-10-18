package org.foodmonks.backend.Usuario;

import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;
	
	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	public boolean bloquearUsuario (String correo) {
		try {
		
			Usuario usuario = usuarioRepository.findByCorreo(correo);
			if (usuario instanceof Restaurante) {
				Restaurante restaurante = (Restaurante) usuario;
				 restaurante.setEstado(EstadoRestaurante.BLOQUEADO);
				 ///FALTA ENVIAR MAIL
			} else {
				Cliente cliente = (Cliente) usuario;
			     cliente.setEstado(EstadoCliente.BLOQUEADO);
			     //FALTA ENVIAR MAIL
				
			}
			
			return true;
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean esUsuarioBloqueado (String correo) {
		
			Usuario usuario = usuarioRepository.findByCorreo(correo);
			if (usuario instanceof Restaurante) {
				Restaurante restaurante = (Restaurante) usuario;
				 return restaurante.getEstado()==EstadoRestaurante.BLOQUEADO;
			} else {
				Cliente cliente = (Cliente) usuario;
				return cliente.getEstado()==EstadoCliente.BLOQUEADO;
				
			}
		
	}
	
	public boolean esUsuarioEliminado (String correo) {
		
		Usuario usuario = usuarioRepository.findByCorreo(correo);
		if (usuario instanceof Restaurante) {
			Restaurante restaurante = (Restaurante) usuario;
			 return restaurante.getEstado()==EstadoRestaurante.ELIMINADO;
		} else {
			Cliente cliente = (Cliente) usuario;
			return cliente.getEstado()==EstadoCliente.ELIMINADO;
			
		}
	
}

}
