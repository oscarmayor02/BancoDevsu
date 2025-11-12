package org.cuentasservice;

import com.intuit.karate.junit5.Karate;

/**
 * Test Runner para ejecutar todas las pruebas de Karate del microservicio Cuenta
 */
class CuentaKarateTest {

    @Karate.Test
    Karate testCuentas() {
        return Karate.run("classpath:karate/cuenta/cuenta-movimiento-api.feature").relativeTo(getClass());
    }
}