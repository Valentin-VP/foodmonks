package org.foodmonks.backend.Cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void crearCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public List<Cliente> listarCliente(){
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(Long id) {
        Optional<Cliente> aux = clienteRepository.findById(id);
        if (aux.isEmpty()) {
            System.out.println("No existe ese cliente");
            return null;
        }else{
            System.out.println(aux.get().getNombre());
            return aux.get();
        }
    }

    public void eliminarCliente(Long id) {
        Optional<Cliente> aux = clienteRepository.findById(id);
        if (aux.isEmpty())
            System.out.println("No existe ese cliente");
        else {
            System.out.println(aux.get().getNombre());
            clienteRepository.delete(aux.get());
        }
    }

    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }
}
