package org.clienteservice.controller;
import lombok.RequiredArgsConstructor;
import org.clienteservice.dto.ClienteDTO;
import org.clienteservice.service.ClienteServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO creado = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable String clienteId) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(clienteId);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes() {
        List<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable String clienteId,
            @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO actualizado = clienteService.actualizarCliente(clienteId, clienteDTO);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> actualizarClienteParcial(
            @PathVariable String clienteId,
            @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO actualizado = clienteService.actualizarCliente(clienteId, clienteDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String clienteId) {
        clienteService.eliminarCliente(clienteId);
        return ResponseEntity.noContent().build();
    }
}