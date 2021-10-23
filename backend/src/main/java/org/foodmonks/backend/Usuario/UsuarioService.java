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
	
	public void bloquearUsuario (String correo) throws UsuarioNoEncontradoException, UsuarioNoBloqueadoException {
		
			Usuario usuario = usuarioRepository.findByCorreo(correo);
				
					if (usuario instanceof Restaurante) {
						Restaurante restaurante = (Restaurante) usuario;
						if (restaurante.getEstado()== EstadoRestaurante.BLOQUEADO || restaurante.getEstado()== EstadoRestaurante.ELIMINADO) {
							throw new UsuarioNoBloqueadoException("Usuario "+correo+" no pudo ser bloqueado" );
						}else {
							 restaurante.setEstado(EstadoRestaurante.BLOQUEADO);
							 usuarioRepository.save(restaurante);
							 ///FALTA ENVIAR MAIL
						}
					} else if (usuario instanceof Cliente) {
						Cliente cliente = (Cliente) usuario;
						if (cliente.getEstado()== EstadoCliente.BLOQUEADO || cliente.getEstado()== EstadoCliente.ELIMINADO) {
							throw new UsuarioNoBloqueadoException("Usuario "+correo+" no pudo ser bloqueado" );
						}else {
						     cliente.setEstado(EstadoCliente.BLOQUEADO);
						     usuarioRepository.save(cliente);
						     //FALTA ENVIAR MAIL
						}
					} else 
						throw new  UsuarioNoEncontradoException("Usuario "+correo+" no encontrado.");		
	}
	
	public void desbloquearUsuario (String correo) throws UsuarioNoEncontradoException, UsuarioNoDesbloqueadoException {
		
		Usuario usuario = usuarioRepository.findByCorreo(correo);
			
				if (usuario instanceof Restaurante) {
					Restaurante restaurante = (Restaurante) usuario;
					if (restaurante.getEstado()!= EstadoRestaurante.BLOQUEADO) {
						throw new UsuarioNoDesbloqueadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
						 restaurante.setEstado(EstadoRestaurante.CERRADO);
						 usuarioRepository.save(restaurante);
						 ///FALTA ENVIAR MAIL
					}
				} else if (usuario instanceof Cliente) {
					Cliente cliente = (Cliente) usuario;
					if (cliente.getEstado()!= EstadoCliente.BLOQUEADO) {
						throw new UsuarioNoDesbloqueadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
					     cliente.setEstado(EstadoCliente.ACTIVO);
					     usuarioRepository.save(cliente);
					     //FALTA ENVIAR MAIL
					}
				} else 
					throw new  UsuarioNoEncontradoException("Usuario "+correo+" no encontrado.");		
	}
	

}
