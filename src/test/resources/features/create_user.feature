
Feature: Seguridad y Acceso (US16)
Scenario: Registro exitoso de nuevo administrador
 Given que el usuario se encuentra en la página de "Registro de Cuenta"
 When ingresa un nombre de usuario "admin_bodega", correo "contacto@bodega.com" y una contraseña segura
 And presiona el botón "Crear Usuario"
 Then el sistema debe encriptar la contraseña
 And redirigir al usuario al Dashboard principal con una sesión activa

 Feature: Gestión del Catálogo (US06)
Scenario: Registro de un nuevo producto base
 Given que el administrador ha iniciado sesión
 And se encuentra en el módulo de "Inventario"
 When completa el formulario de nuevo producto con nombre "Aceite Vegetal 1L" y categoría "Abarrotes"
 Then el sistema debe almacenar el producto en el catálogo maestro
 And mostrar el nuevo ítem en la lista de productos disponibles sin stock inicial

Feature: Gestión de Salidas de Inventario (US03)
Scenario: Confirmación exitosa de salida (Venta)
 Given que el usuario tiene un borrador de salida con "5" unidades del producto "Leche Gloria 400g"
 And el stock actual disponible en el sistema es de "20" unidades
 When el usuario selecciona la opción "Confirmar Salida"
 Then el sistema debe registrar el movimiento de salida (transacción ACID)
 And el stock disponible debe actualizarse automáticamente a "15" unidades

Scenario: Bloqueo de salida por stock insuficiente
 Given que el usuario intenta confirmar una salida de "50" unidades de "Arroz Costeño 5kg"
 And el stock en sistema indica que solo existen "10" unidades
 When el usuario intenta procesar la transacción
 Then el sistema debe mostrar un mensaje de alerta: "Error: Stock insuficiente para completar la operación"
 And no debe permitir el descuento ni la creación del registro de salida

 Feature: Gestión de Ingreso de Lotes (US15)
Scenario: Registro de lote con fecha de caducidad
 Given que el usuario recibe mercadería del proveedor "Distribuidora San Jorge"
 When registra el ingreso de "24" unidades de "Yogurt Natural" con fecha de vencimiento "20241215"
 Then el sistema debe crear un nuevo lote vinculado al proveedor
 And incrementar el stock total del producto en el panel de control

 Feature: Sistema de Alertas Inteligentes (US14)
Scenario: Notificación automática por stock bajo
 Given que un producto tiene configurado un "Stock Mínimo" de "10" unidades
 And el stock actual es de "11" unidades
 When se confirma una salida de "2" unidades
 Then el stock remanente cambia a "9"
 And el sistema debe generar automáticamente una alerta visual en el Dashboard indicando "Stock Crítico"

Scenario: Identificación de lotes por vencer
 Given que el sistema analiza diariamente las fechas de vencimiento
 When un lote se encuentra a menos de "7" días de caducar
 Then el sistema debe listar dicho lote en la sección de "Alertas Pendientes"
 And marcar el registro con un color de advertencia (rojo/amarillo)

### 6.1.4. Core System Tests.
