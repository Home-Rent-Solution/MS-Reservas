package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.*;
import com.HomeRentSolution.ms_reservas.dto.ms.*;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltrosDTO;
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
    private limpiezaClient limpiezaClient;

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

    public ReservaDetalleResponse crearReserva(ReservaResponse dto) {

        // 1. Validar disponibilidad
        validarDisponibilidad(dto.getIdPropiedad());

        // 2. Construir y guardar la reserva
        Reserva reserva = new Reserva();
        reserva.setIdPropiedad(dto.getIdPropiedad());
        reserva.setIdInquilino(dto.getIdInquilino());
        reserva.setFechaInicio(dto.getFechaInicio().atStartOfDay());
        reserva.setFechaFin(dto.getFechaFin().atStartOfDay());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        reserva.setMontoTotal(BigDecimal.ZERO);

        reservaRepository.save(reserva);

        // 3. Generar pago en MS-Pagos
        generarPago(reserva);

        // 4. Retorno manual
        ReservaDetalleResponse response = new ReservaDetalleResponse();
        response.setIdReserva(reserva.getIdReserva());
        response.setIdPropiedad(reserva.getIdPropiedad());
        response.setIdInquilino(reserva.getIdInquilino());
        response.setEstado(reserva.getEstadoReserva());
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
                .filter(r -> r.getEstadoReserva() != null && r.getEstadoReserva() != EstadoReserva.CANCELADA)
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
        BigDecimal montoTotal = propiedad.getPrecio()
                .multiply(BigDecimal.valueOf(precio.getMultiplicador()))
                .multiply(BigDecimal.valueOf(numeroDias));

        // 5. Calcular anticipo (50% del total para reservar)
        BigDecimal montoAnticipo = montoTotal.multiply(new BigDecimal("0.5"));

        // 6. Actualizar montoTotal en la reserva
        reserva.setMontoTotal(montoTotal);
        reservaRepository.save(reserva);

        // 7. Enviar a MS-Pagos
        ReservaPagosDTO pagoDTO = new ReservaPagosDTO();
        pagoDTO.setIdReserva(reserva.getIdReserva());
        pagoDTO.setIdInquilino(reserva.getIdInquilino());
        pagoDTO.setIdPropiedad(reserva.getIdPropiedad());
        pagoDTO.setMontoTotal(montoTotal);           // monto total de la estadía
        pagoDTO.setMontoAnticipo(montoAnticipo);      // 50% que se paga para reservar
        pagoDTO.setFechaVencimiento(reserva.getFechaLimitesPago());
        pagoDTO.setNumeroCuotas(1);

        pagosClient.crearPago(pagoDTO);
    }
    public void cancelarReserva(Long idReserva, String motivo, BigDecimal montoReembolso) {

        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        // 2. Validar que no esté ya cancelada o finalizada
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA ||
                reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new RuntimeException(
                    "La reserva no puede cancelarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        // 3. Calcular horas restantes antes de fechaInicio
        long horasRestantes = ChronoUnit.HOURS.between(
                LocalDateTime.now(),
                reserva.getFechaInicio()
        );

        // 4. Determinar porcentaje de reembolso
        BigDecimal porcentajeReembolso;

        if (horasRestantes > 72) {
            porcentajeReembolso = BigDecimal.ONE;
        } else if (horasRestantes > 24) {
            porcentajeReembolso = new BigDecimal("0.5");
        } else {
            porcentajeReembolso = BigDecimal.ZERO;
        }

        BigDecimal monto = reserva.getMontoTotal()
                .multiply(porcentajeReembolso);

        // 5. Cambiar estado a CANCELADA
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // 6. Notificar a MS-Pagos con el monto a reembolsar
        pagosClient.cancelarPago(idReserva, motivo, monto);

        // 7. Agendar limpieza inmediata (reemplaza propiedadClient.cambiarEstado())
        // MS-Limpieza se encargará de marcar la propiedad como DISPONIBLE al terminar
        agendarLimpieza(reserva, LocalDateTime.now());
    }

    public ReservaDetalleResponse confirmarReserva(Long idReserva) {

        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        // 2. Validar que esté en estado PENDIENTE
        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException(
                    "La reserva no puede confirmarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        // 3. Validar que no haya vencido el plazo de pago
        if (LocalDateTime.now().isAfter(reserva.getFechaLimitesPago())) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);
            throw new RuntimeException(
                    "La reserva venció su fecha límite de pago y fue cancelada automáticamente."
            );
        }

        // 4. Confirmar el pago en MS-Pagos
        pagosClient.confirmarPago(idReserva);

        // 5. Cambiar estado a CONFIRMADA
        reserva.setEstadoReserva(EstadoReserva.COMPLETADA);
        reservaRepository.save(reserva);

        // 6. Actualizar disponibilidad en MS-Propiedades
        propiedadClient.cambiarEstado(reserva.getIdPropiedad());

        // 7. Retorno manual (mismo patrón que crearReserva)
        ReservaDetalleResponse response = new ReservaDetalleResponse();
        response.setIdReserva(reserva.getIdReserva());
        response.setIdPropiedad(reserva.getIdPropiedad());
        response.setIdInquilino(reserva.getIdInquilino());
        response.setEstado(reserva.getEstadoReserva());
        response.setFechaReserva(reserva.getFechaReserva());
        response.setFechaInicio(reserva.getFechaInicio());
        response.setFechaFin(reserva.getFechaFin());
        response.setFechaLimitesPago(reserva.getFechaLimitesPago());
        response.setMontoTotal(reserva.getMontoTotal());

        return response;
    }

    public List<ReservaFiltrosDTO> obtenerReservasCliente(Long idInquilino,
                                                          LocalDate fechaInicio,
                                                          LocalDate fechaFin) {

        // 1. Validar que el inquilino existe
        ReservaInquilinoDTO inquilino = inquilinosClient.obtenerInquilinoPorId(idInquilino);
        if (inquilino == null) {
            throw new RecursoNoEncontradoException(
                    "Inquilino no encontrado con ID: " + idInquilino
            );
        }

        // 2. Obtener todas las reservas del inquilino
        List<Reserva> reservas = reservaRepository.findByIdInquilino(idInquilino);

        // 3. Filtrar por rango de fechas si se proporcionan
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atStartOfDay();
            reservas = reservas.stream()
                    .filter(r -> estaEnRango(r, inicio, fin))
                    .collect(Collectors.toList());
        }

        // 4. Enriquecer cada reserva con propiedad, precio e inquilino
        return reservas.stream()
                .map(reserva -> enriquecerReserva(reserva, inquilino))
                .collect(Collectors.toList());
    }

    private ReservaFiltrosDTO enriquecerReserva(Reserva reserva, ReservaInquilinoDTO inquilino) {

        ReservaFiltrosDTO dto = new ReservaFiltrosDTO();

        // Datos base de la reserva
        dto.setIdReserva(reserva.getIdReserva());
        dto.setEstadoReserva(reserva.getEstadoReserva());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setFechaLimitesPago(reserva.getFechaLimitesPago());
        dto.setMontoTotal(reserva.getMontoTotal());

        // Datos del inquilino (una sola llamada reutilizada)
        dto.setIdInquilino(inquilino.getIdInquilino());
        dto.setNombre(inquilino.getNombre());
        dto.setEmail(inquilino.getEmail());

        // Datos de la propiedad
        try {
            ReservaPropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(
                    reserva.getIdPropiedad()
            );
            if (propiedad != null) {
                dto.setIdPropiedad(propiedad.getIdPropiedad().longValue());
                dto.setUbicacion(propiedad.getUbicacion());
                dto.setEstadoPropiedad(propiedad.getEstadoPropiedad());
                dto.setPrecioBase(propiedad.getPrecio());
            }
        } catch (Exception e) {
            // Si MS-Propiedades falla, se muestra sin datos de propiedad
        }

        // Datos del precio/temporada
        try {
            ReservaPrecioDTO precio = precioClient.obtenerPrecioPorPropiedad(
                    reserva.getIdPropiedad()
            );
            if (precio != null) {
                dto.setTemporada(precio.getTemporada());
                dto.setMultiplicador(precio.getMultiplicador());
            }
        } catch (Exception e) {
            // Si MS-Precios falla, se muestra sin datos de precio
        }

        return dto;
    }

    private void enviarConfirmacion(Reserva reserva) {

        // 1. Obtener datos del inquilino
        ReservaInquilinoDTO inquilino = inquilinosClient.obtenerInquilinoPorId(
                reserva.getIdInquilino()
        );

        if (inquilino == null) {
            throw new RecursoNoEncontradoException(
                    "Inquilino no encontrado con ID: " + reserva.getIdInquilino()
            );
        }

        // 2. Construir el contenido del mensaje
        String contenido = String.format(
                "Estimado/a %s, su reserva #%d ha sido confirmada. " +
                        "Fecha inicio: %s | Fecha fin: %s | Monto total: $%.2f. " +
                        "Gracias por confiar en HomeRentSolution.",
                inquilino.getNombre(),
                reserva.getIdReserva(),
                reserva.getFechaInicio().toLocalDate(),
                reserva.getFechaFin().toLocalDate(),
                reserva.getMontoTotal()
        );

        // 3. Armar el DTO con la estructura de MS-Mensajería
        ReservaMensajeriaDTO mensajeDTO = new ReservaMensajeriaDTO();
        mensajeDTO.setFecha(LocalDateTime.now());
        mensajeDTO.setContenido(contenido);
        mensajeDTO.setIdEmisor(reserva.getIdReserva());    // quien origina
        mensajeDTO.setIdReceptor(reserva.getIdInquilino()); // quien recibe

        // 4. Enviar a MS-Mensajería y registrar en ReservaFiltrosDTO
        try {
            ReservaMensajeriaDTO respuesta = mensajeriaClient.enviarEmail(mensajeDTO);

            // 5. Registrar la respuesta en ReservaFiltrosDTO
            ReservaFiltrosDTO registro = new ReservaFiltrosDTO();
            registro.setIdMensaje(respuesta.getIdMensaje());   // ID generado por MS-Mensajería
            registro.setFecha(respuesta.getFecha());
            registro.setContenido(respuesta.getContenido());
            registro.setIdEmisor(respuesta.getIdEmisor());
            registro.setIdReceptor(respuesta.getIdReceptor());
            registro.setMontoTotal(reserva.getMontoTotal());
            registro.setFechaInicio(reserva.getFechaInicio());
            registro.setFechaFin(reserva.getFechaFin());

            log.info("[MS-Reservas] Confirmación enviada. idMensaje={} | idReceptor={} | fecha={}",
                    respuesta.getIdMensaje(),
                    respuesta.getIdReceptor(),
                    respuesta.getFecha()
            );

        } catch (Exception e) {
            // No interrumpe el flujo si MS-Mensajería falla
            log.error("[MS-Reservas] Error al enviar confirmación al inquilino ID: {}. Error: {}",
                    reserva.getIdInquilino(), e.getMessage());
        }
    }

    public ReservaFiltrosDTO finalizarReserva(Long idReserva) {

        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        // 2. Validar que esté CONFIRMADA
        if (reserva.getEstadoReserva() != EstadoReserva.COMPLETADA) {
            throw new RuntimeException(
                    "La reserva no puede finalizarse porque está en estado: " + reserva.getEstadoReserva()
            );
        }

        // 3. Cambiar estado a FINALIZADA
        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        reservaRepository.save(reserva);

        // 4. Agendar limpieza al fin de la reserva
        // MS-Limpieza se encargará de marcar la propiedad como DISPONIBLE al terminar
        ReservaFiltrosDTO response = agendarLimpieza(reserva, reserva.getFechaFin());

        return response;
    }
    private ReservaFiltrosDTO agendarLimpieza(Reserva reserva, LocalDateTime fechaProgramada) {
        // 1. Preparamos la petición para el microservicio de limpieza
        // Usamos los campos idPropiedad e idReserva que ya tienes en tu entidad Reserva
        ReservaLimpiezaDTO request = new ReservaLimpiezaDTO();
        request.setIdPropiedad(reserva.getIdPropiedad());
        request.setIdReserva(reserva.getIdReserva());
        request.setFechaProgramada(fechaProgramada);

        // 2. Llamada al MS-Limpieza a través del cliente Feign
        // Guardamos la respuesta que contiene el id de limpieza y el estado
        ReservaLimpiezaDTO responseMS = limpiezaClient.agendarLimpieza(request);

        // 3. Creamos el DTO de retorno que tus métodos esperan
        // Mapeamos los datos de la reserva local + los datos que vienen del microservicio
        ReservaFiltrosDTO dtoFinal = new ReservaFiltrosDTO();

        // Datos de tu entidad local
        dtoFinal.setIdReserva(reserva.getIdReserva());
        dtoFinal.setEstadoReserva(reserva.getEstadoReserva());
        dtoFinal.setMontoTotal(reserva.getMontoTotal());
        dtoFinal.setFechaInicio(reserva.getFechaInicio());
        dtoFinal.setFechaFin(reserva.getFechaFin());
        dtoFinal.setIdPropiedad(reserva.getIdPropiedad());

        // Datos que vienen de la respuesta del MS-Limpieza
        dtoFinal.setIdLimpieza(responseMS.getIdLimpieza());
        dtoFinal.setFechaProgramada(responseMS.getFechaProgramada());

        // Convertimos el Enum del MS-Limpieza a String para tu ReservaFiltrosDTO
        if (responseMS.getEstadoLimpieza() != null) {
            dtoFinal.setEstadoLimpieza(String.valueOf(responseMS.getEstadoLimpieza()));
        }

        return dtoFinal;
    }

}

