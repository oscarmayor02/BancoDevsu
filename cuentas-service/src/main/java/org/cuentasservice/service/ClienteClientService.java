package org.cuentasservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClienteClientService {

    private final RestTemplate restTemplate;

    @Value("${cliente.service.url:http://cliente-service:8081}")
    private String clienteServiceUrl;

    public void validarClienteExiste(String clienteId) {
        try {
            String url = clienteServiceUrl + "/api/clientes/" + clienteId;
            restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Cliente no encontrado: " + clienteId);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar cliente: " + e.getMessage());
        }
    }

    public String obtenerNombreCliente(String clienteId) {
        try {
            String url = clienteServiceUrl + "/api/clientes/" + clienteId;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response != null ? (String) response.get("nombre") : "Desconocido";
        } catch (Exception e) {
            return "Cliente no disponible";
        }
    }
}