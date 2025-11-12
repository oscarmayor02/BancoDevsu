package org.cuentasservice.service;
import org.cuentasservice.dto.MovimientoDTO;
import org.cuentasservice.entity.Cuenta;
import org.cuentasservice.entity.Movimiento;
import org.cuentasservice.exception.CuentaNotFoundException;
import org.cuentasservice.exception.SaldoInsuficienteException;
import org.cuentasservice.repository.CuentaRepository;
import org.cuentasservice.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ClienteClientService clienteClientService;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuenta;
    private MovimientoDTO movimientoDTO;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipo("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("2000"));
        cuenta.setEstado(true);
        cuenta.setClienteId("jose123");

        movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta("478758");
        movimientoDTO.setMovimiento(new BigDecimal("-575"));
    }

    @Test
    void testRegistrarMovimiento_RetiroExitoso() {
        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByNumeroCuentaOrderByFechaDesc(anyString()))
                .thenReturn(Optional.empty());
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));
        when(clienteClientService.obtenerNombreCliente(anyString())).thenReturn("Jose Lema");

        MovimientoDTO resultado = movimientoService.registrarMovimiento(movimientoDTO);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("1425"), resultado.getSaldoDisponible());
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    void testRegistrarMovimiento_DepositoExitoso() {
        movimientoDTO.setMovimiento(new BigDecimal("600"));

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByNumeroCuentaOrderByFechaDesc(anyString()))
                .thenReturn(Optional.empty());
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));
        when(clienteClientService.obtenerNombreCliente(anyString())).thenReturn("Jose Lema");

        MovimientoDTO resultado = movimientoService.registrarMovimiento(movimientoDTO);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("2600"), resultado.getSaldoDisponible());
    }

    @Test
    void testRegistrarMovimiento_SaldoInsuficiente() {
        movimientoDTO.setMovimiento(new BigDecimal("-3000"));

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByNumeroCuentaOrderByFechaDesc(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(SaldoInsuficienteException.class, () -> {
            movimientoService.registrarMovimiento(movimientoDTO);
        });

        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void testRegistrarMovimiento_CuentaNoExiste() {
        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class, () -> {
            movimientoService.registrarMovimiento(movimientoDTO);
        });
    }
}