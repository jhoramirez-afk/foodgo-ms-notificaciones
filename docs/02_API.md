# API REST: notificaciones

Base local: http://localhost:8086/api. Swagger UI: http://localhost:8086/swagger-ui/index.html.

| Método | Ruta | HTTP de éxito |
|---|---|---:|
| POST | /notificaciones | 201 |
| GET | /notificaciones | 200 |
| GET | /notificaciones/{id} | 200 |
| PUT | /notificaciones/{id} | 200 |
| DELETE | /notificaciones/{id} | 204 |
| POST | /notificaciones/{id}/intentos | 201 |
| GET | /notificaciones/{id}/intentos | 200 |
| GET | /intentos/{id} | 200 |
| PUT | /intentos/{id} | 200 |
| DELETE | /intentos/{id} | 204 |

## Crear entidad principal

```json
{
  "destinatario": "camila.soto.DEMO@example.com",
  "canal": "EMAIL",
  "mensaje": "Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."
}
```

## Crear entidad relacionada

```json
{
  "resultado": "ENVIADO",
  "fechaHora": "2026-10-01T13:30:00",
  "detalle": "Registro académico de entrega del aviso de confirmación."
}
```

Usar el ID retornado por la creación del padre. Los ID son generados por la BD. Editar los hijos mediante sus propias rutas. Ver las reglas y los campos calculados en REGLAS_EP02.md.

Errores: 400 para datos o JSON inválidos; 404 para recurso/relación local inexistente; 409 para conflictos de integridad o unicidad cuando corresponda. Un campo demasiado largo devuelve 400. Los mensajes y validationErrors se entregan mediante ApiExceptionHandler.
