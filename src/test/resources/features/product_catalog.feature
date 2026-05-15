Feature: Gestión del Catálogo de Productos
  Como encargado de ventas
  Quiero administrar los productos y sus categorías
  Para mantener la información del inventario organizada y veraz.

  # US06: Gestionar catálogo de productos
  Scenario: Creación de un nuevo producto
    Given que el usuario necesita registrar un nuevo artículo
    When completa el formulario con datos válidos y guarda la información
    Then el sistema añade el producto al catálogo y lo habilita para operaciones.

  Scenario: Edición de un producto existente
    Given que la información de un producto debe ser actualizada
    When el usuario modifica los campos permitidos como precio o descripción
    Then el sistema actualiza la ficha del producto
    And bloquea la edición de campos críticos como el ID o SKU.

  Scenario: Inhabilitación lógica (Soft-Delete)
    Given que un producto ya no será comercializado
    When el usuario selecciona la opción de desactivar o eliminar
    Then el sistema cambia su estado a inactivo
    And lo oculta de nuevas ventas conservando su data histórica.

  # US07: Clasificación de productos por categoría
  Scenario: Clasificación válida de producto
    Given que existe un maestro de categorías configurado
    When el usuario asigna una categoría a un producto
    Then el producto queda correctamente vinculado a dicha categoría.

  Scenario: Filtrado del catálogo por categoría
    Given que existen productos registrados en distintas categorías
    When se aplica un filtro por una categoría específica
    Then el sistema muestra únicamente los productos pertenecientes a esa categoría.

  # US08: Búsqueda y filtrado de productos
  Scenario: Búsqueda de producto por coincidencia parcial
    Given que existen productos registrados en el sistema
    When se realiza una búsqueda ingresando una parte del nombre
    Then el sistema lista todos los resultados que coincidan con la cadena.