package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.*;
import com.HomeRentSolution.ms_reservas.dto.ms.*;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.exception.PropiedadNoDisponibleException;
import com.HomeRentSolution.ms_reservas.exception.RecursoNoEncontradoException;
import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PropiedadesClient propiedadClient;
    private final ReservaRepository reservaRepository;
    private final PrecioClient precioClient;
    private final InquilinoClient inquilinosClient;
    private final PagosClient pagosClient;
    private final MensajeriaClient mensajeriaClient;
    @Autowired
    private final LimpiezaClient limpiezaClient;

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }


    public List<Reserva> buscarPorEstado(EstadoReserva estadoReserva) {
        return reservaRepository.findByEstadoReserva(estadoReserva);
    }


    public List<Reserva> buscarTodas() { return reservaRepository.findAll(); }

    // ─── MAPPER PRIVADO (evita duplicación) ───────────────────────────────────

    private ReservaResponse toResponse(Reserva reserva) {
        ReservaResponse r = new ReservaResponse();
        r.setIdReserva(reserva.getIdReserva());
        r.setIdPropiedad(reserva.getIdPropiedad());
        r.setIdInquilino(reserva.getIdInquilino());
        r.setEstado(reserva.getEstadoReserva());
        r.setFechaReserva(reserva.getFechaReserva());
        r.setFechaInicio(reserva.getFechaInicio());
        r.setFechaFin(reserva.getFechaFin());
        r.setFechaLimitesPago(reserva.getFechaLimitesPago());
        r.setMontoTotal(reserva.getMontoTotal());
        return r;
    }

    // ─── BUSCAR CON FILTRO ─────────────────────────────────────────────────────

    public List<ReservaDetalleResponse> buscarConFiltro(ReservaFiltroRequest filtro) {

        List<PropiedadDTO> todasPropiedades = propiedadClient.obtenerTodas();
        List<Reserva> reservas = reservaRepository.findAll();

        completarDisponibilidad(reservas, todasPropiedades, filtro.getFechaInicio(), filtro.getFechaFin());

        return todasPropiedades.stream()
                .filter(p -> Boolean.TRUE.equals(p.isDisponible()))
                .filter(p -> cumpleUbicacion(p, filtro.getUbicacion()))
                .filter(p -> cumpleRangoPrecio(p, filtro.getPrecioMin(), filtro.getPrecioMax()))
                .map(this::mapPropiedadADetalle)
                .collect(Collectors.toList());
    }

    private ReservaDetalleResponse mapPropiedadADetalle(PropiedadDTO propiedad) {
        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        dto.setIdPropiedad(propiedad.getIdPropiedad().longValue());
        dto.setUbicacion(propiedad.getUbicacion());
        dto.setEstadoPropiedad(propiedad.getEstadoPropiedad());
        dto.setPrecioBase(propiedad.getPrecio());

        try {
            PrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(propiedad.getIdPropiedad().longValue());
            if (precio != null) {
                dto.setTemporada(precio.getTemporada());
                dto.setMultiplicador(precio.getMultiplicador());
            }
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener precio para propiedad {}", propiedad.getIdPropiedad());
        }

        return dto;
    }

    // ─── CREAR RESERVA ─────────────────────────────────────────────────────────

    public ReservaResponse crearReserva(ReservaCrearRequest request) {

        validarDisponibilidad(request.getIdPropiedad());

        Reserva reserva = new Reserva();
        reserva.setIdPropiedad(request.getIdPropiedad());
        reserva.setIdInquilino(request.getIdInquilino());
        reserva.setFechaInicio(request.getFechaInicio().atStartOfDay());
        reserva.setFechaFin(request.getFechaFin().atStartOfDay());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        reserva.setMontoTotal(BigDecimal.ZERO);

        reservaRepository.save(reserva);
        generarPago(reserva);

        return toResponse(reserva);
    }

    // ─── CONFIRMAR RESERVA ─────────────────────────────────────────────────────

    public ReservaResponse confirmarReserva(Long idReserva) {

        Reserva reserva = buscarReservaOException(idReserva);

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException(
                    "La reserva no puede confirmarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        if (LocalDateTime.now().isAfter(reserva.getFechaLimitesPago())) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);
            throw new RuntimeException("La reserva venció su fecha límite de pago y fue cancelada automáticamente.");
        }

        pagosClient.confirmarPago(idReserva);

        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA); // corregido: era COMPLETADA
        reservaRepository.save(reserva);

        propiedadClient.cambiarEstado(reserva.getIdPropiedad());
        enviarConfirmacion(reserva); // ahora sí se llama

        return toResponse(reserva);
    }

    // ─── CANCELAR RESERVA ─────────────────────────────────────────────────────

    public ReservaResponse cancelarReserva(Long idReserva, String motivo) {

        Reserva reserva = buscarReservaOException(idReserva);

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA ||
                reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new RuntimeException(
                    "La reserva no puede cancelarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        BigDecimal montoReembolso = calcularReembolso(reserva);

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        pagosClient.cancelarPago(idReserva, motivo, montoReembolso);
        agendarLimpieza(reserva, LocalDateTime.now());

        return toResponse(reserva);
    }

    private BigDecimal calcularReembolso(Reserva reserva) {
        long horasRestantes = ChronoUnit.HOURS.between(LocalDateTime.now(), reserva.getFechaInicio());

        if (horasRestantes > 72) return reserva.getMontoTotal();
        if (horasRestantes > 24) return reserva.getMontoTotal().multiply(new BigDecimal("0.5"));
        return BigDecimal.ZERO;
    }

    // ─── FINALIZAR RESERVA ─────────────────────────────────────────────────────

    public ReservaDetalleResponse finalizarReserva(Long idReserva) {

        Reserva reserva = buscarReservaOException(idReserva);

        if (reserva.getEstadoReserva() != EstadoReserva.CONFIRMADA) {
            throw new RuntimeException(
                    "La reserva no puede finalizarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        reservaRepository.save(reserva);

        return agendarLimpieza(reserva, reserva.getFechaFin());
    }

    // ─── OBTENER RESERVAS DE CLIENTE ──────────────────────────────────────────

    public List<ReservaDetalleResponse> obtenerReservasCliente(Long idInquilino,
                                                               LocalDate fechaInicio,
                                                               LocalDate fechaFin) {
        InquilinoDTO inquilino = inquilinosClient.obtenerInquilinoPorId(idInquilino);
        if (inquilino == null) {
            throw new RecursoNoEncontradoException("Inquilino no encontrado con ID: " + idInquilino);
        }

        List<Reserva> reservas = reservaRepository.findByIdInquilino(idInquilino);

        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atStartOfDay();
            reservas = reservas.stream()
                    .filter(r -> estaEnRango(r, inicio, fin))
                    .collect(Collectors.toList());
        }

        return reservas.stream()
                .map(r -> enriquecerReserva(r, inquilino))
                .collect(Collectors.toList());
    }

    // ─── HELPERS PRIVADOS ─────────────────────────────────────────────────────

    private Reserva buscarReservaOException(Long idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));
    }

    private boolean validarDisponibilidad(Long idPropiedad) {
        PropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(idPropiedad);
        if (propiedad == null) throw new RecursoNoEncontradoException("La propiedad no fue encontrada.");
        if (!Boolean.TRUE.equals(propiedad.isDisponible())) {
            throw new PropiedadNoDisponibleException("La propiedad con ID " + idPropiedad + " ya no está disponible.");
        }
        return true;
    }

    private void generarPago(Reserva reserva) {
        PropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(reserva.getIdPropiedad());
        PrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(reserva.getIdPropiedad());

        long numeroDias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        BigDecimal montoTotal = propiedad.getPrecio()
                .multiply(BigDecimal.valueOf(precio.getMultiplicador()))
                .multiply(BigDecimal.valueOf(numeroDias));
        BigDecimal montoAnticipo = montoTotal.multiply(new BigDecimal("0.5"));

        reserva.setMontoTotal(montoTotal);
        reservaRepository.save(reserva);

        PagosDTO pagoDTO = new PagosDTO();
        pagoDTO.setIdReserva(reserva.getIdReserva());
        pagoDTO.setIdInquilino(reserva.getIdInquilino());
        pagoDTO.setIdPropiedad(reserva.getIdPropiedad());
        pagoDTO.setMontoTotal(montoTotal);
        pagoDTO.setMontoAnticipo(montoAnticipo);
        pagoDTO.setFechaVencimiento(reserva.getFechaLimitesPago());
        pagoDTO.setNumeroCuotas(1);

        pagosClient.crearPago(pagoDTO);
    }

    private void enviarConfirmacion(Reserva reserva) {
        InquilinoDTO inquilino = inquilinosClient.obtenerInquilinoPorId(reserva.getIdInquilino());
        if (inquilino == null) {
            throw new RecursoNoEncontradoException("Inquilino no encontrado con ID: " + reserva.getIdInquilino());
        }

        String contenido = String.format(
                "Estimado/a %s, su reserva #%d ha sido confirmada. " +
                        "Fecha inicio: %s | Fecha fin: %s | Monto total: $%.2f. " +
                        "Gracias por confiar en HomeRentSolution.",
                inquilino.getNombre(), reserva.getIdReserva(),
                reserva.getFechaInicio().toLocalDate(),
                reserva.getFechaFin().toLocalDate(),
                reserva.getMontoTotal()
        );

        MensajeriaDTO mensajeDTO = new MensajeriaDTO();
        mensajeDTO.setFecha(LocalDateTime.now());
        mensajeDTO.setContenido(contenido);
        mensajeDTO.setIdEmisor(reserva.getIdReserva());
        mensajeDTO.setIdReceptor(reserva.getIdInquilino());

        try {
            mensajeriaClient.enviarEmail(mensajeDTO);
            log.info("[MS-Reservas] Confirmación enviada al inquilino ID: {}", reserva.getIdInquilino());
        } catch (Exception e) {
            log.error("[MS-Reservas] Error al enviar confirmación al inquilino ID: {}. Error: {}",
                    reserva.getIdInquilino(), e.getMessage());
        }
    }

    private ReservaDetalleResponse agendarLimpieza(Reserva reserva, LocalDateTime fechaProgramada) {
        LimpiezaDTO request = new LimpiezaDTO();
        request.setIdPropiedad(reserva.getIdPropiedad());
        request.setIdReserva(reserva.getIdReserva());
        request.setFechaProgramada(fechaProgramada);

        LimpiezaDTO response = limpiezaClient.agendarLimpieza(request);

        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setEstadoReserva(reserva.getEstadoReserva());
        dto.setMontoTotal(reserva.getMontoTotal());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setIdPropiedad(reserva.getIdPropiedad());
        dto.setIdLimpieza(response.getIdLimpieza());
        dto.setFechaProgramada(response.getFechaProgramada());
        if (response.getEstadoLimpieza() != null) {
            dto.setEstadoLimpieza(String.valueOf(response.getEstadoLimpieza()));
        }

        return dto;
    }

    private ReservaDetalleResponse enriquecerReserva(Reserva reserva, InquilinoDTO inquilino) {
        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setEstadoReserva(reserva.getEstadoReserva());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setFechaLimitesPago(reserva.getFechaLimitesPago());
        dto.setMontoTotal(reserva.getMontoTotal());
        dto.setIdInquilino(inquilino.getIdInquilino());
        dto.setNombre(inquilino.getNombre());
        dto.setEmail(inquilino.getEmail());

        try {
            PropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(reserva.getIdPropiedad());
            if (propiedad != null) {
                dto.setIdPropiedad(propiedad.getIdPropiedad().longValue());
                dto.setUbicacion(propiedad.getUbicacion());
                dto.setEstadoPropiedad(propiedad.getEstadoPropiedad());
                dto.setPrecioBase(propiedad.getPrecio());
            }
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener propiedad {} para reserva {}",
                    reserva.getIdPropiedad(), reserva.getIdReserva());
        }

        try {
            PrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(reserva.getIdPropiedad());
            if (precio != null) {
                dto.setTemporada(precio.getTemporada());
                dto.setMultiplicador(precio.getMultiplicador());
            }
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener precio para propiedad {}", reserva.getIdPropiedad());
        }

        return dto;
    }

    private void completarDisponibilidad(List<Reserva> reservas, List<PropiedadDTO> propiedades,
                                         LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return;

        Set<Long> propiedadesOcupadas = reservas.stream()
                .filter(r -> estaEnRango(r, inicio, fin))
                .filter(r -> r.getEstadoReserva() != null && r.getEstadoReserva() != EstadoReserva.CANCELADA)
                .map(Reserva::getIdPropiedad)
                .collect(Collectors.toSet());

        propiedades.forEach(p -> {
            if (!propiedadesOcupadas.contains(p.getIdPropiedad().longValue())) {
                p.setEstadoPropiedad(EstadoPropiedad.DISPONIBLE);
                p.setDisponible(true);
            }
        });
    }

    private boolean estaEnRango(Reserva reserva, LocalDateTime inicio, LocalDateTime fin) {
        return !reserva.getFechaInicio().isAfter(fin) && !reserva.getFechaFin().isBefore(inicio);
    }

    private boolean cumpleUbicacion(PropiedadDTO p, String ubicacion) {
        return ubicacion == null || ubicacion.isEmpty() || p.getUbicacion().equalsIgnoreCase(ubicacion);
    }

    private boolean cumpleRangoPrecio(PropiedadDTO p, BigDecimal min, BigDecimal max) {
        boolean cumpleMin = (min == null) || (p.getPrecio().compareTo(min) >= 0);
        boolean cumpleMax = (max == null) || (p.getPrecio().compareTo(max) <= 0);
        return cumpleMin && cumpleMax;
    }

    // ─── VISTA CLIENTE (inquilino) ────────────────────────────────────────────
// Muestra solo los datos relevantes para el inquilino: su reserva, fechas, monto, estado
    public ReservaResponse obtenerParaCliente(Long idReserva) {
        Reserva reserva = buscarReservaOException(idReserva);
        return toResponse(reserva);
    }

    // ─── VISTA ADMIN ──────────────────────────────────────────────────────────
// Muestra todo: datos de propiedad, precio, temporada, inquilino, limpieza
    public ReservaDetalleResponse obtenerParaAdmin(Long idReserva) {
        Reserva reserva = buscarReservaOException(idReserva);

        // Reutilizamos obtenerInquilino para enriquecer
        InquilinoDTO inquilino = null;
        try {
            inquilino = inquilinosClient.obtenerInquilinoPorId(reserva.getIdInquilino());
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener inquilino {} para admin", reserva.getIdInquilino());
        }

        // Si no hay inquilino, igual mostramos la reserva con los datos que haya
        InquilinoDTO inquilinoFinal = (inquilino != null) ? inquilino : new InquilinoDTO();
        return enriquecerReserva(reserva, inquilinoFinal);
    }


}
