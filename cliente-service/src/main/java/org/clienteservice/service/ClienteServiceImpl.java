package org.clienteservice.service;


import lombok.RequiredArgsConstructor;
import org.clienteservice.dto.ClienteDTO;
import org.clienteservice.entity.Cliente;
import org.clienteservice.exception.ClienteNotFoundException;
import org.clienteservice.exception.DuplicateClienteException;
import org.clienteservice.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteDTO crearCliente(ClienteDTO dto) {
        if (clienteRepository.existsByClienteId(dto.getClienteId())) {
            throw new DuplicateClienteException("Cliente con ID " + dto.getClienteId() + " ya existe");
        }

        Cliente cliente = convertirDTOaEntidad(dto);
        Cliente guardado = clienteRepository.save(cliente);
        return convertirEntidadaDTO(guardado);
    }

    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado: " + clienteId));
        return convertirEntidadaDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteDTO actualizarCliente(String clienteId, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado: " + clienteId));

        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setContrasena(dto.getContrasena());
        cliente.setEstado(dto.getEstado());

        Cliente actualizado = clienteRepository.save(cliente);
        return convertirEntidadaDTO(actualizado);
    }

    @Transactional
    public void eliminarCliente(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado: " + clienteId));
        clienteRepository.delete(cliente);
    }

    private ClienteDTO convertirEntidadaDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getIdentificacion(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getClienteId(),
                "****", // No exponemos la contraseña
                cliente.getEstado()
        );
    }

    private Cliente convertirDTOaEntidad(ClienteDTO dto) {
        return new Cliente(
                dto.getIdentificacion(),
                dto.getNombre(),
                dto.getGenero(),
                dto.getEdad(),
                dto.getDireccion(),
                dto.getTelefono(),
                dto.getClienteId(),
                dto.getContrasena(),
                dto.getEstado()
        );
    }
}