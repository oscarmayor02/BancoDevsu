package org.cuentasservice.controller;


import lombok.RequiredArgsConstructor;
import org.cuentasservice.dto.ReporteMovimientoDTO;
import org.cuentasservice.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteMovimientoDTO>> generarReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam String cliente) {

        List<ReporteMovimientoDTO> reporte = reporteService
                .generarReportePorFechasYCliente(fechaInicio, fechaFin, cliente);

        return ResponseEntity.ok(reporte);
    }
}