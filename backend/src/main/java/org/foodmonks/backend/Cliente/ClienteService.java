package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.Cliente.Exceptions.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionRepository;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.persistencia.DireccionID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder , UsuarioRepository usuarioRepository , DireccionRepository direccionRepository) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder; this.usuarioRepository = usuarioRepository; this.direccionRepository = direccionRepository;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro,
                             Float calificacion, Direccion direccion, EstadoCliente activo) throws ClienteDireccionException, UsuarioExisteException {
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una dirección");
        }
        List<Direccion> direcciones = new ArrayList<>();
        direcciones.add(direccion);
        Cliente cliente = new Cliente(nombre,apellido,correo,passwordEncoder.encode(password),fechaRegistro,calificacion,direcciones,activo,"",null);
//        List<Cliente> clientes = direccion.getCliente();
//        clientes.add(cliente);
//        direccion.setCliente(clientes);
        clienteRepository.save(cliente);
        direccionRepository.save(direccion);
        System.out.println("direccion " + clienteRepository.findByCorreo(correo).getDirecciones().get(0).getCalle());
    }


    public List<Cliente> listarCliente(){
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public void modificarEstadoCliente(String correo, EstadoCliente estado) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(correo);
        cliente.setEstado(estado);
        clienteRepository.save(cliente);
    }

    public EstadoCliente clienteEstado(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(correo);
        return cliente.getEstado();
    }

    public void agregarDireccionCliente(String correo, Direccion direccion) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteExisteDireccionException {
        Cliente cliente = obtenerCliente(correo);
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una dirección");
        }
        List<Direccion> direcciones = cliente.getDirecciones();
        if (direcciones.contains(direccion)) {
            throw new ClienteExisteDireccionException("Esa dirección ya esta registrada para el Cliente " + correo);
        }
        List<Cliente> clientesDireccion = direccion.getCliente();
        clientesDireccion.add(cliente);
        direccion.setCliente(clientesDireccion);
        direcciones.add(direccion);
        cliente.setDirecciones(direcciones);
        clienteRepository.save(cliente);
        direccionRepository.save(direccion);

    }

    public void eliminarDireccionCliente(String correo, Direccion direccion) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteUnicaDireccionException, ClienteNoExisteDireccionException {
        Cliente cliente = obtenerCliente(correo);
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una dirección");
        }
        List<Direccion> direcciones = cliente.getDirecciones();
        if (direcciones.size() < 2){
            throw new ClienteUnicaDireccionException("No puede eliminar la única dirección registrada del Cliente " + correo);
        }
        if (!direcciones.contains(direccion)) {
            throw new ClienteNoExisteDireccionException("Esa dirección no esta registrada para el Cliente " + correo);
        }
        List<Cliente> clientesDireccion = direccion.getCliente();
        clientesDireccion.remove(cliente);
        direccion.setCliente(clientesDireccion);
        direcciones.remove(direccion);
        cliente.setDirecciones(direcciones);
        clienteRepository.save(cliente);
        direccionRepository.save(direccion);

    }

    private Cliente obtenerCliente(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }
        return cliente;
    }

}
