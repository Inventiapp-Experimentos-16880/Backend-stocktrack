Feature: Reportes Operativos de Inventario
  Como dueño de bodega
  Quiero generar reportes dinámicos del estado de mis productos
  Para priorizar decisiones estratégicas y compras.

  # US04: Generar reportes de estado de inventario
  Scenario: Visualización del stock actual consolidado
    Given que el usuario accede al módulo de reportes de inventario
    When selecciona la vista general de Stock Actual
    Then el sistema muestra una lista paginada de todos los productos con su cantidad disponible, categoría y valorización total.

  Scenario: Filtrado de productos con bajo stock
    Given que el usuario necesita planificar sus compras
    When aplica el filtro de Bajo Stock en el reporte
    Then el sistema actualiza la vista para mostrar únicamente los productos cuya cantidad actual sea menor o igual a su umbral mínimo configurado.

  Scenario: Identificación de lotes próximos a vencer
    Given que el usuario desea evitar pérdidas por vencimiento
    When aplica el filtro de Próximos a Vencer
    Then el sistema lista los lotes ordenados por fecha de caducidad más próxima, resaltando visualmente aquellos que vencen en los próximos 30 días.

  Scenario: Exportación de datos de reportes
    Given que el usuario ha generado una vista específica del reporte (Stock General, Bajo Stock o Vencimientos)
    When hace clic en el botón de Exportar
    Then el sistema genera y descarga un archivo (Excel o PDF) que contiene exactamente los datos mostrados en pantalla con sus respectivos filtros aplicados.