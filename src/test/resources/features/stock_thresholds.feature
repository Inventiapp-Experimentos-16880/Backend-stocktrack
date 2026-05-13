Feature: Configuración de Umbrales de Stock
  Como encargado de ventas
  Quiero configurar umbrales mínimos de stock
  Para que el sistema dispare alertas automáticas de reposición.

  # US12: Configurar umbrales de stock
  Scenario: Registro de umbral
    Given que se está registrando o editando un producto
    When se asigna un valor de stock mínimo
    Then el sistema vincula ese parámetro al producto para monitorear sus saldos.

  Scenario: Edición de umbral
    Given que ya existe un umbral configurado
    When el usuario actualiza el valor y guarda
    Then el sistema recalcula inmediatamente si debe mostrar alertas con el nuevo valor.