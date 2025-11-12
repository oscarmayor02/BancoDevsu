package org.cuentasservice.service;

import lombok.RequiredArgsConstructor;
import org.cuentasservice.dto.ReporteMovimientoDTO;
import org.cuentasservice.entity.Movimiento;
import org.cuentasservice.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final ClienteClientService clienteClientService;

    @Transactional(readOnly = true)
    public List<ReporteMovimientoDTO> generarReportePorFechasYCliente(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String clienteId) {

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository
                .findByClienteIdAndFechaBetween(clienteId, inicio, fin);

        String nombreCliente = clienteClientService.obtenerNombreCliente(clienteId);

        return movimientos.stream()
                .map(m -> convertirAReporte(m, nombreCliente))
                .collect(Collectors.toList());
    }

    private ReporteMovimientoDTO convertirAReporte(Movimiento m, String nombreCliente) {
        ReporteMovimientoDTO dto = new ReporteMovimientoDTO();
        dto.setFecha(m.getFecha());
        dto.setCliente(nombreCliente);
        dto.setNumeroCuenta(m.getNumeroCuenta());
        dto.setTipo(m.getTipo());
        dto.setSaldoInicial(m.getSaldoInicial());
        dto.setEstado(m.getEstado());
        dto.setMovimiento(m.getMovimiento());
        dto.setSaldoDisponible(m.getSaldoDisponible());
        return dto;
    }
}