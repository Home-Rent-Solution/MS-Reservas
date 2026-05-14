package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.InquilinoClient;
import com.HomeRentSolution.ms_reservas.client.PagosClient;
import com.HomeRentSolution.ms_reservas.client.PropiedadesClient;
import com.HomeRentSolution.ms_reservas.client.PrecioClient;
import com.HomeRentSolution.ms_reservas.dto.ReservaPrecioDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPagosDTO;
import com.HomeRentSolution.ms_reservas.dto.*;
import com.HomeRentSolution.ms_reservas.exception.PropiedadNoDisponibleException;
import com.HomeRentSolution.ms_reservas.exception.RecursoNoEncontradoException;
import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PropiedadesClient propiedadClient;
    private final ReservaRepository reservaRepository;
    private final PrecioClient precioClient;
    private final InquilinoClient inquilinosClient;
    private final PagosClient pagosClient;

    private List<ReservaFiltrosDTO> buscarConFiltro(ReservaFiltrosDTO filtrosDTO) {

        List<ReservaPropiedadDTO> todasPropiedades = propiedadClient.obtenerTodas();

        // Marca disponibilidad sobre la lista según el rango de fechas
        List<Reserva> reservas = reservaRepository.findAll();
        completarDisponibilidad(reservas, todasPropiedades, filtrosDTO.getFechaInicio(), filtrosDTO.getFechaFin());

        List<ReservaPropiedadDTO> propiedadesDisponibles = todasPropiedades.stream()
                .filter(p -> Boolean.TRUE.equals(p.isDisponible()))
                .filter(p -> cumpleUbicacion(p, filtrosDTO.getUbicacion()))
                .filter(p -> cumpleRangoPrecio(p, filtrosDTO.getPrecioMin(), filtrosDTO.getPrecioMax()))
                .collect(Collectors.toList());

        // Asocia precios a las propiedades disponibles
        return obtenerPrecios(propiedadesDisponibles);

    }

    public ReservaDTO crearReserva(ReservaAdmDTO dto) {

        // 1. Validar disponibilidad
        validarDisponibilidad(dto.getIdPropiedad());

        // 2. Construir y guardar la reserva
        Reserva reserva = new Reserva();
        reserva.setIdPropiedad(dto.getIdPropiedad());
        reserva.setIdInquilino(dto.getIdInquilino());
        reserva.setFechaInicio(dto.getFechaInicio().atStartOfDay());
        reserva.setFechaFin(dto.getFechaFin().atStartOfDay());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        reserva.setMontoTotal(BigDecimal.ZERO);

        reservaRepository.save(reserva);

        // 3. Generar pago en MS-Pagos
        generarPago(reserva);

        // 4. Retorno manual
        ReservaDTO response = new ReservaDTO();
        response.setIdReserva(reserva.getIdReserva());
        response.setIdPropiedad(reserva.getIdPropiedad());
        response.setIdInquilino(reserva.getIdInquilino());
        response.setEstado(reserva.getEstado());
        response.setFechaReserva(reserva.getFechaReserva());
        response.setFechaInicio(reserva.getFechaInicio());
        response.setFechaFin(reserva.getFechaFin());
        response.setFechaLimitesPago(reserva.getFechaLimitesPago());
        response.setMontoTotal(reserva.getMontoTotal());

        return response;
    }

    private boolean cumpleEstado(ReservaPropiedadDTO p, EstadoPropiedad estadoFiltro) {
        if (estadoFiltro == null) return true;
        return p.getEstadoPropiedad().equals(estadoFiltro);
    }

    private boolean cumpleUbicacion(ReservaPropiedadDTO p, String ubicacionFiltro) {
        if (ubicacionFiltro == null || ubicacionFiltro.isEmpty()) return true;
        return p.getUbicacion().equalsIgnoreCase(ubicacionFiltro);
    }

    private boolean cumpleRangoPrecio(ReservaPropiedadDTO p, BigDecimal min, BigDecimal max) {
        boolean cumpleMin = (min == null) || (p.getPrecio().compareTo(min) >= 0);
        boolean cumpleMax = (max == null) || (p.getPrecio().compareTo(max) <= 0);

        return cumpleMin && cumpleMax;
    }

    private void completarDisponibilidad(List<Reserva> reservas,
                                         List<ReservaPropiedadDTO> propiedades,
                                         LocalDateTime inicio,
                                         LocalDateTime fin) {

        if (inicio == null || fin == null) return;

        // IDs de propiedades que tienen reservas ACTIVAS en ese rango
        Set<Long> propiedadesOcupadas = reservas.stream()
                .filter(r -> estaEnRango(r, inicio, fin))
                .filter(r -> r.getEstado() != null && r.getEstado() != EstadoReserva.CANCELADA)
                .map(Reserva::getIdPropiedad)
                .collect(Collectors.toSet());

        // Las que no están ocupadas → DISPONIBLE
        propiedades.forEach(p -> {
            if (!propiedadesOcupadas.contains(p.getIdPropiedad().longValue())) {
                p.setEstadoPropiedad(EstadoPropiedad.DISPONIBLE);
                p.setDisponible(true);
            }
        });
    }

    private boolean estaEnRango(Reserva reserva, LocalDateTime inicio, LocalDateTime fin) {
        return !reserva.getFechaInicio().isAfter(fin)
                && !reserva.getFechaFin().isBefore(inicio);
    }


    private boolean validarDisponibilidad(Long idPropiedad){

        ReservaPropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(idPropiedad);

            if (propiedad == null) {
                throw new RecursoNoEncontradoException("La propiedad no fue encontrada.");
            }

            if (!Boolean.TRUE.equals(propiedad.isDisponible())) {
                throw new PropiedadNoDisponibleException(
                        "La propiedad con ID " + idPropiedad + " ya no está disponible."
                );
            }

            return true;
    }

    private List<ReservaFiltrosDTO> obtenerPrecios(List<ReservaPropiedadDTO> propiedadesDisponibles) {

        return propiedadesDisponibles.stream()
                .map(propiedad -> {
                    ReservaFiltrosDTO dto = new ReservaFiltrosDTO();
                    dto.setIdPropiedad(propiedad.getIdPropiedad().longValue());
                    dto.setUbicacion(propiedad.getUbicacion());
                    dto.setEstadoPropiedad(propiedad.getEstadoPropiedad());

                    try {
                        ReservaPrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(
                                propiedad.getIdPropiedad().longValue()
                        );
                        if (precio != null) {
                            dto.setTemporada(precio.getTemporada());
                            dto.setMultiplicador(precio.getMultiplicador());
                        }
                    } catch (Exception e) {
                        // Si precios falla, la propiedad se muestra sin precio
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void generarPago(Reserva reserva) {

        // 1. Obtener precio base de la propiedad
        ReservaPropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(
                reserva.getIdPropiedad()
        );

        // 2. Obtener multiplicador de MS-Precios
        ReservaPrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(
                reserva.getIdPropiedad()
        );

        // 3. Calcular número de días
        long numeroDias = ChronoUnit.DAYS.between(
                reserva.getFechaInicio(),
                reserva.getFechaFin()
        );

        // 4. Calcular monto total
        BigDecimal montoCalculado = propiedad.getPrecio()
                .multiply(BigDecimal.valueOf(precio.getMultiplicador()))
                .multiply(BigDecimal.valueOf(numeroDias));

        // 5.
        ReservaPagosDTO pagoDTO = new ReservaPagosDTO();
        pagoDTO.setIdReserva(reserva.getIdReserva());
        pagoDTO.setIdInquilino(reserva.getIdInquilino());
        pagoDTO.setIdPropiedad(reserva.getIdPropiedad());
        pagoDTO.setMontoTotal(montoCalculado);
        pagoDTO.setFechaVencimiento(reserva.getFechaLimitesPago());
        pagoDTO.setNumeroCuotas(1);

        pagosClient.crearPago(pagoDTO);
    }


    //buscarDisponibilidad() *
    //crearReserva *
    //private boolean validarDisponibilidad()*
    //private obtenerPrecios()*
    //private void generarPago()
    //public void cancelarReserva()
    //public void confirmarReserva()
    //public obtenerReservasCliente()
    //private void enviarConfirmacion()
}

