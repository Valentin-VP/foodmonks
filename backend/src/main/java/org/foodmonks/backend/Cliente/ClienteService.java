package org.foodmonks.backend.Cliente;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionRepository;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ClienteService {

    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final PedidoService pedidoService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, DireccionRepository direccionRepository, PedidoService pedidoService) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder; this.usuarioRepository = usuarioRepository; this.direccionRepository = direccionRepository;
        this.pedidoService = pedidoService;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro,
                             Float calificacion, Direccion direccion, EstadoCliente activo) throws ClienteDireccionException, UsuarioExisteException {
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una direccion");
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
        Cliente aux = clienteRepository.findByCorreo(correo);
        return aux;
    }

    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }


    public void modificarEstadoCliente(String correo, EstadoCliente estado){
        Cliente clienteAux = clienteRepository.findByCorreo(correo);
        clienteAux.setEstado(estado);
        clienteRepository.save(clienteAux);
    }

    public EstadoCliente clienteEstado(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }
        return cliente.getEstado();
    }

    public JsonObject listarPedidosRealizados(String correo, String estadoPedido, String nombreMenu, String nombreRestaurante, String medioPago, String orden, String fecha, String total, String page, String size) throws ClienteNoEncontradoException {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente==null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }
        String[] _total = (total!=null && total.contains(",")) ? total.split(",") : null;
        String[] _fecha = (fecha!=null && fecha.contains(",")) ? fecha.split(",") : null;
        Float[] totalFinal = new Float[2];
        LocalDateTime[] fechaFinal = new LocalDateTime[2];

        MedioPago pago = null;
        EstadoPedido estado = null;
        int pageFinal = 0;
        int sizeFinal = 10;
        if (estadoPedido!= null && !estadoPedido.equals("")) {
            try {
                estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                estado = null;
            }
        }
        if (nombreRestaurante!= null && nombreRestaurante.equals("")) {
            nombreRestaurante = null;
        }
        if (nombreMenu!= null && nombreMenu.equals("")) {
            nombreMenu = null;
        }
        if (medioPago!= null && !medioPago.equals("")) {
            try{
                pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                pago = null;
            }
        }

        if (_total != null && _total[0] != null && _total[1] != null){
            try{
                totalFinal[0] = Math.abs(Float.valueOf(_total[0]));
                totalFinal[1] = Math.abs(Float.valueOf(_total[1]));
            }catch(NumberFormatException e){
                totalFinal = null;
            }
        }else
            totalFinal = null;

        if (_fecha != null && _fecha[0] != null && _fecha[1] != null){
            try{
                fechaFinal[0] = LocalDateTime.of(LocalDate.parse(_fecha[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
                fechaFinal[1] = LocalDateTime.of(LocalDate.parse(_fecha[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
            }catch(DateTimeException e){
                fechaFinal = null;
            }
        }else
            fechaFinal = null;

        try{
            pageFinal = Integer.parseInt(page);
            sizeFinal = Integer.parseInt(size);
        }catch(NumberFormatException e){
            e.printStackTrace();
            pageFinal = 0;
            sizeFinal = 5;
        }
        return pedidoService.listaPedidosRealizados(cliente, estado, nombreMenu, nombreRestaurante, pago, orden, fechaFinal, totalFinal, pageFinal, sizeFinal);
    }
}
