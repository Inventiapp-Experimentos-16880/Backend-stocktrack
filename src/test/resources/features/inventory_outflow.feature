Feature: Gestión de Salida de Inventario
  Como dueño de bodega
  Quiero gestionar las salidas de productos mediante borradores y confirmaciones
  Para mantener el stock actualizado en tiempo real.

  # US01: Iniciar borrador de salida de productos
  Scenario: Borrador creado
    Given que el usuario quiere registrar una venta
    When añade un producto a la venta por registrar
    Then el sistema crea un borrador con los productos añadidos a la salida de venta.

  # US02: Gestionar ítems del borrador
  Scenario: Agregar ítem al borrador
    Given que el usuario tiene un borrador vacío
    When agrega un producto con cantidad mayor a 0
    Then el producto se añade al borrador y se recalculan subtotales.

  Scenario: Editar cantidad de un ítem
    Given que el usuario quiere editar la cantidad de un ítem existente en el borrador
    When cambia la cantidad a un valor válido mayor a 0
    Then el sistema actualiza el ítem y muestra el nuevo subtotal.

  Scenario: Retirar ítem del borrador
    Given que el usuario quiere retirar un ítem del borrador
    When retira el ítem seleccionado
    Then el ítem desaparece del borrador y se recalculan los totales.

  # US03: Confirmar salida de producto y descontar inventario
  Scenario: Confirmación de salida exitosa
    Given que el usuario tiene un borrador válido
    When confirma la salida de los productos
    Then el sistema crea los movimientos por ítem
    And decrementa el stock disponible
    And cambia el estado de la transacción a confirmado.

  Scenario: Bloqueo de confirmación por stock insuficiente
    Given que existen ítems en el borrador sin stock suficiente
    When se intenta confirmar la salida
    Then el sistema bloquea la acción y lista los productos faltantes.

  Scenario: Disparo de alertas de stock mínimo
    Given que se confirma la salida de un producto
    When la cantidad resultante queda por debajo del umbral mínimo configurado
    Then el sistema genera automáticamente una alerta de bajo stock.