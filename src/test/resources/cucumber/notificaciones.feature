# language: es
Característica: Servicio Notificacion (microservicio notificaciones del caso FoodGo)
  Los escenarios validan el contrato REST del microservicio alineado a sus endpoints.

  Escenario: el listado del recurso responde 200
    Dado el servicio "Notificacion" está disponible
    Cuando consulto el listado de "notificaciones"
    Entonces el listado responde con código 200

  Escenario: ciclo de vida completo del recurso
    Dado un nuevo "notificacion" con destinatario "camila.cucumber@example.com"
    Cuando consulto el "notificacion" recién creado
    Entonces el recurso tiene destinatario "camila.cucumber@example.com" y código 200
    Cuando actualizo el "notificacion" con destinatario "camila.actualizada@example.com"
    Entonces el recurso queda con destinatario "camila.actualizada@example.com" y código 200
    Cuando elimino el "notificacion"
    Entonces la eliminación responde con código 204
    Y al consultar el "notificacion" eliminado responde 404
