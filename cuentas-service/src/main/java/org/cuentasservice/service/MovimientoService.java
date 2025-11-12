package org.cuentasservice.service;

import lombok.RequiredArgsConstructor;
import org.cuentasservice.dto.MovimientoDTO;
import org.cuentasservice.entity.Cuenta;
import org.cuentasservice.entity.Movimiento;
import org.cuentasservice.exception.CuentaNotFoundException;
import org.cuentasservice.exception.SaldoInsuficienteException;
import org.cuentasservice.repository.CuentaRepository;
import org.cuentasservice.repository.MovimientoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ClienteClientService clienteClientService;

    @Transactional
    public MovimientoDTO registrarMovimiento(MovimientoDTO dto) {
        // Validar que la cuenta existe
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(dto.getNumeroCuenta())
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada: " + dto.getNumeroCuenta()));

        // Obtener último saldo
        BigDecimal saldoActual = obtenerUltimoSaldo(dto.getNumeroCuenta(), cuenta.getSaldoInicial());

        // Validar saldo suficiente para retiros
        if (dto.getMovimiento().compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal nuevoSaldo = saldoActual.add(dto.getMovimiento());
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
        }

        // Calcular nuevo saldo
        BigDecimal nuevoSaldo = saldoActual.add(dto.getMovimiento());

        // Crear movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setNumeroCuenta(dto.getNumeroCuenta());
        movimiento.setTipo(cuenta.getTipo());
        movimiento.setSaldoInicial(saldoActual);
        movimiento.setEstado(true);
        movimiento.setMovimiento(dto.getMovimiento());
        movimiento.setSaldoDisponible(nuevoSaldo);

        Movimiento guardado = movimientoRepository.save(movimiento);

        // Enviar notificación asíncrona (RabbitMQ)
        enviarNotificacionMovimiento(guardado, cuenta.getClienteId());

        return convertirEntidadaDTO(guardado, cuenta.getClienteId());
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerMovimientosPorCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        return movimientoRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta)
                .stream()
                .map(m -> convertirEntidadaDTO(m, cuenta.getClienteId()))
                .collect(Collectors.toList());
    }

    private BigDecimal obtenerUltimoSaldo(String numeroCuenta, BigDecimal saldoInicial) {
        return movimientoRepository.findFirstByNumeroCuentaOrderByFechaDesc(numeroCuenta)
                .map(Movimiento::getSaldoDisponible)
                .orElse(saldoInicial);
    }

    private void enviarNotificacionMovimiento(Movimiento movimiento, String clienteId) {
        try {
            String mensaje = String.format(
                    "Movimiento registrado - Cuenta: %s, Monto: %s, Nuevo Saldo: %s",
                    movimiento.getNumeroCuenta(),
                    movimiento.getMovimiento(),
                    movimiento.getSaldoDisponible()
            );
            rabbitTemplate.convertAndSend("movimientos.exchange", "movimiento.registrado", mensaje);
        } catch (Exception e) {
            // Log error pero no fallar la transacción
            System.err.println("Error enviando notificación: " + e.getMessage());
        }
    }

    private MovimientoDTO convertirEntidadaDTO(Movimiento movimiento, String clienteId) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setFecha(movimiento.getFecha());
        dto.setNumeroCuenta(movimiento.getNumeroCuenta());
        dto.setTipo(movimiento.getTipo());
        dto.setSaldoInicial(movimiento.getSaldoInicial());
        dto.setEstado(movimiento.getEstado());
        dto.setMovimiento(movimiento.getMovimiento());
        dto.setSaldoDisponible(movimiento.getSaldoDisponible());

        // Obtener nombre del cliente
        try {
            String nombreCliente = clienteClientService.obtenerNombreCliente(clienteId);
            dto.setClienteNombre(nombreCliente);
        } catch (Exception e) {
            dto.setClienteNombre("Cliente no disponible");
        }

        return dto;
    }
}