package org.foodmonks.backend.Usuario;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoBloqueadoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoDesbloqueadoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEliminadoException;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

@Service
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;
	private final TemplateEngine templateEngine;
	private final EmailService emailService;
  	private final PasswordEncoder passwordEncoder;
	private final ClienteRepository clienteRepository;
	private final RestauranteRepository restauranteRepository;
	
	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository, TemplateEngine templateEngine, EmailService emailService, PasswordEncoder passwordEncoder, ClienteRepository clienteRepository, RestauranteRepository restauranteRepository) {
		this.usuarioRepository = usuarioRepository;
		this.templateEngine = templateEngine;
		this.emailService = emailService;
    	this.passwordEncoder = passwordEncoder;
		this.clienteRepository = clienteRepository;
		this.restauranteRepository = restauranteRepository;
	}
  
  public void cambiarPassword(String correo, String password) throws UsuarioNoEncontradoException {
       Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No existe el Usuario " + correo);
        }
        usuario.setContrasenia(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    public String generarTokenResetPassword() {
        return UUID.randomUUID().toString();
    }

	public List<Usuario> listarUsuarios(String correo, String tipoUser, String fechaInicio, String fechaFin, String estado, boolean orden) {
		List<Usuario> listaUsuarios = usuarioRepository.findAll();

		//filtros:
		if(orden) {//ordenamiento por calificacion(cliente o restaurante)
			List<Cliente> auxListOrdenCliente = new ArrayList<>();
			List<Restaurante> auxListOrdenRestaurante = new ArrayList<>();
			//necesito ambas listas por como ordeno los usuarios
			if(tipoUser.equals("cliente")) {
				auxListOrdenCliente = clienteRepository.findAllByRolesOrderByCalificacionDesc("ROLE_CLIENTE");
//				for(Usuario user: listaUsuarios) {
//					Cliente cliente = (Cliente) usuarioRepository.findByCorreo((user.getCorreo()));
//					auxListOrdenCliente.add(cliente);
//					//ordenamiento por calificacion global de cliente
//					auxListOrdenCliente.sort(new Comparator<Cliente>() {
//						@Override
//						public int compare(Cliente c1, Cliente c2) {
//							return c1.getCalificacion().compareTo(c2.getCalificacion());//de menor a mayor
//						}
//					});
//				}
				listaUsuarios = new ArrayList<Usuario>(auxListOrdenCliente);
			} else {
				auxListOrdenRestaurante = restauranteRepository.findAllByRolesOrderByCalificacion("ROLE_RESTAURANTE");
//				for(Usuario user: listaUsuarios) {
//					Restaurante restaurante = (Restaurante) usuarioRepository.findByCorreo(user.getCorreo());
//					auxListOrdenRestaurante.add(restaurante);
//					//ordenamiento por calificacion global de restaurante
//					auxListOrdenRestaurante.sort(new Comparator<Restaurante>() {
//						@Override
//						public int compare(Restaurante r1, Restaurante r2) {
//							return r1.getCalificacion().compareTo(r2.getCalificacion());//de menor a mayor
//						}
//					});
//				}
				listaUsuarios = new ArrayList<>(auxListOrdenRestaurante);
			}
		}
		if(!correo.isEmpty()) {//filtro por correo(cliente, restaurante o admin)
			List<Usuario> auxList = new ArrayList<>();
			for(Usuario user: listaUsuarios) {
				if(user.getCorreo().equals(correo)) {
					auxList.add(user);
				}
			}
			listaUsuarios = auxList;
		}
		if(!fechaInicio.isEmpty()) {//filtro por fecha de registro(cliente, restaurante o admin)
			List<Usuario> auxListInicio = new ArrayList<>();
			List<Usuario> auxListFin = new ArrayList<>();
			for(Usuario user: listaUsuarios) {
				if(user.getFechaRegistro().isAfter(LocalDate.parse(fechaInicio))) {
					auxListInicio.add(user);
				}
			}
			listaUsuarios = auxListInicio;
			if(!fechaFin.isEmpty()) {
				for(Usuario user: listaUsuarios) {
					if(user.getFechaRegistro().isBefore(LocalDate.parse(fechaFin))) {
						auxListFin.add(user);
					}
				}
				listaUsuarios = auxListFin;
			}
		}
		if(!tipoUser.isEmpty()) {//filtro por tipo de usuario(cliente o restaurante)
			List<Usuario> auxList = new ArrayList<>();
			for(Usuario user: listaUsuarios) {
				if (tipoUser.equals("cliente")) {//filtro por cliente
					if (user instanceof Cliente) {
						auxList.add(user);
					}
				} else if (tipoUser.equals("restaurante")){//filtro por restaurante
					if (user instanceof Restaurante) {
						auxList.add(user);
					}
				} else {//si es admin
					if (user instanceof Admin) {
						auxList.add(user);
					}
				}
			}
			listaUsuarios = auxList;
		}
		if(!estado.isEmpty()) {//filtro por estado(cliente o restaurante)
			List<Usuario> auxList = new ArrayList<Usuario>();
			for (Usuario user : listaUsuarios) {
				if (estado.equals("BLOQUEADO") || estado.equals("ELIMINADO")) {
					if (user instanceof Cliente) {
						Cliente cliente = (Cliente) usuarioRepository.findByCorreo(user.getCorreo());
						if(cliente.getEstado().equals(EstadoCliente.valueOf(estado))) {
							auxList.add(user);
						}
					} else if(user instanceof Restaurante) {
						Restaurante restaurante = (Restaurante) usuarioRepository.findByCorreo(user.getCorreo());
						if(restaurante.getEstado().equals(EstadoRestaurante.valueOf(estado))) {
							auxList.add(user);
						}
					}
				} else if (estado.equals("DESBLOQUEADO")) {
					if (user instanceof Cliente) {
						Cliente cliente = (Cliente) usuarioRepository.findByCorreo(user.getCorreo());
						if(cliente.getEstado().equals(EstadoCliente.valueOf("ACTIVO"))) {
							auxList.add(user);
						}
					} else if(user instanceof Restaurante) {
						Restaurante restaurante = (Restaurante) usuarioRepository.findByCorreo(user.getCorreo());
						if(restaurante.getEstado().equals(EstadoRestaurante.valueOf("ABIERTO")) || restaurante.getEstado().equals(EstadoRestaurante.valueOf("CERRADO"))) {
							auxList.add(user);
						}
					}
				}
			}
			listaUsuarios = auxList;
		}
		return listaUsuarios;
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
	
	
	public void eliminarUsuario (String correo) throws UsuarioNoEncontradoException, EmailNoEnviadoException, UsuarioNoEliminadoException {
		
		Usuario usuario = usuarioRepository.findByCorreo(correo);
			
				if (usuario instanceof Restaurante) {
					Restaurante restaurante = (Restaurante) usuario;
					if (restaurante.getEstado()!= EstadoRestaurante.BLOQUEADO) {
						throw new UsuarioNoEliminadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
						 restaurante.setEstado(EstadoRestaurante.ELIMINADO);
						 usuarioRepository.save(restaurante);
					}
				} else if (usuario instanceof Cliente) {
					Cliente cliente = (Cliente) usuario;
					if (cliente.getEstado()!= EstadoCliente.BLOQUEADO) {
						throw new UsuarioNoEliminadoException("Usuario "+correo+" debe estar bloqueado" );
					}else {
					     cliente.setEstado(EstadoCliente.ELIMINADO);
					     usuarioRepository.save(cliente);
					}
				} else if (usuario instanceof Admin){
					usuarioRepository.delete(usuario);
				} else
					throw new  UsuarioNoEncontradoException("Usuario "+correo+" no encontrado.");
				
				//ENVIAR NOTIFICACION EMAIL
				 Context context = new Context();
				 context.setVariable("user", usuario.getNombre());
				 context.setVariable("contenido","Su cuenta ha sido eliminada debido a la falta grave de incumplimiento a las normas de FoodMonks. Por mas información, envíe un mail a foodmonksoficial@gmail.com.");
				 String htmlContent = templateEngine.process("bloquear-desbloquear-eliminar", context);
				 try {
					 emailService.enviarMail(usuario.getCorreo(), "Cuenta Eliminada", htmlContent, null);
				 }catch (EmailNoEnviadoException e) {
					 throw new EmailNoEnviadoException("Usuario eliminado, " +e.getMessage());
				 }
	}

	public Usuario ObtenerUsuario (String correo) {
		return usuarioRepository.findByCorreo(correo);
	}

}
