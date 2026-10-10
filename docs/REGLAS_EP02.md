# Integridad del dominio notificaciones

Canales EMAIL o SMS. EMAIL exige correo con formato válido; SMS exige teléfono internacional, por ejemplo +56912345678. Mensaje obligatorio. Los intentos registran ENVIADO o FALLIDO con fecha y detalle. Este CRUD registra avisos e intentos para la EP02; no envía comunicaciones externas.

## Alcance de la evaluación

Se mantienen controller/service/repository/model, CRUD REST, relaciones OneToMany/ManyToOne, MySQL, Maven y Git. Las reglas hacen coherentes los datos retornados y las pruebas de éxito/error (IE1, IE2, IE3, IE5, IE6). No se agregan componentes externos. README, Postman y consultas SQL respaldan IE4, IE7 e IE10.

La colección incluye 9 peticiones de CRUD/lectura, 10 casos de error y 6 peticiones de eliminación/cascada. `mvn clean install` ejecuta pruebas unitarias, MockMvc con JPA/H2 y escenarios Cucumber. MySQL se demuestra con el perfil mysql y la colección.
