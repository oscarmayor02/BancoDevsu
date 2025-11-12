package org.clienteservice;
import org.clienteservice.dto.ClienteDTO;
import org.clienteservice.entity.Cliente;
import org.clienteservice.exception.ClienteNotFoundException;
import org.clienteservice.exception.DuplicateClienteException;
import org.clienteservice.repository.ClienteRepository;
import org.clienteservice.service.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(
                1L,
                "Jose Lema",
                "Masculino",
                30,
                "Otavalo sn y principal",
                "098254785",
                "jose123",
                "1234",
                true
        );

        clienteDTO = new ClienteDTO(
                null,
                "Jose Lema",
                "Masculino",
                30,
                "Otavalo sn y principal",
                "098254785",
                "jose123",
                "1234",
                true
        );
    }

    @Test
    void testCrearCliente_Exitoso() {
        when(clienteRepository.existsByClienteId(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteDTO resultado = clienteService.crearCliente(clienteDTO);

        assertNotNull(resultado);
        assertEquals("Jose Lema", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testCrearCliente_ClienteDuplicado() {
        when(clienteRepository.existsByClienteId(anyString())).thenReturn(true);

        assertThrows(DuplicateClienteException.class, () -> {
            clienteService.crearCliente(clienteDTO);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testObtenerClientePorId_Exitoso() {
        when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = clienteService.obtenerClientePorId("jose123");

        assertNotNull(resultado);
        assertEquals("Jose Lema", resultado.getNombre());
        assertEquals("jose123", resultado.getClienteId());
    }

    @Test
    void testObtenerClientePorId_NoEncontrado() {
        when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.obtenerClientePorId("inexistente");
        });
    }

    @Test
    void testActualizarCliente_Exitoso() {
        when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteDTO.setNombre("Jose Lema Actualizado");
        ClienteDTO resultado = clienteService.actualizarCliente("jose123", clienteDTO);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testEliminarCliente_Exitoso() {
        when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).delete(any(Cliente.class));

        assertDoesNotThrow(() -> clienteService.eliminarCliente("jose123"));
        verify(clienteRepository, times(1)).delete(any(Cliente.class));
    }
}