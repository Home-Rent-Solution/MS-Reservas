package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.*;
import com.HomeRentSolution.ms_reservas.dto.ms.PropiedadDTO;
import com.HomeRentSolution.ms_reservas.dto.ms.PrecioDTO;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.exception.RecursoNoEncontradoException;
import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private PropiedadesClient propiedadClient;
    @Mock private PrecioClient precioClient;
    @Mock private InquilinoClient inquilinosClient;
    @Mock private PagosClient pagosClient;
    @Mock private MensajeriaClient mensajeriaClient;
    @Mock private LimpiezaClient limpiezaClient;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private PropiedadDTO propiedadDTO;
    private PrecioDTO precioDTO;

    @BeforeEach
    void setUp() {
        reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setIdPropiedad(10L);
        reserva.setIdInquilino(20L);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaInicio(LocalDateTime.now().plusDays(5));
        reserva.setFechaFin(LocalDateTime.now().plusDays(10));
        reserva.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        reserva.setMontoTotal(new BigDecimal("300000"));

        propiedadDTO = new PropiedadDTO();
        propiedadDTO.setIdPropiedad(10L);
        propiedadDTO.setDisponible(true);
        propiedadDTO.setEstadoPropiedad(EstadoPropiedad.DISPONIBLE);
        propiedadDTO.setPrecio(new BigDecimal("50000"));

        precioDTO = new PrecioDTO();
        precioDTO.setIdPrecios(1L);
        precioDTO.setMultiplicador(1.0);
        precioDTO.setTemporada("NORMAL");
    }

    // PRUEBA 1: buscar reserva por ID existente
    @Test
    void buscarPorId_debeRetornarReserva_cuandoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Reserva resultado = reservaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdReserva());
    }

    // PRUEBA 2: buscar reserva por ID inexistente — debe lanzar excepción
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.buscarPorId(99L));
    }

    // PRUEBA 3: buscarTodas devuelve lista
    @Test
    void buscarTodas_debeRetornarListaDeReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<Reserva> resultado = reservaService.buscarTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    // PRUEBA 4: crear reserva con propiedad disponible
    @Test
    void crearReserva_debeGuardarYRetornarEstadoPendiente() {
        ReservaCrearRequest request = new ReservaCrearRequest();
        request.setIdPropiedad(10L);
        request.setIdInquilino(20L);
        request.setFechaInicio(LocalDate.now().plusDays(5));
        request.setFechaFin(LocalDate.now().plusDays(10));

        when(propiedadClient.obtenerPropiedadPorId(10L)).thenReturn(propiedadDTO);
        when(precioClient.obtenerPrecioPorPropiedad(10L)).thenReturn(precioDTO);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse resultado = reservaService.crearReserva(request);

        assertNotNull(resultado);
        verify(reservaRepository, atLeastOnce()).save(any(Reserva.class));
        verify(pagosClient, times(1)).crearPago(any());
    }

    // PRUEBA 5: buscar por estado devuelve lista filtrada
    @Test
    void buscarPorEstado_debeRetornarSoloReservasConEseEstado() {
        when(reservaRepository.findByEstadoReserva(EstadoReserva.PENDIENTE))
                .thenReturn(List.of(reserva));

        List<Reserva> resultado = reservaService.buscarPorEstado(EstadoReserva.PENDIENTE);

        assertFalse(resultado.isEmpty());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(0).getEstadoReserva());
    }

    // PRUEBA 6: cancelar reserva en estado válido
    @Test
    void cancelarReserva_debeCambiarEstadoACancelada() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse resultado = reservaService.cancelarReserva(1L, "Motivo de prueba");

        verify(reservaRepository, atLeastOnce()).save(any(Reserva.class));
        verify(pagosClient, times(1)).cancelarPago(anyLong(), anyString(), any(BigDecimal.class));
    }
}
