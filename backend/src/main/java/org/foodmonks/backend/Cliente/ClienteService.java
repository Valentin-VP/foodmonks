package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro, Float calificacion, Direccion direccion, EstadoCliente activo) {
        //clienteRepository.save(cliente);

    }

    public List<Cliente> listarCliente(){
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(String correo) {
        Cliente aux = clienteRepository.findByCorreo(correo);
        if (aux == null) {
            return null;
        }else{
            return aux;
        }
    }

    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

}
