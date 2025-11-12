Feature: API de Clientes - Pruebas de Integración

  Background:
    * url 'http://localhost:8081'
    * def basePath = '/api/clientes'

  Scenario: Crear un nuevo cliente exitosamente
    * def clienteId = 'juan_test_' + java.lang.System.currentTimeMillis()
    Given path basePath
    And request
      """
      {
        "nombre": "Juan Perez",
        "genero": "Masculino",
        "edad": 35,
        "direccion": "Av. Principal 123",
        "telefono": "0987654321",
        "clienteId": "#(clienteId)",
        "contrasena": "pass123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    And match response.nombre == 'Juan Perez'
    And match response.clienteId == '#notnull'
    And match response.estado == true
    * def clienteIdCreado = response.clienteId

  Scenario: Obtener todos los clientes
    Given path basePath
    When method GET
    Then status 200
    And match response == '#[]'
    And match each response ==
      """
      {
        "identificacion": "#number",
        "nombre": "#string",
        "genero": "#string",
        "edad": "#number",
        "direccion": "#string",
        "telefono": "#string",
        "clienteId": "#string",
        "contrasena": "#string",
        "estado": "#boolean"
      }
      """

  Scenario: Flujo completo - Crear, Obtener, Actualizar y Eliminar cliente
    * def nuevoClienteId = 'test_' + java.lang.System.currentTimeMillis()

    # 1. Crear cliente
    Given path basePath
    And request
      """
      {
        "nombre": "Maria Lopez",
        "genero": "Femenino",
        "edad": 28,
        "direccion": "Calle Secundaria 456",
        "telefono": "0998877665",
        "clienteId": "#(nuevoClienteId)",
        "contrasena": "maria123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    And match response.nombre == 'Maria Lopez'
    * def identificacion = response.identificacion

    # 2. Obtener el cliente creado
    Given path basePath + '/' + nuevoClienteId
    When method GET
    Then status 200
    And match response.nombre == 'Maria Lopez'
    And match response.clienteId == nuevoClienteId

    # 3. Actualizar el cliente
    Given path basePath + '/' + nuevoClienteId
    And request
      """
      {
        "nombre": "Maria Lopez Actualizada",
        "genero": "Femenino",
        "edad": 29,
        "direccion": "Nueva Direccion 789",
        "telefono": "0998877665",
        "clienteId": "#(nuevoClienteId)",
        "contrasena": "maria456",
        "estado": true
      }
      """
    When method PUT
    Then status 200
    And match response.nombre == 'Maria Lopez Actualizada'
    And match response.edad == 29

    # 4. Eliminar el cliente
    Given path basePath + '/' + nuevoClienteId
    When method DELETE
    Then status 204

    # 5. Verificar que fue eliminado
    Given path basePath + '/' + nuevoClienteId
    When method GET
    Then status 404

  Scenario: Intentar crear cliente con ID duplicado
    * def clienteIdDuplicado = 'duplicado_' + java.lang.System.currentTimeMillis()

    # Crear primer cliente
    Given path basePath
    And request
      """
      {
        "nombre": "Cliente Original",
        "genero": "Masculino",
        "edad": 30,
        "direccion": "Direccion 1",
        "telefono": "0912345678",
        "clienteId": "#(clienteIdDuplicado)",
        "contrasena": "pass1",
        "estado": true
      }
      """
    When method POST
    Then status 201

    # Intentar crear segundo cliente con mismo ID
    Given path basePath
    And request
      """
      {
        "nombre": "Cliente Duplicado",
        "genero": "Femenino",
        "edad": 25,
        "direccion": "Direccion 2",
        "telefono": "0923456789",
        "clienteId": "#(clienteIdDuplicado)",
        "contrasena": "pass2",
        "estado": true
      }
      """
    When method POST
    Then status 409
    And match response.mensaje contains 'ya existe'

  Scenario: Buscar cliente que no existe
    Given path basePath + '/cliente_inexistente_12345'
    When method GET
    Then status 404
    And match response.mensaje contains 'no encontrado'
