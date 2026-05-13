Feature: Reportes Operativos de Inventario
  Como dueño de bodega
  Quiero generar reportes dinámicos del estado de mis productos
  Para priorizar decisiones estratégicas y compras.

  # US04: Generar reportes de estado de inventario
  Scenario: Visualización del stock actual consolidado
    Given que el usuario accede al módulo de reportes de inventario
    When selecciona la vista general de Stock Actual
    Then el sistema muestra una lista paginada con cantidad, categoría y valorización total.

  Scenario: Filtrado de productos con bajo stock para reposición
    Given que el usuario necesita planificar sus compras
    When aplica el filtro de Bajo Stock en el reporte
    Then el sistema muestra solo productos con cantidad menor o igual al umbral mínimo.

  Scenario: Identificación de lotes próximos a vencer (Merma)
    Given que el usuario desea evitar pérdidas por vencimiento
    When aplica el filtro de Próximos a Vencer
    Then el sistema lista los lotes ordenados por caducidad
    And resalta visualmente los que vencen en los próximos 30 días.

  Scenario: Exportación de datos de reportes
    Given que el usuario ha generado una vista específica del reporte con filtros
    When hace clic en el botón de Exportar
    Then el sistema descarga un archivo Excel o PDF con los datos mostrados.