Feature: Gestión de Salida de Inventario
  Como dueño de bodega
  Quiero gestionar las salidas de productos mediante borradores y confirmaciones
  Para mantener el stock actualizado en tiempo real.

  # US01: Iniciar borrador de salida de productos
  Scenario: Borrador creado
    Given que el usuario quiere registrar una venta
    When añade algún producto a la venta por registrar
    Then el sistema crea un borrador con los productos añadidos a la salida de venta.

  # US02: Gestionar ítems del borrador
  Scenario: Agregar ítem
    Given que el usuario tiene un borrador vacío
    When agrega un producto con cantidad > 0
    Then el producto se añade al borrador y se recalculan subtotales.

  Scenario: Editar cantidad
    Given que el usuario quiere editar la cantidad de un ítem existente
    When cambia la cantidad a un valor válido (> 0)
    Then el sistema actualiza el ítem y muestra el nuevo subtotal.

  Scenario: Retirar ítem
    Given que el usuario quiere retirar un ítem del borrador
    When retira el ítem del borrador
    Then el ítem desaparece del borrador y se recalculan los totales.

  # US03: Confirmar salida de producto y descontar inventario
  Scenario: Confirmación exitosa
    Given que el usuario tiene un borrador válido
    When confirma la salida
    Then el sistema crea movimientos por ítem, decrementa el stock disponible y cambia el estado a confirmado.

  Scenario: Bloqueo por stock insuficiente
    Given que hay ítems sin stock suficiente
    When se intenta confirmar la salida
    Then el sistema bloquea la acción y lista los productos faltantes.

  Scenario: Disparo de alertas
    Given que se confirma la salida
    When algún producto queda por debajo del umbral mínimo
    Then el sistema genera una alerta de bajo stock.