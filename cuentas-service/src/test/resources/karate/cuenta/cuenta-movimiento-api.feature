Feature: API de Cuentas y Movimientos - Pruebas de Integración

  Background:
    * url 'http://localhost:8082'
    * def cuentasPath = '/api/cuentas'
    * def movimientosPath = '/api/movimientos'
    * def reportesPath = '/api/reportes'

  Scenario: Crear una cuenta exitosamente
    * def numeroCuentaUnico = '9999' + java.lang.System.currentTimeMillis()
    Given path cuentasPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuentaUnico)",
        "tipo": "Ahorros",
        "saldoInicial": 1000,
        "estado": true,
        "clienteId": "jose123"
      }
      """
    When method POST
    Then status 201
    And match response.numeroCuenta == numeroCuentaUnico
    And match response.tipo == 'Ahorros'
    And match response.saldoInicial == 1000

  Scenario: Obtener todas las cuentas
    Given path cuentasPath
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Flujo completo - Cuenta con Movimientos (Depósito y Retiro)
    * def numeroCuenta = '8888' + java.lang.System.currentTimeMillis()

    # 1. Crear cuenta con saldo inicial de 2000
    Given path cuentasPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuenta)",
        "tipo": "Corriente",
        "saldoInicial": 2000,
        "estado": true,
        "clienteId": "jose123"
      }
      """
    When method POST
    Then status 201
    And match response.saldoInicial == 2000

    # 2. Realizar depósito de 500
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuenta)",
        "movimiento": 500
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == 500
    And match response.saldoDisponible == 2500

    # 3. Realizar retiro de 800
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuenta)",
        "movimiento": -800
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == -800
    And match response.saldoDisponible == 1700

    # 4. Obtener movimientos de la cuenta
    Given path movimientosPath + '/cuenta/' + numeroCuenta
    When method GET
    Then status 200
    And match response == '#[2]'
    And match response[0].saldoDisponible == 1700
    And match response[1].saldoDisponible == 2500

  Scenario: Validar saldo insuficiente
    * def numeroCuenta = '7777' + java.lang.System.currentTimeMillis()

    # Crear cuenta con saldo de 100
    Given path cuentasPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuenta)",
        "tipo": "Ahorros",
        "saldoInicial": 100,
        "estado": true,
        "clienteId": "jose123"
      }
      """
    When method POST
    Then status 201

    # Intentar retirar más del saldo disponible
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuenta)",
        "movimiento": -500
      }
      """
    When method POST
    Then status 400
    And match response.mensaje == 'Saldo no disponible'

  Scenario: Casos de uso del documento - Movimientos específicos
    # Caso: Cuenta 478758 (Jose Lema) - Retiro de 575
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "478758",
        "movimiento": -575
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == -575
    And match response.saldoDisponible == 1425

  Scenario: Casos de uso del documento - Depósito
    # Caso: Cuenta 225487 (Marianela Montalvo) - Depósito de 600
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "225487",
        "movimiento": 600
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == 600
    And match response.saldoDisponible == 700

  Scenario: Casos de uso del documento - Depósito en cuenta con saldo 0
    # Caso: Cuenta 495878 (Juan Osorio) - Depósito de 150
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "495878",
        "movimiento": 150
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == 150
    And match response.saldoDisponible == 150

  Scenario: Casos de uso del documento - Retiro total
    # Caso: Cuenta 496825 (Marianela Montalvo) - Retiro de 540
    Given path movimientosPath
    And request
      """
      {
        "numeroCuenta": "496825",
        "movimiento": -540
      }
      """
    When method POST
    Then status 201
    And match response.movimiento == -540
    And match response.saldoDisponible == 0

  Scenario: Reporte de movimientos por fechas y cliente (F4)
    Given path reportesPath
    And param fechaInicio = '2022-02-01'
    And param fechaFin = '2022-02-28'
    And param cliente = 'marianela456'
    When method GET
    Then status 200
    And match response == '#[]'
    And match each response contains { fecha: '#notnull', cliente: '#string', numeroCuenta: '#string', tipo: '#string', movimiento: '#number', saldoDisponible: '#number' }

  Scenario: Obtener cuentas por cliente
    Given path cuentasPath + '/cliente/jose123'
    When method GET
    Then status 200
    And match response == '#[]'
    And match each response.clienteId == 'jose123'

  Scenario: Intentar crear cuenta con cliente inexistente
    * def numeroCuentaTemp = '6666' + java.lang.System.currentTimeMillis()
    Given path cuentasPath
    And request
      """
      {
        "numeroCuenta": "#(numeroCuentaTemp)",
        "tipo": "Ahorros",
        "saldoInicial": 1000,
        "estado": true,
        "clienteId": "cliente_no_existe_12345"
      }
      """
    When method POST
    Then status 500
    And match response.mensaje contains 'Cliente no encontrado'