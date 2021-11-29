package org.foodmonks.backend.Cliente;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Cliente.Exceptions.ClienteExisteDireccionException;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.MenuConverter;
import org.foodmonks.backend.Menu.MenuRepository;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.MenuCompra.MenuCompraService;
import org.foodmonks.backend.Pedido.PedidoConverter;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.ReclamoService;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    ClienteService clienteService;

    @Mock PasswordEncoder passwordEncoder;
    @Mock ClienteRepository clienteRepository;
    @Mock UsuarioService usuarioService;
    @Mock DireccionService direccionService;
    @Mock ClienteConverter clienteConverter;
    @Mock PedidoService pedidoService;
    @Mock RestauranteService restauranteService;
    @Mock MenuCompraService menuCompraService;
    @Mock MenuService menuService;
    @Mock MenuConverter menuConverter;
    @Mock MenuRepository menuRepository;
    @Mock PedidoConverter pedidoConverter;
    @Mock EmailService emailService;
    @Mock ReclamoService reclamoService;
    @Mock TemplateEngine templateEngine;

    @BeforeEach
    void setUp(){
        clienteService = new ClienteService(clienteRepository, passwordEncoder, usuarioService, direccionService,
                clienteConverter, pedidoService, restauranteService, menuCompraService,
                menuService,menuConverter, menuRepository, pedidoConverter,
                emailService, reclamoService, templateEngine);
    }

    @Test
    void crearCliente() throws DireccionNumeroException, UsuarioExisteException, ClienteDireccionException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        when(usuarioService.ObtenerUsuario(anyString())).thenReturn(null);
        when(direccionService.crearDireccion(any(JsonObject.class))).thenReturn(dir1);
        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);

        clienteService.crearCliente("dummy", "dummy", "dummy", "dummy",
                LocalDate.now(), 5.0f, new JsonObject(), EstadoCliente.ACTIVO);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getCorreo()).isEqualTo("dummy");
//        JsonObject jsonDireccion = new JsonObject();
//        jsonDireccion.addProperty("numero", "0");
//        jsonDireccion.addProperty("calle", "dummy");
//        jsonDireccion.addProperty("esquina", "dummy");
//        jsonDireccion.addProperty("detalles", "dummy");
//        jsonDireccion.addProperty("latitud", "dummy");
//        jsonDireccion.addProperty("longitud", "dummy");

        // ------------------------------------------------
        when(usuarioService.ObtenerUsuario(anyString())).thenReturn(cliente1);
        assertThatThrownBy(()->clienteService.crearCliente("dummy", "dummy", "dummy", "dummy",
                LocalDate.now(), 5.0f, new JsonObject(), EstadoCliente.ACTIVO)).isInstanceOf(UsuarioExisteException.class)
                .hasMessageContaining("Ya existe un Usuario registrado con el correo dummy");
        // ------------------------------------------------
        when(usuarioService.ObtenerUsuario(anyString())).thenReturn(null);
        when(direccionService.crearDireccion(any(JsonObject.class))).thenReturn(null);
        assertThatThrownBy(()->clienteService.crearCliente("dummy", "dummy", "dummy", "dummy",
                LocalDate.now(), 5.0f, new JsonObject(), EstadoCliente.ACTIVO)).isInstanceOf(ClienteDireccionException.class)
                .hasMessageContaining("Debe ingresar una dirección");
    }

    @Test
    void agregarDireccionCliente() throws ClienteExisteDireccionException, DireccionNumeroException, ClienteNoEncontradoException, ClienteDireccionException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "0.0", "0.0");
        dir1.setId(1L);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);

        JsonObject jsonDireccion = new JsonObject();
        jsonDireccion.addProperty("numero", "0");
        jsonDireccion.addProperty("calle", "dummy");
        jsonDireccion.addProperty("esquina", "dummy");
        jsonDireccion.addProperty("detalles", "dummy");
        jsonDireccion.addProperty("latitud", "5.0");
        jsonDireccion.addProperty("longitud", "5.0");

        JsonObject expectedResult = new JsonObject();
        expectedResult.addProperty("id", 1L);

        JsonObject result = clienteService.agregarDireccionCliente("dummy", jsonDireccion);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getDirecciones().size()).isEqualTo(1);

    }
}