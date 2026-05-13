Feature: Gestión de Proveedores y Lotes
  Como dueño de negocio
  Quiero administrar mis abastecedores y el ingreso de mercadería
  Para asegurar la trazabilidad y calidad de los productos.

  # US10: Gestionar cartera de proveedores
  Scenario: Registro de proveedor con validación de duplicados
    Given que se desea agregar a un nuevo abastecedor
    When el usuario ingresa un RUC que ya existe en la base de datos
    Then el sistema bloquea la creación y muestra un mensaje de error por duplicidad.

  # US11: Asociar productos a proveedor
  Scenario: Asociación de producto para reposición
    Given que se está gestionando una reposición de inventario
    When se selecciona un producto para vincularlo a un proveedor específico
    Then el sistema crea la relación y la almacena para futuras consultas.

  # US15: Gestionar el ingreso de nuevos lotes
  Scenario: Registro de lote con fecha de vencimiento
    Given que el usuario recibe mercadería nueva del proveedor
    When registra el ingreso asignando cantidad, proveedor y fecha de caducidad
    Then el sistema crea el lote e incrementa el stock del producto
    And guarda la fecha para el monitoreo de alertas.

  Scenario: Visualización de caducidad en el inventario
    Given que se consulta el detalle de un producto
    When el usuario revisa los lotes vigentes registrados
    Then el sistema muestra la fecha límite de consumo de cada unidad ingresada.

  Scenario: Filtro de auditoría de ingresos por proveedor
    Given que se necesita revisar mercadería de una empresa específica
    When se aplica el filtro por proveedor en la vista de lotes
    Then el sistema lista únicamente los ingresos vinculados a ese socio.