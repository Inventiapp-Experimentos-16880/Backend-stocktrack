Feature: Administración de Usuarios
  Como dueño de startup
  Quiero crear cuentas de usuario personalizadas
  Para organizar el equipo de trabajo y segregar responsabilidades.

  # US16: Crear usuarios nuevos
  Scenario: Registro exitoso de un nuevo colaborador
    Given que el administrador se encuentra en el módulo de gestión de personal
    When ingresa los datos de perfil (nombre, correo, rol) y confirma la creación
    Then el sistema genera la cuenta en la base de datos
    And despacha automáticamente las credenciales de acceso al correo electrónico del nuevo usuario.