package org.foodmonks.backend.Cliente;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuConverter;
import org.foodmonks.backend.Menu.MenuRepository;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.MenuCompra.MenuCompraService;
import org.foodmonks.backend.Pedido.PedidoConverter;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.ReclamoService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    @Spy
    MenuConverter menuConverter;
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
        Direccion dir2 = new Direccion(1234, "calle", "esquina", "detalles", "1.0", "1.0");
        dir1.setId(1L);
        dir2.setId(2L);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                null, EstadoCliente.ACTIVO, null, null);
        ArrayList<Direccion> direccionArrayList = new ArrayList<>();
        direccionArrayList.add(dir1);
        cliente1.setDirecciones(direccionArrayList);

        JsonObject jsonDireccion = new JsonObject();
        jsonDireccion.addProperty("numero", "0");
        jsonDireccion.addProperty("calle", "dummy");
        jsonDireccion.addProperty("esquina", "dummy");
        jsonDireccion.addProperty("detalles", "dummy");
        jsonDireccion.addProperty("latitud", "5.0");
        jsonDireccion.addProperty("longitud", "5.0");

        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);
        when(direccionService.crearDireccion(any(JsonObject.class))).thenReturn(dir2);

        JsonObject expectedResult = new JsonObject();
        expectedResult.addProperty("id", 2L);

        JsonObject result = clienteService.agregarDireccionCliente("dummy", jsonDireccion);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getDirecciones().size()).isEqualTo(2);
        assertThat(result).isEqualTo(expectedResult);
        // -------------------------------
        jsonDireccion.addProperty("latitud", "");
        jsonDireccion.addProperty("longitud", "");
        assertThatThrownBy(()->clienteService.agregarDireccionCliente("dummy", jsonDireccion))
                .isInstanceOf(ClienteDireccionException.class)
                .hasMessageContaining("Debe ingresar una dirección");
        // -------------------------------
        jsonDireccion.addProperty("latitud", "5.0");
        jsonDireccion.addProperty("longitud", "5.0");
        when(direccionService.crearDireccion(any(JsonObject.class))).thenReturn(dir1);
        assertThatThrownBy(()->clienteService.agregarDireccionCliente("dummy", jsonDireccion))
                .isInstanceOf(ClienteExisteDireccionException.class)
                .hasMessageContaining("Esa dirección ya esta registrada para el Cliente dummy");

    }

    @Test
    void eliminarDireccionCliente() throws ClienteUnicaDireccionException, ClienteNoExisteDireccionException, ClienteNoEncontradoException, ClienteDireccionException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "0.0", "0.0");
        Direccion dir2 = new Direccion(1234, "calle", "esquina", "detalles", "1.0", "1.0");
        Direccion dir3 = new Direccion(1234, "calle", "esquina", "detalles", "2.0", "2.0");
        dir1.setId(1L);
        dir2.setId(2L);
        dir3.setId(3L);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                null, EstadoCliente.ACTIVO, null, null);
        ArrayList<Direccion> direccionArrayList = new ArrayList<>();
        direccionArrayList.add(dir1);
        direccionArrayList.add(dir2);
        cliente1.setDirecciones(direccionArrayList);

        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir1);

        clienteService.eliminarDireccionCliente("dummy", 1L);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getDirecciones().size()).isEqualTo(1);
        // -------------------------------
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(null);
        assertThatThrownBy(()->clienteService.eliminarDireccionCliente("dummy", 1L))
                .isInstanceOf(ClienteDireccionException.class)
                .hasMessageContaining("No existe esa direccion en el sistema");
        // -------------------------------
        //cliente1.setDirecciones(direccionArrayList);
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir1);
        assertThatThrownBy(()->clienteService.eliminarDireccionCliente("dummy", 1L))
                .isInstanceOf(ClienteUnicaDireccionException.class)
                .hasMessageContaining("No puede eliminar la única dirección registrada del Cliente dummy");
        // -------------------------------
        direccionArrayList.add(dir1);
        //.setDirecciones(direccionArrayList);
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir3);
        assertThatThrownBy(()->clienteService.eliminarDireccionCliente("dummy", 3L))
                .isInstanceOf(ClienteNoExisteDireccionException.class)
                .hasMessageContaining("Esa dirección no esta registrada para el Cliente dummy");
    }

    @Test
    void modificarDireccionCliente() throws DireccionNumeroException, ClienteNoExisteDireccionException, ClienteNoEncontradoException, ClienteDireccionException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "0.0", "0.0");
        Direccion dir2 = new Direccion(1234, "calle", "esquina", "detalles", "1.0", "1.0");
        dir1.setId(1L);
        dir2.setId(2L);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                null, EstadoCliente.ACTIVO, null, null);
        ArrayList<Direccion> direccionArrayList = new ArrayList<>();
        direccionArrayList.add(dir1);
        cliente1.setDirecciones(direccionArrayList);

        JsonObject jsonDireccion = new JsonObject();
        jsonDireccion.addProperty("numero", "0");
        jsonDireccion.addProperty("calle", "dummy");
        jsonDireccion.addProperty("esquina", "dummy");
        jsonDireccion.addProperty("detalles", "dummy");
        jsonDireccion.addProperty("latitud", "5.0");
        jsonDireccion.addProperty("longitud", "5.0");

        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir1);
        when(direccionService.crearDireccion(any(JsonObject.class))).thenReturn(dir2);

        clienteService.modificarDireccionCliente("dummy", 1L, jsonDireccion);

        // -------------------------------
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(null);
        assertThatThrownBy(()->clienteService.modificarDireccionCliente("dummy", 1L, jsonDireccion))
                .isInstanceOf(ClienteDireccionException.class)
                .hasMessageContaining("No existe esa direccion en el sistema");
        // -------------------------------
        jsonDireccion.addProperty("latitud", "");
        jsonDireccion.addProperty("longitud", "");
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir1);
        assertThatThrownBy(()->clienteService.modificarDireccionCliente("dummy", 1L, jsonDireccion))
                .isInstanceOf(ClienteDireccionException.class)
                .hasMessageContaining("Debe ingresar una dirección Nueva");
        // -------------------------------
        direccionArrayList.add(dir1);
        jsonDireccion.addProperty("latitud", "5.0");
        jsonDireccion.addProperty("longitud", "5.0");
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir1);
        assertThatThrownBy(()->clienteService.modificarDireccionCliente("dummy", 2L, jsonDireccion))
                .isInstanceOf(ClienteNoExisteDireccionException.class)
                .hasMessageContaining("La direccion actual ingresada no existe para el Cliente dummy");
    }

    @Test
    void listarPedidosRealizados() throws ClienteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        when(clienteRepository.findByCorreo(anyString())).thenReturn(cliente1);
        clienteService.listarPedidosRealizados("","","","","","","","", "", "");
        clienteService.listarPedidosRealizados("a","","","","","","","", "", "");
        clienteService.listarPedidosRealizados("","FINALIZADO","","","","","","", "", "");
        clienteService.listarPedidosRealizados("","asdasd","","","","","","", "", "");
        clienteService.listarPedidosRealizados("","","a","","","","","", "", "");
        clienteService.listarPedidosRealizados("","","","a","","","","", "", "");
        clienteService.listarPedidosRealizados("","","","","PAYPAL","","","", "", "");
        clienteService.listarPedidosRealizados("","","","","asdasd","","","", "", "");
        clienteService.listarPedidosRealizados("","","","","","","2020-01-01,2021-01-01","", "", "");
        clienteService.listarPedidosRealizados("","","","","","","2020-01-01T18:05:05,2021-01-01T19:05:05","", "", "");
        clienteService.listarPedidosRealizados("","","","","","","","1,2", "", "");
        clienteService.listarPedidosRealizados("","","","","","","","1a,b2", "", "");
        clienteService.listarPedidosRealizados("","","","","","","","", "0", "");
        clienteService.listarPedidosRealizados("","","","","","","","", "0.5", "");
        clienteService.listarPedidosRealizados("","","","","","","","", "", "10");
        clienteService.listarPedidosRealizados("","","","","","","","", "", "1.0");
        when(clienteRepository.findByCorreo(anyString())).thenReturn(null);
        assertThatThrownBy(()->clienteService.listarPedidosRealizados("dummy","","",
                "","","","","", "",""))
                .isInstanceOf(ClienteNoEncontradoException.class)
                .hasMessageContaining("No existe el Cliente dummy");

//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("currentPage", 0);
//        jsonObject.addProperty("totalItems", 5);
//        jsonObject.addProperty("totalPages", 1);
//        jsonObject.addProperty("pedidos", );
//        when(pedidoService.listaPedidosHistorico(restaurante1, EstadoPedido.FINALIZADO,
//                MedioPago.PAYPAL, "", new LocalDateTime[2], new Float[2], 0, 5)).thenReturn(null);
    }

    @Test
    void listarMenus() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Menu menu1 = new Menu("dummy",10.0f,"dummy",
                true,0.0f,"dummy", CategoriaMenu.PIZZAS,restaurante1);
        menu1.setId(1L);
        Menu menu2 = new Menu("dummy",20.0f,"dummy",
                true,25.0f,"dummy", CategoriaMenu.HAMBURGUESAS,restaurante1);
        menu2.setId(2L);

        when(restauranteService.obtenerRestaurante(anyString())).thenReturn(restaurante1);
        when(menuRepository.findMenusByRestaurante(any(Restaurante.class))).thenReturn(List.of(menu1, menu2));
        List<JsonObject> expectedResult = menuConverter.listaJsonMenu(List.of(menu1,menu2));
//        List<JsonObject> expectedResult = new ArrayList<>();
//        JsonObject jsonMenu = new JsonObject();
//        jsonMenu.addProperty("id", "1");
//        jsonMenu.addProperty("nombre", "dummy");
//        jsonMenu.addProperty("price", "10.0");
//        jsonMenu.addProperty("descripcion", "dummy");
//        jsonMenu.addProperty("visible", true);
//        jsonMenu.addProperty("multiplicadorPromocion", "0.0");
//        jsonMenu.addProperty("imagen", "dummy");
//        jsonMenu.addProperty("categoria", CategoriaMenu.PIZZAS.name());
//        jsonMenu.addProperty("restaurante", "restaurante1@gmail.com");
//        expectedResult.add(jsonMenu);
//        jsonMenu = new JsonObject();
//        jsonMenu.addProperty("id", "2");
//        jsonMenu.addProperty("nombre", "dummy");
//        jsonMenu.addProperty("price", "20.0");
//        jsonMenu.addProperty("descripcion", "dummy");
//        jsonMenu.addProperty("visible", true);
//        jsonMenu.addProperty("multiplicadorPromocion", "25.0");
//        jsonMenu.addProperty("imagen", "dummy");
//        jsonMenu.addProperty("categoria", CategoriaMenu.HAMBURGUESAS.name());
//        jsonMenu.addProperty("restaurante", "restaurante1@gmail.com");
//        expectedResult.add(jsonMenu);
        //when(menuConverter.listaJsonMenu(any())).thenReturn(null);
        List<JsonObject> result = clienteService.listarMenus("dummy", "", null, null);
        assertThat(result).isEqualTo(expectedResult);

        // --------------------------------------------
        when(menuService.listMenuRestauranteCategoria(any(), any())).thenReturn(null);
        result = clienteService.listarMenus("dummy", "BEBIDAS", null, null);
        assertThat(result).isEqualTo(null);
        // --------------------------------------------
        when(menuService.existeCategoriaMenu(any(), any())).thenReturn(true);
        expectedResult = menuConverter.listaJsonMenu(List.of(menu1));
        result = clienteService.listarMenus("dummy", "BEBIDAS", 0.0f, 10.0f);
        assertThat(result).isEqualTo(expectedResult);
        // --------------------------------------------
        expectedResult = menuConverter.listaJsonMenu(List.of(menu2));
        result = clienteService.listarMenus("dummy", "", 11.0f, 18.0f);
        assertThat(result).isEqualTo(expectedResult);
    }
}