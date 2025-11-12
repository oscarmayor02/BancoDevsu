package org.cuentasservice.service;

import lombok.RequiredArgsConstructor;
import org.cuentasservice.dto.CuentaDTO;
import org.cuentasservice.entity.Cuenta;
import org.cuentasservice.exception.CuentaNotFoundException;
import org.cuentasservice.exception.DuplicateCuentaException;
import org.cuentasservice.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteClientService clienteClientService;

    @Transactional
    public CuentaDTO crearCuenta(CuentaDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new DuplicateCuentaException("Cuenta con número " + dto.getNumeroCuenta() + " ya existe");
        }

        // Validar que el cliente existe
        clienteClientService.validarClienteExiste(dto.getClienteId());

        Cuenta cuenta = convertirDTOaEntidad(dto);
        Cuenta guardada = cuentaRepository.save(cuenta);
        return convertirEntidadaDTO(guardada);
    }

    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada: " + numeroCuenta));
        return convertirEntidadaDTO(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll().stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasPorCliente(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CuentaDTO actualizarCuenta(String numeroCuenta, CuentaDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        cuenta.setTipo(dto.getTipo());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());

        Cuenta actualizada = cuentaRepository.save(cuenta);
        return convertirEntidadaDTO(actualizada);
    }

    @Transactional
    public void eliminarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada: " + numeroCuenta));
        cuentaRepository.delete(cuenta);
    }

    private CuentaDTO convertirEntidadaDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipo(cuenta.getTipo());
        dto.setSaldoInicial(cuenta.getSaldoInicial());
        dto.setEstado(cuenta.getEstado());
        dto.setClienteId(cuenta.getClienteId());

        // Obtener nombre del cliente
        try {
            String nombreCliente = clienteClientService.obtenerNombreCliente(cuenta.getClienteId());
            dto.setClienteNombre(nombreCliente);
        } catch (Exception e) {
            dto.setClienteNombre("Cliente no disponible");
        }

        return dto;
    }

    private Cuenta convertirDTOaEntidad(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipo(dto.getTipo());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());
        return cuenta;
    }
}