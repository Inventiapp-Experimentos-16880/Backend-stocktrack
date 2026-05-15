Feature: Definición de Composición de Kits
  Como encargado de ventas
  Quiero definir la estructura de kits de productos
  Para estandarizar las ofertas comerciales y paquetes promocionales.

  # US11: Definir composición de un kit
  Scenario: Creación de la definición
    Given que el usuario accede al módulo de gestión de kits
    When crea un nuevo kit agregando componentes y sus cantidades
    Then el sistema almacena la definición del kit correctamente.