package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.*;
import com.HomeRentSolution.ms_reservas.config.AppConfig;
import com.HomeRentSolution.ms_reservas.dto.ms.PropiedadDTO;
import com.HomeRentSolution.ms_reservas.dto.ms.PrecioDTO;
import com.HomeRentSolution.ms_reservas.dto.ms.InquilinoDTO;
import com.HomeRentSolution.ms_reservas.dto.ms.ReservaEventoDTO;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.exception.AccesoDenegadoException;
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
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse resultado = reservaService.crearReserva(request);

        assertNotNull(resultado);
        verify(reservaRepository, atLeastOnce()).save(any(Reserva.class));
        verify(rabbitTemplate).convertAndSend(
                eq(AppConfig.PAGOS_EXCHANGE),
                eq(AppConfig.ROUTING_RESERVA_CREADA_PAGOS),
                any(ReservaEventoDTO.class));
        verify(rabbitTemplate).convertAndSend(
                eq(AppConfig.LIMPIEZAS_EXCHANGE),
                eq(AppConfig.ROUTING_RESERVA_CREADA_LIMPIEZA),
                any(ReservaEventoDTO.class));
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
        assertEquals(EstadoReserva.CANCELADA, resultado.getEstado());
        verify(rabbitTemplate).convertAndSend(
                eq(AppConfig.PAGOS_EXCHANGE),
                eq(AppConfig.ROUTING_RESERVA_CANCELADA_PAGOS),
                any(ReservaEventoDTO.class));
        verify(rabbitTemplate).convertAndSend(
                eq(AppConfig.LIMPIEZAS_EXCHANGE),
                eq(AppConfig.ROUTING_RESERVA_CANCELADA_LIMPIEZA),
                any(ReservaEventoDTO.class));
    }

    @Test
    void cancelarReserva_rechazaReservaYaCancelada() {
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservaService.cancelarReserva(1L, "duplicada"));

        assertTrue(exception.getMessage().contains("CANCELADA"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_rechazaPropiedadInexistente() {
        ReservaCrearRequest request = new ReservaCrearRequest();
        request.setIdPropiedad(99L);
        when(propiedadClient.obtenerPropiedadPorId(99L)).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.crearReserva(request));
    }

    @Test
    void crearReserva_rechazaPropiedadNoDisponible() {
        ReservaCrearRequest request = new ReservaCrearRequest();
        request.setIdPropiedad(10L);
        propiedadDTO.setDisponible(false);
        when(propiedadClient.obtenerPropiedadPorId(10L)).thenReturn(propiedadDTO);

        assertThrows(RuntimeException.class, () -> reservaService.crearReserva(request));
    }

    @Test
    void confirmarReserva_debeConfirmarYNotificar() {
        InquilinoDTO inquilino = new InquilinoDTO();
        inquilino.setIdInquilino(20L);
        inquilino.setNombre("Catalina");
        inquilino.setEmail("catalina@mail.com");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(inquilinosClient.obtenerInquilinoPorId(20L)).thenReturn(inquilino);

        ReservaResponse resultado = reservaService.confirmarReserva(1L);

        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
        verify(pagosClient).confirmarPago(1L);
        verify(propiedadClient).cambiarEstado(10L);
        verify(mensajeriaClient).enviarEmail(any());
    }

    @Test
    void confirmarReserva_vencida_debeCancelarla() {
        reserva.setFechaLimitesPago(LocalDateTime.now().minusMinutes(1));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(RuntimeException.class, () -> reservaService.confirmarReserva(1L));
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstadoReserva());
    }

    @Test
    void confirmarReserva_rechazaEstadoInvalido() {
        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(RuntimeException.class, () -> reservaService.confirmarReserva(1L));
    }

    @Test
    void buscarConFiltro_debeRetornarSoloPropiedadDisponible() {
        PropiedadDTO ocupada = new PropiedadDTO();
        ocupada.setIdPropiedad(10L);
        ocupada.setUbicacion("Santiago");
        ocupada.setPrecio(new BigDecimal("50000"));
        PropiedadDTO libre = new PropiedadDTO();
        libre.setIdPropiedad(11L);
        libre.setUbicacion("Santiago");
        libre.setPrecio(new BigDecimal("60000"));
        PrecioDTO precioLibre = new PrecioDTO();
        precioLibre.setTemporada("Alta");
        precioLibre.setMultiplicador(1.5);
        ReservaFiltroRequest filtro = new ReservaFiltroRequest();
        filtro.setFechaInicio(LocalDateTime.now().plusDays(6));
        filtro.setFechaFin(LocalDateTime.now().plusDays(7));
        filtro.setUbicacion("Santiago");
        filtro.setPrecioMin(new BigDecimal("40000"));
        filtro.setPrecioMax(new BigDecimal("70000"));
        when(propiedadClient.obtenerTodas()).thenReturn(List.of(ocupada, libre));
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));
        when(precioClient.obtenerPreciosPorPropiedad(11L)).thenReturn(List.of(precioLibre));

        List<ReservaDetalleResponse> resultado = reservaService.buscarConFiltro(filtro);

        assertEquals(1, resultado.size());
        assertEquals(11L, resultado.get(0).getIdPropiedad());
    }

    @Test
    void obtenerReservasCliente_debeFiltrarYEnriquecer() {
        InquilinoDTO inquilino = new InquilinoDTO();
        inquilino.setIdInquilino(20L);
        inquilino.setNombre("Catalina");
        inquilino.setEmail("catalina@mail.com");
        propiedadDTO.setUbicacion("Santiago");
        when(inquilinosClient.obtenerInquilinoPorId(20L)).thenReturn(inquilino);
        when(reservaRepository.findByIdInquilino(20L)).thenReturn(List.of(reserva));
        when(propiedadClient.obtenerPropiedadPorId(10L)).thenReturn(propiedadDTO);
        when(precioClient.obtenerPreciosPorPropiedad(10L)).thenReturn(List.of(precioDTO));

        List<ReservaDetalleResponse> resultado = reservaService.obtenerReservasCliente(
                20L, LocalDate.now().plusDays(4), LocalDate.now().plusDays(11));

        assertEquals(1, resultado.size());
        assertEquals("Catalina", resultado.get(0).getNombre());
        assertEquals("NORMAL", resultado.get(0).getTemporada());
    }

    @Test
    void obtenerReservasCliente_rechazaInquilinoInexistente() {
        when(inquilinosClient.obtenerInquilinoPorId(99L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.obtenerReservasCliente(99L, null, null));
    }

    @Test
    void finalizarReserva_debeFinalizarYRetornarDetalle() {
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        InquilinoDTO inquilino = new InquilinoDTO();
        inquilino.setIdInquilino(20L);
        inquilino.setNombre("Catalina");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(inquilinosClient.obtenerInquilinoPorId(20L)).thenReturn(inquilino);
        when(propiedadClient.obtenerPropiedadPorId(10L)).thenReturn(propiedadDTO);
        when(precioClient.obtenerPreciosPorPropiedad(10L)).thenReturn(List.of(precioDTO));

        ReservaDetalleResponse resultado = reservaService.finalizarReserva(1L);

        assertEquals(EstadoReserva.FINALIZADA, resultado.getEstadoReserva());
        verify(rabbitTemplate).convertAndSend(
                eq(AppConfig.RESERVAS_EXCHANGE), eq(AppConfig.ROUTING_FINALIZADA), any(ReservaResponse.class));
    }

    @Test
    void finalizarReserva_rechazaEstadoInvalido() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        assertThrows(RuntimeException.class, () -> reservaService.finalizarReserva(1L));
    }

    @Test
    void obtenerParaCliente_debeMapearReserva() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        assertEquals(1L, reservaService.obtenerParaCliente(1L).getIdReserva());
    }

    @Test
    void obtenerParaAdmin_toleraFallosDeIntegracion() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(inquilinosClient.obtenerInquilinoPorId(20L)).thenThrow(new RuntimeException("caído"));
        when(propiedadClient.obtenerPropiedadPorId(10L)).thenThrow(new RuntimeException("caído"));
        when(precioClient.obtenerPreciosPorPropiedad(10L)).thenThrow(new RuntimeException("caído"));

        ReservaDetalleResponse resultado = reservaService.obtenerParaAdmin(1L);

        assertEquals(1L, resultado.getIdReserva());
    }

    // ─── INQUILINO DE LA SESIÓN (B-11) ─────────────────────────────────────────

    @Test
    void idInquilinoPorCorreo_loBuscaEnMsInquilinos() {
        InquilinoDTO perfil = new InquilinoDTO();
        perfil.setIdInquilino(20L);
        when(inquilinosClient.obtenerInquilinoPorEmail("inquilino@duoc.cl")).thenReturn(perfil);

        assertEquals(20L, reservaService.idInquilinoPorCorreo("inquilino@duoc.cl"));
    }

    @Test
    void validarDuenioDeReserva_conReservaDeOtroInquilino_lanzaAccesoDenegado() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva)); // es del inquilino 20

        assertThrows(AccesoDenegadoException.class,
                () -> reservaService.validarDuenioDeReserva(1L, 99L));
    }

    @Test
    void validarDuenioDeReserva_conSuPropiaReserva_noLanzaError() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertDoesNotThrow(() -> reservaService.validarDuenioDeReserva(1L, 20L));
    }
}
