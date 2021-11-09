package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionRepository;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuConvertidor;
import org.foodmonks.backend.Menu.MenuRepository;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.persistencia.DireccionID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;
    private final MenuConvertidor menuConvertidor;
    private final MenuService menuService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder , UsuarioRepository usuarioRepository , DireccionRepository direccionRepository, MenuRepository menuRepository,RestauranteRepository restauranteRepository, MenuConvertidor menuConvertidor, MenuService menuService) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder; this.usuarioRepository = usuarioRepository; this.direccionRepository = direccionRepository; this.menuRepository = menuRepository; this.restauranteRepository = restauranteRepository; this.menuConvertidor = menuConvertidor; this.menuService = menuService;
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

    public List<JsonObject> listarMenus (String correo, String categoria, Float precioInicial, Float precioFinal){

        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        List<Menu> menus = menuRepository.findMenusByRestaurante(restaurante);

        if(!categoria.isEmpty()){

            CategoriaMenu categoriaMenu = CategoriaMenu.valueOf(categoria);
            return menuConvertidor.listaJsonMenu(menuRepository.findMenuByRestauranteAndCategoria(restaurante,categoriaMenu));
        }

        return menuConvertidor.listaJsonMenu(menus);
    }

}
