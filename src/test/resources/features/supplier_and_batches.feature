Feature: Gestión de Proveedores y Lotes
  Como dueño de negocio
  Quiero administrar mis abastecedores y el ingreso de mercadería
  Para asegurar la trazabilidad y calidad de los productos.

  # US10: Asociar productos a proveedor
  Scenario: Asociación de producto
    Given que se está gestionando una reposición de inventario
    When se selecciona un producto para vincularlo a un proveedor específico
    Then el sistema crea la relación y la almacena para futuras consultas.

  Scenario: Visualización de cartera de productos
    Given que un proveedor tiene productos asociados
    When el usuario consulta el detalle del proveedor
    Then visualiza el listado completo de artículos que dicho proveedor suministra.

  # US14: Gestionar el ingreso de nuevos lotes
  Scenario: Registro de lote con vencimiento
    Given que el usuario recibe mercadería nueva
    When registra el ingreso asignando una cantidad, un proveedor y una fecha de caducidad
    Then el sistema crea el lote, incrementa el stock del producto y guarda la fecha para futuras alertas.

  Scenario: Visualización de caducidad
    Given que se consulta el detalle del inventario
    When el usuario revisa los lotes vigentes
    Then el sistema muestra claramente la fecha límite de consumo de cada unidad ingresada.

  Scenario: Filtro de auditoría por proveedor
    Given que se necesita revisar la mercadería suministrada por una empresa específica
    When se aplica el filtro por proveedor en la vista de lotes
    Then el sistema lista únicamente los ingresos vinculados a ese socio comercial.