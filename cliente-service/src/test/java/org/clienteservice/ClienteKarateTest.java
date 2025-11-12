package org.clienteservice;

import com.intuit.karate.junit5.Karate;

/**
 * Test Runner para ejecutar todas las pruebas de Karate del microservicio
 */
class ClienteKarateTest {

    @Karate.Test
    Karate testClientes() {
        return Karate.run("classpath:karate/cliente/cliente-api.feature");
    }
}