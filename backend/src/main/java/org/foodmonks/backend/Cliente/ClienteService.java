package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.Cliente.Exceptions.ClienteExisteException;
import org.foodmonks.backend.Cliente.Exceptions.DireccionVaciaException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final PasswordEncoder passwordEncoder;

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro,
                             Float calificacion, Direccion direccion, EstadoCliente activo) throws ClienteExisteException, DireccionVaciaException {
        if (clienteRepository.findByCorreo(correo) != null) {
            throw new ClienteExisteException("Ya existe un Cliente registrado con el correo " + correo);
        }
        if (direccion == null){
            throw new DireccionVaciaException("Debe ingresar una direccion");
        }
        List<Direccion> direcciones = new ArrayList<>();
        direcciones.add(direccion);
        Cliente cliente = new Cliente(nombre,apellido,correo,passwordEncoder.encode(password),fechaRegistro,calificacion,direcciones,activo,"",null);
        clienteRepository.save(cliente);
    }

    public List<Cliente> listarCliente(){
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(String correo) {
        Cliente aux = clienteRepository.findByCorreo(correo);
        return aux;
    }

    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

}
