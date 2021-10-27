package org.foodmonks.backend.Usuario;

import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;
	private final TemplateEngine templateEngine;
	private final EmailService emailService;
	
	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository, TemplateEngine templateEngine, EmailService emailService) {
		this.usuarioRepository = usuarioRepository;
		this.templateEngine = templateEngine;
		this.emailService = emailService;
	}
	
	public void bloquearUsuario (String correo) throws UsuarioNoEncontradoException, UsuarioNoBloqueadoException, EmailNoEnviadoException {
		
			Usuario usuario = usuarioRepository.findByCorreo(correo);
				
					if (usuario instanceof Restaurante) {
						Restaurante restaurante = (Restaurante) usuario;
						if (restaurante.getEstado()== EstadoRestaurante.BLOQUEADO || restaurante.getEstado()== EstadoRestaurante.ELIMINADO) {
							throw new UsuarioNoBloqueadoException("Usuario "+correo+" no pudo ser bloqueado" );
						}else {
							 restaurante.setEstado(EstadoRestaurante.BLOQUEADO);
							 usuarioRepository.save(restaurante);
							 
						}
					} else if (usuario instanceof Cliente) {
						Cliente cliente = (Cliente) usuario;
						if (cliente.getEstado()== EstadoCliente.BLOQUEADO || cliente.getEstado()== EstadoCliente.ELIMINADO) {
							throw new UsuarioNoBloqueadoException("Usuario "+correo+" no pudo ser bloqueado" );
						}else {
						     cliente.setEstado(EstadoCliente.BLOQUEADO);
						     usuarioRepository.save(cliente);
						  
						}
					} else 
						throw new  UsuarioNoEncontradoException("Usuario "+correo+" no encontrado.");
					
					//ENVIAR NOTIFICACION EMAIL
					 Context context = new Context();
					 context.setVariable("user", usuario.getNombre());
					 context.setVariable("contenido","Su cuenta ha sido bloqueada por incumplir las normas de FoodMonks. Si entiende que es un error, envíe un mail a foodmonksoficial@gmail.com.");
					 String htmlContent = templateEngine.process("bloquear-desbloquear-eliminar", context);
					 try {
						 emailService.enviarMail(usuario.getCorreo(), "Cuenta Bloqueada", htmlContent, null);
					 }catch (EmailNoEnviadoException e) {
						 throw new EmailNoEnviadoException("Usuario bloqueado, " +e.getMessage());
					 }
	}
	
	public void desbloquearUsuario (String correo) throws UsuarioNoEncontradoException, UsuarioNoDesbloqueadoException, EmailNoEnviadoException {
		
		Usuario usuario = usuarioRepository.findByCorreo(correo);
			
				if (usuario instanceof Restaurante) {
					Restaurante restaurante = (Restaurante) usuario;
					if (restaurante.getEstado()!= EstadoRestaurante.BLOQUEADO) {
						throw new UsuarioNoDesbloqueadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
						 restaurante.setEstado(EstadoRestaurante.CERRADO);
						 usuarioRepository.save(restaurante);
					}
				} else if (usuario instanceof Cliente) {
					Cliente cliente = (Cliente) usuario;
					if (cliente.getEstado()!= EstadoCliente.BLOQUEADO) {
						throw new UsuarioNoDesbloqueadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
					     cliente.setEstado(EstadoCliente.ACTIVO);
					     usuarioRepository.save(cliente);
					}
				} else 
					throw new  UsuarioNoEncontradoException("Usuario "+correo+" no encontrado.");
				
				//ENVIAR NOTIFICACION EMAIL
				 Context context = new Context();
				 context.setVariable("user", usuario.getNombre());
				 context.setVariable("contenido","Su cuenta ha sido desbloqueada, por favor verifique las normas de FoodMonks para evitar inconvenientes a futuro. Cualquier consulta, envíe un mail a foodmonksoficial@gmail.com.");
				 String htmlContent = templateEngine.process("bloquear-desbloquear-eliminar", context);
				 try {
					 emailService.enviarMail(usuario.getCorreo(), "Cuenta Desbloqueada", htmlContent, null);
				 }catch (EmailNoEnviadoException e) {
					 throw new EmailNoEnviadoException("Usuario desbloqueado, " +e.getMessage());
				 }
	}
	

}
