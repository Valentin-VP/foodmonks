package org.foodmonks.backend.Cliente;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Exceptions.MenuIdException;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuConverter;
import org.foodmonks.backend.Menu.MenuRepository;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.MenuCompra.MenuCompraService;
import org.foodmonks.backend.Pedido.Exceptions.*;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoConverter;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoComentarioException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoRazonException;
import org.foodmonks.backend.Reclamo.ReclamoService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.datatypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    @Test
    void crearPedido() throws RestauranteNoEncontradoException, MenuIdException, ClienteNoExisteDireccionException, PedidoTotalException, ClienteNoEncontradoException, MenuNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Direccion dir2 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
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
                true,25.0f,"dummy", CategoriaMenu.HAMBURGUESAS,null);
        menu2.setId(2L);

        when(clienteRepository.findByCorreo(any())).thenReturn(cliente1);
        when(restauranteService.obtenerRestaurante(any())).thenReturn(restaurante1);
        when(direccionService.obtenerDireccion(any())).thenReturn(dir1);
        when(menuService.obtenerMenu(any(), any())).thenReturn(menu1);
        JsonObject jsonRequestPedido = new JsonObject();
        JsonObject jsonMenu = new JsonObject();
        JsonArray jsonMenus = new JsonArray();
        jsonRequestPedido.addProperty("medioPago", "EFECTIVO");
        jsonRequestPedido.addProperty("direccionId", "1");
        jsonRequestPedido.addProperty("total", "10.0");
        jsonRequestPedido.addProperty("restaurante", "dummy");
        //jsonRequestPedido.addProperty("ordenId", "dummy");
        //jsonRequestPedido.addProperty("linkAprobacion", "dummy");
        jsonMenu.addProperty("id", "1");
        jsonMenu.addProperty("cantidad", "1");
        jsonMenus.add(jsonMenu);
        jsonRequestPedido.add("menus", jsonMenus);

        List<MenuCompra> menus = new ArrayList<>();
        MenuCompra menuCompra = new MenuCompra();
        menuCompra.setNombre("dummy");
        menuCompra.setPrice(10.0f);
        menuCompra.setDescripcion("dummy");
        menuCompra.setMultiplicadorPromocion(0.0f);
        menuCompra.setImagen("dummy");
        menuCompra.setCategoria(CategoriaMenu.PIZZAS);
        menus.add(menuCompra);

//        JsonObject jsonMenuCompra = new JsonObject();
//        jsonMenuCompra.addProperty("nombre","dummy");
//        jsonMenuCompra.addProperty("price",10.0f);
//        jsonMenuCompra.addProperty("descripcion","dummy");
//        jsonMenuCompra.addProperty("multiplicador",0.0f);
//        jsonMenuCompra.addProperty("imagen","dummy");
//        jsonMenuCompra.addProperty("categoria",CategoriaMenu.PIZZAS.toString());
        JsonObject result = clienteService.crearPedido("",jsonRequestPedido);
        assertThat(result).isEqualTo(null);
        // -----------------------------------------
        jsonRequestPedido.addProperty("medioPago", "PAYPAL");
        jsonRequestPedido.addProperty("ordenId", "dummy");
        jsonRequestPedido.addProperty("linkAprobacion", "dummy");
        result = clienteService.crearPedido("dummy",jsonRequestPedido);
        assertThat(result).isEqualTo(null);
        // -----------------------------------------
        when(direccionService.obtenerDireccion(any())).thenReturn(null);
        assertThatThrownBy(()->clienteService.crearPedido("dummy",jsonRequestPedido))
                .isInstanceOf(ClienteNoExisteDireccionException.class).hasMessageContaining("No existe la direccion ingresada en el sistema");
        // -----------------------------------------
        when(direccionService.obtenerDireccion(any())).thenReturn(dir2);
        assertThatThrownBy(()->clienteService.crearPedido("dummy",jsonRequestPedido))
                .isInstanceOf(ClienteNoExisteDireccionException.class).hasMessageContaining("La direccion ingresada no existe para el Cliente dummy");
        // -----------------------------------------
        when(direccionService.obtenerDireccion(any())).thenReturn(dir1);
        when(menuService.obtenerMenu(any(), any())).thenReturn(null);
        assertThatThrownBy(()->clienteService.crearPedido("dummy",jsonRequestPedido))
                .isInstanceOf(MenuNoEncontradoException.class).hasMessageContaining("El menu no existe para el Restaurante NombreRestaurante");
        // -----------------------------------------
        //when(menuService.obtenerMenu(any(), any())).thenReturn(menu1); // no llega
        jsonMenu.addProperty("id", "1a");
        jsonMenu.addProperty("cantidad", "1");
        jsonMenus.add(jsonMenu);
        jsonRequestPedido.add("menus", jsonMenus);
        assertThatThrownBy(()->clienteService.crearPedido("dummy",jsonRequestPedido))
                .isInstanceOf(MenuIdException.class).hasMessageContaining("El formado del menu con id 1a es invalido");


    }

    @Test
    void calificarCliente() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoEstadoException, PedidoPuntajeException, PedidoCalificacionClienteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 1, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 3.0f,1,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F, MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        clienteService.calificarCliente("restaurante1@gmail.com", calificacion);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getCalificacion()).isEqualTo(3.5f);
        // -------------------------------------------------------
        assertThatThrownBy(()->clienteService.calificarCliente("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El restaurante fakeEmail no es restaurante del pedido id 1");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.CONFIRMADO);
        assertThatThrownBy(()->clienteService.calificarCliente("restaurante1@gmail.com", calificacion))
                .isInstanceOf(PedidoEstadoException.class)
                .hasMessageContaining("El pedido id 1no esta en EstadoPedido para calificar");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.FINALIZADO);
        pedido1.setCalificacionCliente(new DtCalificacion(5.0f,"dummy"));
        assertThatThrownBy(()->clienteService.calificarCliente("restaurante1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionClienteException.class)
                .hasMessageContaining("El pedido con id 1 ya tiene calificacion Cliente");
    }

    @Test
    void modificarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoPuntajeException, PedidoCalificacionClienteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 1, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 3.0f,2,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F, MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionCliente(new DtCalificacion(5.0f,"dummy"));
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        clienteService.modificarCalificacionCliente("restaurante1@gmail.com", calificacion);

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getCalificacion()).isEqualTo(2.5f);
        // -------------------------------------------------------
        assertThatThrownBy(()->clienteService.modificarCalificacionCliente("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El restaurante fakeEmail no es restaurante del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionCliente(null);
        assertThatThrownBy(()->clienteService.modificarCalificacionCliente("restaurante1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionClienteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Cliente");
    }

    @Test
    void eliminarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoCalificacionClienteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 1, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.2f,5,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionCliente(new DtCalificacion(5.0f,"dummy"));

        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);

        clienteService.eliminarCalificacionCliente("restaurante1@gmail.com", "1");

        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getCalificacion()).isEqualTo(4.0f);
        assertThat(clienteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(4);
        // -------------------------------------------------------
        cliente1.setCalificacion(3.0f);
        cliente1.setCantidadCalificaciones(1);
        clienteService.eliminarCalificacionCliente("restaurante1@gmail.com", "1");
        //verify(restauranteRepository, times(2)).save(restauranteArgumentCaptor.capture());
        assertThat(clienteArgumentCaptor.getValue().getCalificacion()).isEqualTo(5.0f);
        assertThat(clienteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(0);
        // -------------------------------------------------------
        assertThatThrownBy(()->clienteService.eliminarCalificacionCliente("restaurante1@gmail.com", "    "))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        assertThatThrownBy(()->clienteService.eliminarCalificacionCliente("restaurante1@gmail.com", "asd"))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        // -------------------------------------------------------
        assertThatThrownBy(()->clienteService.eliminarCalificacionCliente("fakeEmail", "1"))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El restaurante fakeEmail no es restaurante del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionCliente(null);
        assertThatThrownBy(()->clienteService.eliminarCalificacionCliente("restaurante1@gmail.com", "1"))
                .isInstanceOf(PedidoCalificacionClienteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Cliente");
    }

    @Test
    void agregarReclamo() throws RestauranteNoEncontradoException, PedidoNoExisteException, ClientePedidoNoCoincideException, PedidoIdException, ReclamoComentarioException, ReclamoRazonException, ReclamoNoFinalizadoException, ReclamoExisteException, EmailNoEnviadoException, ClienteNoEncontradoException, PedidoSinRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 1, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.2f,5,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.2f,5,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);

        when(clienteRepository.findByCorreo(any())).thenReturn(cliente1);
        when(pedidoService.obtenerPedido(any())).thenReturn(pedido1);

        JsonObject jsonReclamo = new JsonObject();
        jsonReclamo.addProperty("pedidoId", "1");
        jsonReclamo.addProperty("razon", "dummy");
        jsonReclamo.addProperty("comentario", "dummy");

        JsonObject result = clienteService.agregarReclamo("dummy", jsonReclamo);
        assertThat(result).isEqualTo(null);

        // --------------------------------------
        pedido1.setCliente(null);
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(ClientePedidoNoCoincideException.class)
                .hasMessageContaining("El cliente con correo cliente1@gmail.com no realizo el pedido a reclamar");
        // --------------------------------------
        pedido1.setCliente(cliente2);
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(ClientePedidoNoCoincideException.class)
                .hasMessageContaining("El cliente con correo cliente1@gmail.com no realizo el pedido a reclamar");
        // -------------- verificarJsonReclamo ------------------------
        jsonReclamo.addProperty("pedidoId", "1.0");
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        jsonReclamo.addProperty("pedidoId", "");
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        jsonReclamo.addProperty("pedidoId", "1");
        jsonReclamo.addProperty("razon", "   ");
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(ReclamoRazonException.class)
                .hasMessageContaining("La razon del reclamo no puede ser vacia");
        jsonReclamo.addProperty("razon", "dummy");
        jsonReclamo.addProperty("comentario", "   ");
        assertThatThrownBy(()->clienteService.agregarReclamo("dummy", jsonReclamo))
                .isInstanceOf(ReclamoComentarioException.class)
                .hasMessageContaining("El comentario del reclamo no puede ser vacio");
    }
}