package org.foodmonks.backend.Pedido;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    PedidoService pedidoService;
    @Mock
    PedidoRepository pedidoRepository;
    @Mock
    PedidoConverter pedidoConverter;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoService(pedidoRepository, pedidoConverter);
    }

    @Test
    void listaPedidosHistorico() {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                "a", LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        LocalDateTime[] fecha = new LocalDateTime[2];
        Float[] total = new Float[2];
        total[0] = 0f;
        total[1] = 1f;

        fecha[0] = LocalDateTime.now();
        fecha[1] = LocalDateTime.now();
        Page<Pedido> pedidoPage = new Page<Pedido>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Pedido, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Pedido> getContent() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Pedido> iterator() {
                return null;
            }
        };

        when(pedidoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pedidoPage);
        when(pedidoConverter.listaJsonPedidoPaged(any())).thenReturn(new JsonObject());
        pedidoService.listaPedidosHistorico(restaurante, EstadoPedido.FINALIZADO, MedioPago.PAYPAL, "asc", fecha, total, 0, 5);
        pedidoService.listaPedidosHistorico(restaurante, EstadoPedido.DEVUELTO, MedioPago.PAYPAL, "asc", fecha, total, 0, 5);
        pedidoService.listaPedidosHistorico(restaurante, null, MedioPago.PAYPAL, "desc", fecha, total, 0, 5);
    }

    @Test
    void listaPedidosRealizados() {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", "a",
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        LocalDateTime[] fecha = new LocalDateTime[2];
        Float[] total = new Float[2];
        total[0] = 0f;
        total[1] = 1f;

        fecha[0] = LocalDateTime.now();
        fecha[1] = LocalDateTime.now();
        Page<Pedido> pedidoPage = new Page<Pedido>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Pedido, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Pedido> getContent() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Pedido> iterator() {
                return null;
            }
        };

        when(pedidoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pedidoPage);
        when(pedidoConverter.listaJsonPedidoPaged(any())).thenReturn(new JsonObject());
        pedidoService.listaPedidosRealizados(cliente1, EstadoPedido.FINALIZADO, "dummy", "dummy", MedioPago.PAYPAL, "asc", fecha, total, 0, 5);
        pedidoService.listaPedidosRealizados(cliente1, EstadoPedido.DEVUELTO, "dummy", "dummy", MedioPago.PAYPAL, "asc", fecha, total, 0, 5);
        pedidoService.listaPedidosRealizados(cliente1, null, "dummy", "dummy", MedioPago.PAYPAL, "desc", fecha, total, 0, 5);
    }

    @Test
    void crearPedido() {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", "a",
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Restaurante restaurante = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                "a", LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        pedidoService.crearPedido(EstadoPedido.PENDIENTE, 50.0f, MedioPago.EFECTIVO, null,
                dir1, cliente1, restaurante, new ArrayList<>());
        pedidoService.crearPedido(EstadoPedido.PENDIENTE, 50.0f, MedioPago.PAYPAL, new DtOrdenPaypal("dummy", "dummy", "dummy"),
                dir1, cliente1, restaurante, new ArrayList<>());
    }

    @Test
    void obtenerPedido() throws PedidoNoExisteException {
        Pedido pedido = new Pedido(EstadoPedido.PENDIENTE, 100.0F, MedioPago.EFECTIVO);
        when(pedidoRepository.findPedidoById(anyLong())).thenReturn(pedido);
        Pedido result = pedidoService.obtenerPedido(1L);
        assertThat(result.getEstado()).isEqualTo(pedido.getEstado());
    }

    @Test
    void obtenerPedido_Inexistente() throws PedidoNoExisteException {
        when(pedidoRepository.findPedidoById(anyLong())).thenReturn(null);
        assertThatThrownBy(()->pedidoService.obtenerPedido(1L)).isInstanceOf(PedidoNoExisteException.class).hasMessageContaining("No existe pedido con id 1");
    }
}