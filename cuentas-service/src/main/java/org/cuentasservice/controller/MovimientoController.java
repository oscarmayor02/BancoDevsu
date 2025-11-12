package org.cuentasservice.controller;
import lombok.RequiredArgsConstructor;
import org.cuentasservice.dto.MovimientoDTO;
import org.cuentasservice.service.MovimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO registrado = movimientoService.registrarMovimiento(movimientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientosPorCuenta(
            @PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientosPorCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }
}
