package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.jav.exposerversdk.PushClientException;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionConverter;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.Pedido.Exceptions.*;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.*;
import org.foodmonks.backend.notificacion.NotificacionExpoService;
import org.foodmonks.backend.paypal.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {
    @InjectMocks
    RestauranteService restauranteService;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RestauranteRepository restauranteRepository;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    MenuService menuService;
    @Mock
    RestauranteConverter restauranteConverter;
    @Mock
    PedidoService pedidoService;
    @Spy
    ReclamoConverter reclamoConverter;
    @Mock
    TemplateEngine templateEngine;
    @Mock
    EmailService emailService;
    @Mock
    PayPalService payPalService;
    @Mock
    NotificacionExpoService notificacionExpoService;
    @Mock
    DireccionService direccionService;
    @Mock
    DireccionConverter direccionConverter;

    @BeforeEach
    void setUp() {
        restauranteConverter = new RestauranteConverter(direccionConverter);
        restauranteService = new RestauranteService(restauranteRepository, passwordEncoder,
                usuarioRepository, menuService, restauranteConverter,
                pedidoService, reclamoConverter, templateEngine,
                emailService, payPalService, direccionService, notificacionExpoService);
        ReflectionTestUtils.setField(restauranteService, "googleapikey", "AIzaSyDKRPzWlwRbrMOqg8W_nXOgr_fn5_Jgk0s");
        ReflectionTestUtils.setField(restauranteService, "distanciaMaxima", 1000000L);
    }

    @Test
    void createSolicitudAltaRestaurante() throws UsuarioNoRestaurante, UsuarioExisteException, MenuMultiplicadorException, MenuNombreExistente, RestauranteFaltaMenuException, MenuPrecioException, ClienteDireccionException, MenuNombreException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);

        ArrayList<JsonObject> jsonMenus = new ArrayList<>();
        jsonMenus.addAll(List.of(new JsonObject(), new JsonObject(), new JsonObject()));

        restauranteService.createSolicitudAltaRestaurante("dummy", "dummy", "dummy", "dummy", LocalDate.now(), 5.0f, "dummy",
                "1234", dir1, "PENDIENTE", "1234", "dummy", "dummy", "dummy", jsonMenus);

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getDireccion().getNumero()).isEqualTo(dir1.getNumero());

        // ---------------------------------

        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        assertThatThrownBy(()-> restauranteService.createSolicitudAltaRestaurante("dummy", "dummy", "dummy", "dummy", LocalDate.now(), 5.0f, "dummy",
                "1234", dir1, "PENDIENTE", "1234", "dummy", "dummy", "dummy", jsonMenus))
                .isInstanceOf(UsuarioExisteException.class).hasMessageContaining("Ya existe un Usuario registrado con el correo dummy");

        // ---------------------------------

        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);
        assertThatThrownBy(()-> restauranteService.createSolicitudAltaRestaurante("dummy", "dummy", "dummy", "dummy", LocalDate.now(), 5.0f, "dummy",
                "1234", null, "PENDIENTE", "1234", "dummy", "dummy", "dummy", jsonMenus))
                .isInstanceOf(ClienteDireccionException.class).hasMessageContaining("Debe ingresar una direccion");

        // ---------------------------------

        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);
        jsonMenus.remove(0);
        assertThatThrownBy(()-> restauranteService.createSolicitudAltaRestaurante("dummy", "dummy", "dummy", "dummy", LocalDate.now(), 5.0f, "dummy",
                "1234", dir1, "PENDIENTE", "1234", "dummy", "dummy", "dummy", jsonMenus))
                .isInstanceOf(RestauranteFaltaMenuException.class).hasMessageContaining("Debe ingresar al menos 3 menus");
    }

    @Test
    void listarHistoricoPedidos() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        restauranteService.listarHistoricoPedidos("","","","","","","0","5");
        restauranteService.listarHistoricoPedidos("a","","","","","","0","5");
        restauranteService.listarHistoricoPedidos("","FINALIZADO","","","","","0","5");
        //restauranteService.listarHistoricoPedidos("","asdasd","","","","","0","5");
        restauranteService.listarHistoricoPedidos("","","PAYPAL","","","","0","5");
        //restauranteService.listarHistoricoPedidos("","","asdasd","","","","0","5");
        restauranteService.listarHistoricoPedidos("","","","","2020-01-01,2021-01-01","","0","5");
        //restauranteService.listarHistoricoPedidos("","","","","2020-01-01T18:05:05,2021-01-01T19:05:05","","0","5");
        restauranteService.listarHistoricoPedidos("","","","","","1,2","0","5");
        //restauranteService.listarHistoricoPedidos("","","","","","1a,b2","0","5");
        restauranteService.listarHistoricoPedidos("","","","","","","0","5");
        //restauranteService.listarHistoricoPedidos("","","","","","","O","5");
        //restauranteService.listarHistoricoPedidos("","","","","","","0","10");
        //restauranteService.listarHistoricoPedidos("","","","","","","0","1O");
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.listarHistoricoPedidos("dummy","","",
                "","","","",""))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");

//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("currentPage", 0);
//        jsonObject.addProperty("totalItems", 5);
//        jsonObject.addProperty("totalPages", 1);
//        jsonObject.addProperty("pedidos", );
//        when(pedidoService.listaPedidosHistorico(restaurante1, EstadoPedido.FINALIZADO,
//                MedioPago.PAYPAL, "", new LocalDateTime[2], new Float[2], 0, 5)).thenReturn(null);
    }

    @Test
    void registrarPagoEfectivo() throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(true);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(true);
        restauranteService.registrarPagoEfectivo("dummy",1L);
        verify(pedidoService).cambiarEstadoPedido(anyLong(), any(EstadoPedido.class));
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(false);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(PedidoNoExisteException.class)
                .hasMessageContaining("No existe el pedido con id 1");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(true);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(false);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el pedido con id 1 para el Restaurante dummy");
    }

    @Test
    void listaRestaurantesAbiertos() throws Exception {
        //distancia serian 260 km mas o menos
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "-32.050505", "-56.050505");
        Direccion dir2 = new Direccion(1234, "calle", "esquina", "detalles", "-31.050505", "-55.050505");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findRestaurantesIgnoreCaseByEstadoOrderByCalificacionDesc(any(EstadoRestaurante.class))).thenReturn(
                        List.of(restaurante2, restaurante1));
        when(direccionService.obtenerDireccion(anyLong())).thenReturn(dir2);
        List<JsonObject> expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        List<JsonObject> result = restauranteService.listaRestaurantesAbiertos("", "", true, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstadoOrderByCalificacionDesc(anyString(), any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante2, restaurante1));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        result = restauranteService.listaRestaurantesAbiertos("nombreDelRestaurante", "", true, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(menuService.existeCategoriaMenu(any(Restaurante.class), any(CategoriaMenu.class))).thenReturn(true);
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        result = restauranteService.listaRestaurantesAbiertos("", "OTROS", true, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesIgnoreCaseByEstado(any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante1, restaurante2));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("", "", false, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstado(anyString(), any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante1, restaurante2));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("nombreDelRestaurante", "", false, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(menuService.existeCategoriaMenu(any(Restaurante.class), any(CategoriaMenu.class))).thenReturn(true);
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("", "OTROS", false, "1");
        assertThat(result).isEqualTo(expectedRestaurantes);
    }

    @Test
    void actualizarEstadoPedido() throws PedidoNoExisteException, RestauranteNoEncontradoException, PushClientException, PedidoIdException, PedidoDevolucionException, PedidoDistintoRestauranteException, IOException, EmailNoEnviadoException, InterruptedException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Pedido pedido1 = new Pedido(EstadoPedido.PENDIENTE, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(true);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        restauranteService.actualizarEstadoPedido("dummy", 1L,"CONFIRMADO",30);
        verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.CONFIRMADO);
        // -------------------------------------------------------
        restauranteService.actualizarEstadoPedido("dummy", 1L,"RECHAZADO",30);
        //verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.RECHAZADO);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.PAYPAL);
        restauranteService.actualizarEstadoPedido("dummy", 1L,"CONFIRMADO",30);
        verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.FINALIZADO);
        // -------------------------------------------------------
        //restauranteService.actualizarEstadoPedido("dummy", 1L,"RECHAZADO",30);
        //verify(pedidoService, times(2)).cambiarEstadoPedido(1L, EstadoPedido.RECHAZADO);
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.actualizarEstadoPedido("dummy", 1L, "dummy", 0))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(false);
        assertThatThrownBy(()->restauranteService.actualizarEstadoPedido("dummy", 1L, "dummy", 0))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el pedido con id 1 para el Restaurante dummy");
    }

    @Test
    void calificarRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoEstadoException, PedidoPuntajeException, PedidoCalificacionRestauranteException {
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
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion);

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(3.5f);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.calificarRestaurante("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.CONFIRMADO);
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoEstadoException.class)
                .hasMessageContaining("El pedido id 1no esta en EstadoPedido para calificar");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.FINALIZADO);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 ya tiene calificacion Restaurante");
        // -------------------------------------------------------
        // ------------------------ verificarJsonCalificacion -------------------------------
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        calificacion.addProperty("puntaje", "6");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        calificacion.addProperty("puntaje", "5.1");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        calificacion.addProperty("puntaje", "a");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        // -------------------------------------------------------
        calificacion.addProperty("idPedido", "1.");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");

    }

    @Test
    void modificarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoPuntajeException, PedidoCalificacionRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));

        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        restauranteService.modificarCalificacionRestaurante("cliente1@gmail.com", calificacion);

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(2.8f);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.modificarCalificacionRestaurante("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        assertThatThrownBy(()->restauranteService.modificarCalificacionRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Restaurante");
    }

    @Test
    void eliminarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoCalificacionRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));

        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);

        restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1");

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(4.0f);
        assertThat(restauranteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(4);
        // -------------------------------------------------------
        restaurante1.setCalificacion(3.0f);
        restaurante1.setCantidadCalificaciones(1);
        restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1");
        verify(restauranteRepository, times(2)).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(5.0f);
        assertThat(restauranteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(0);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "     "))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "asd"))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("fakeEmail", "1"))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1"))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Restaurante");
    }

    @Test
    void listarReclamos() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        Reclamo reclamo1 = new Reclamo("razon1","comentario1",LocalDateTime.of(2020,1,1,0,0,0), pedido1);
        pedido1.setReclamo(reclamo1);
        Pedido pedido2 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido2.setCliente(cliente1);
        pedido2.setRestaurante(restaurante1);
        Reclamo reclamo2 = new Reclamo("razon2","comentario2",LocalDateTime.of(2020,2,1,0,0,0), pedido2);
        pedido2.setReclamo(reclamo2);
        cliente1.setPedidos(List.of(pedido1, pedido2));
        restaurante1.setPedidos(List.of(pedido1, pedido2));

        ArrayList<Reclamo> reclamoArrayList = new ArrayList<>();
        reclamoArrayList.addAll(List.of(reclamo1, reclamo2));
        restaurante1.setReclamos(reclamoArrayList);


        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        JsonArray expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo1, reclamo2));

        JsonArray result = restauranteService.listarReclamos("dummy", false, "", "");

        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2, reclamo1));
        result = restauranteService.listarReclamos("dummy", true, "", "");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "fail", "razon1");
        assertThat(result).isEqualTo(expectedReclamos);
        result = restauranteService.listarReclamos("dummy", false, "cliente", "fail");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo1));
        result = restauranteService.listarReclamos("dummy", false, "cliente", "razon1");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2, reclamo1));
        result = restauranteService.listarReclamos("dummy", false, "cliente1@gmail.com", "");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "fail", "");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2));
        result = restauranteService.listarReclamos("dummy", false, "", "razon2");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "", "fail");
        assertThat(result).isEqualTo(expectedReclamos);

    }

    @Test
    void realizarDevolucion() throws PedidoNoExisteException, PedidoIdException, PedidoDevolucionException, PedidoDistintoRestauranteException, IOException, RestauranteNoEncontradoException, EmailNoEnviadoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        cliente1.setPedidos(List.of(pedido1));
        restaurante1.setPedidos(List.of(pedido1));
        Reclamo reclamo1 = new Reclamo("razon1","comentario1",LocalDateTime.of(2020,1,1,0,0,0), pedido1);
        pedido1.setReclamo(reclamo1);
        restaurante1.setReclamos(List.of(reclamo1));

        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        when(payPalService.refundOrder(anyString())).thenReturn("dummy");
        when(payPalService.getOrder(anyString())).thenReturn("dummy");
        JsonObject expectedResponse = new JsonObject();
        expectedResponse.addProperty("Mensaje","Devolucion");
        expectedResponse.addProperty("status", "Mail de rechazo enviado");

        JsonObject result = restauranteService.realizarDevolucion("dummy", "1","dummy",false);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.PAYPAL);
        pedido1.setOrdenPaypal(new DtOrdenPaypal("","",""));
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",false);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        expectedResponse.addProperty("status", "dummy");
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",true);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.EFECTIVO);
        expectedResponse.addProperty("status", "completado");
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",true);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.realizarDevolucion("dummy", "1.0","dummy",false))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante2);
        assertThatThrownBy(()->restauranteService.realizarDevolucion("restaurante2@gmail.com", "1","dummy",false))
                .isInstanceOf(PedidoDistintoRestauranteException.class)
                .hasMessageContaining("El pedido id 1 no pertenece al restaurante restaurante2@gmail.com");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        pedido1.setEstado(EstadoPedido.DEVUELTO);
        assertThatThrownBy(()->restauranteService.realizarDevolucion("dummy", "1","dummy",false))
                .isInstanceOf(PedidoDevolucionException.class)
                .hasMessageContaining("El pedido no esta FINALIZADO, no se puede aplicar una devolucion");

    }

    @Test
    void pedidosRegistrados(){
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Pedido pedido;

        for (int i = 0; i < 10; i++){
            pedido = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10),
                    500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2022,3,1,0,0,0),dir1,null);
            pedido.setRestaurante(restaurante1);
            restaurante1.getPedidos().add(pedido);
        }
        for (int i = 0; i < 10; i++){
            pedido = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 6),
                    500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2022,3,1,0,0,0),dir1,null);
            pedido.setRestaurante(restaurante1);
            restaurante2.getPedidos().add(pedido);
        }

        when(restauranteRepository.findAllByRolesOrderByCalificacion(anyString())).thenReturn(List.of(restaurante1, restaurante2));
        JsonObject expectedResult = new JsonObject();
        JsonArray meses = new JsonArray();
        JsonObject pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Enero");
        pedidosRestaurante.addProperty("cantidad", 7);
        when(pedidoService.cantPedidosRestaurante(any(Restaurante.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(3L).thenReturn(4L)
                .thenReturn(3L).thenReturn(5L).thenReturn(1L).thenReturn(1L).thenReturn(0L);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Febrero");
        pedidosRestaurante.addProperty("cantidad", 8);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Marzo");
        pedidosRestaurante.addProperty("cantidad", 2);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Abril");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Mayo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Junio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Julio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Agosto");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Septiembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Octubre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Noviembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Diciembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);

        expectedResult.add("pedidosRegistrados", meses);
        JsonObject result = restauranteService.pedidosRegistrados(2022);
        assertThat(result).isEqualTo(expectedResult);

        // -------------------------------------------------------------------------

        expectedResult = new JsonObject();
        meses = new JsonArray();
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Enero");
        pedidosRestaurante.addProperty("cantidad", 0);
        when(pedidoService.cantPedidosRestaurante(any(Restaurante.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0L)
                .thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L)
                .thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L)
                .thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(3L).thenReturn(0L);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Febrero");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Marzo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Abril");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Mayo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Junio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Julio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Agosto");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Septiembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Octubre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Noviembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Diciembre");
        pedidosRestaurante.addProperty("cantidad", 3);
        meses.add(pedidosRestaurante);

        expectedResult.add("pedidosRegistrados", meses);
        result = restauranteService.pedidosRegistrados(2021);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void ventasRestaurantes() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Pedido pedido;

        for (int i = 0; i < 10; i++){
            pedido = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10),
                    500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2022,3,1,0,0,0),dir1,null);
            pedido.setRestaurante(restaurante1);
            restaurante1.getPedidos().add(pedido);
        }

        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        JsonObject expectedResult = new JsonObject();
        JsonArray meses = new JsonArray();
        JsonObject pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Enero");
        pedidosRestaurante.addProperty("cantidad", 3);
        when(pedidoService.cantVentasRestauranteAnio(any(Restaurante.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(3L).thenReturn(3L)
                .thenReturn(1L).thenReturn(0L);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Febrero");
        pedidosRestaurante.addProperty("cantidad", 3);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Marzo");
        pedidosRestaurante.addProperty("cantidad", 1);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Abril");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Mayo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Junio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Julio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Agosto");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Septiembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Octubre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Noviembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Diciembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);

        expectedResult.addProperty("restaurante",restaurante1.getNombreRestaurante());
        expectedResult.addProperty("anio",2022);
        expectedResult.add("ventas", meses);
        JsonObject result = restauranteService.ventasRestaurantes("dummy", 2022);
        assertThat(result).isEqualTo(expectedResult);

        // -------------------------------------------------------------------------

        expectedResult = new JsonObject();
        meses = new JsonArray();
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Enero");
        pedidosRestaurante.addProperty("cantidad", 0);
        when(pedidoService.cantVentasRestauranteAnio(any(Restaurante.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0L)
                .thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L).thenReturn(0L)
                .thenReturn(0L).thenReturn(0L).thenReturn(3L);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Febrero");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Marzo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Abril");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Mayo");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Junio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Julio");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Agosto");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Septiembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Octubre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Noviembre");
        pedidosRestaurante.addProperty("cantidad", 0);
        meses.add(pedidosRestaurante);
        pedidosRestaurante = new JsonObject();
        pedidosRestaurante.addProperty("mes","Diciembre");
        pedidosRestaurante.addProperty("cantidad", 3);
        meses.add(pedidosRestaurante);

        expectedResult.addProperty("restaurante",restaurante1.getNombreRestaurante());
        expectedResult.addProperty("anio",2021);
        expectedResult.add("ventas", meses);
        result = restauranteService.ventasRestaurantes("dummy", 2021);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void obtenerBalance() throws Exception {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Pedido pedido;
        ArrayList<Pedido> pedidos = new ArrayList<>();

        int menu = 1;
        EstadoPedido estado = EstadoPedido.RECLAMORECHAZADO;
        MedioPago medioPago = MedioPago.EFECTIVO;
        Float total = 150.0F;
        for (int i = 0; i < 20; i++){
            //System.out.println(LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10).toLocalDate().toString());
            pedido = new Pedido(estado, LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10),
                    total, medioPago,
                    LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10), dir1, null);
            //pedido = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2022,3,1,0,0,0).minusDays(i * 10),
            //        500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2022,3,1,0,0,0),dir1,null);
            pedido.setRestaurante(restaurante1);
            restaurante1.getPedidos().add(pedido);
            List<MenuCompra> menus = new ArrayList<>();
            for (int j=0; j<2; j++){
                MenuCompra menuCompra = new MenuCompra("menu" + menu, 75.0F, "a", 0.0F,
                        "https://media.istockphoto.com/vectors/creative-hamburger-logo-design-symbol-vector-illustration-vector-id1156464773?k=20&m=1156464773&s=170667a&w=0&h=AcKSZuETET89SF-Liid0mAWTL5w6YQCIxeynD8J01Lk=",
                        CategoriaMenu.PIZZAS);
                menuCompra.setCantidad(i+1);
                menus.add(menuCompra);
                menu++;
            }
            pedido.setMenusCompra(menus);

            estado = estado.equals(EstadoPedido.FINALIZADO) ? EstadoPedido.DEVUELTO : EstadoPedido.FINALIZADO;
            medioPago = medioPago.equals(MedioPago.EFECTIVO) ? MedioPago.PAYPAL : MedioPago.EFECTIVO;
            total = estado.equals(EstadoPedido.FINALIZADO) ? total + 3 : total;
            pedidos.add(pedido);
        }

        when(restauranteRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);
        when(pedidoService.pedidosRestaurante(any())).thenReturn(pedidos);

        JsonObject result = restauranteService.obtenerBalance("dummy", "","","","");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(0).getAsJsonObject().get("ventasEfectivo"))
                .isEqualTo(new JsonPrimitive(0));
        // ------------------------------------
        when(pedidoService.pedidosRestauranteMedioPago(any(), any())).thenReturn(pedidos);
        result = restauranteService.obtenerBalance("dummy", "PAYPAL","","","");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(1).getAsJsonObject().get("ventasPaypal"))
                .isEqualTo(new JsonPrimitive(165.0));
        // ------------------------------------
        when(pedidoService.pedidosRestauranteFechaHoraProcesado(any(), any(), any())).thenReturn(pedidos);
        result = restauranteService.obtenerBalance("dummy", "","2021-08-23","2022-12-31","");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(1).getAsJsonObject().get("ventasPaypal"))
                .isEqualTo(new JsonPrimitive(180.0));
        // ------------------------------------
        when(pedidoService.pedidosRestauranteMedioPagoFechaHoraProcesado(any(), any(), any(), any())).thenReturn(pedidos);
        result = restauranteService.obtenerBalance("dummy", "EFECTIVO","2021-08-23","2022-12-31","");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(1).getAsJsonObject().get("ventasPaypal"))
                .isEqualTo(new JsonPrimitive(180.0));
        result = restauranteService.obtenerBalance("dummy", "PAYPAL","2021-08-23","2022-12-31","");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(1).getAsJsonObject().get("ventasPaypal"))
                .isEqualTo(new JsonPrimitive(180.0));
        // ------------------------------------
        result = restauranteService.obtenerBalance("dummy", "EFECTIVO","2021-08-23","2022-12-31","BEBIDAS");

        assertThat(result.getAsJsonArray("meses").get(0).getAsJsonObject()
                .get("indicadores").getAsJsonArray().get(1).getAsJsonObject().get("ventasPaypal"))
                .isEqualTo(new JsonPrimitive(0.0));
    }
}