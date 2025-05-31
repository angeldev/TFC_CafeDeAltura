# 📖 Documentación de la API REST

Esta guía resume el uso de la API del backend de TFC_CafeDeAltura, con ejemplos, advertencias y enlaces a otros documentos relevantes.

---

## ✨ Mejoras implementadas respecto a lo exigido

| Mejora implementada                | Descripción breve                                                                 |
|------------------------------------|----------------------------------------------------------------------------------|
| Seguridad JWT y roles              | Endpoints protegidos y autenticación moderna, roles ADMIN/USER configurables      |
| Gestión de errores profesional     | Respuestas estructuradas y centralizadas en `GlobalExceptionHandler`             |
| Paginación avanzada                | Metadatos completos y ejemplos claros en la API y la documentación               |
| Documentación visual y profesional | Diagramas, tablas, ejemplos y enlaces cruzados                                   |
| Colección Postman avanzada         | Incluye tests automáticos y negativos ([docs/postman/README_Postman.md](./postman/README_Postman.md)) |
| Cobertura de código                | JaCoCo integrado y documentado                                                   |
| Scripts SQL robustos               | Incluyen índices, claves foráneas y datos de ejemplo                             |
| Guía de despliegue y configuración | Explicaciones para entornos de desarrollo y producción                            |
| Firma profesional y canales de contacto | Información de contacto y autoría en toda la documentación                  |
| Frontend visual integrado          | Paginación visual en la web, gestión de carrito, vistas Thymeleaf                |

---

## ⚠️ Advertencias y buenas prácticas

> ⚠️ **Advertencia:** Las credenciales mostradas en los ejemplos de configuración de base de datos son solo para desarrollo local. **Nunca uses estas credenciales en producción.** Define siempre tus propias variables de entorno seguras.

- Todas las validaciones de entrada **se realizan manualmente en los servicios**, siguiendo el enunciado del proyecto. Puede haber anotaciones automáticas (`@Valid`, `@NotNull`, etc.) en algunos DTOs o controladores para validaciones básicas, pero **la lógica principal de validación y la gestión de errores se implementa manualmente en la capa de servicios**.
- Se asegura la integridad de los datos recibidos y se gestionan los errores de forma centralizada, devolviendo respuestas estructuradas y mensajes claros.

---

## 🧩 Endpoints disponibles

| Método | Endpoint         | Descripción                |
|--------|-----------------|----------------------------|
| GET    | /api/cafe         | Lista todos los cafés (paginado)      |
| POST   | /api/cafe         | Crea un nuevo café         |
| PUT    | /api/cafe/{id}    | Modifica un café (total)   |
| PATCH  | /api/cafe/{id}    | Modifica un café (parcial) |
| DELETE | /api/cafe/{id}    | Elimina un café            |
| GET    | /api/clientes      | Lista todos los clientes (paginado)   |
| POST   | /api/clientes      | Crea un nuevo cliente      |
| PUT    | /api/clientes/{id} | Modifica un cliente        |
| DELETE | /api/clientes/{id} | Elimina un cliente         |
| GET    | /api/pedidos         | Lista todos los pedidos (paginado)    |
| POST   | /api/pedidos         | Crea un nuevo pedido       |
| GET    | /api/pedidos/{id}    | Consulta un pedido por ID  |
| GET    | /api/pedidos/cliente/{clienteId} | Pedidos de un cliente (paginado) |
| DELETE | /api/pedidos/{id}    | Elimina un pedido          |

Todos los endpoints devuelven respuestas en formato JSON. Los errores se gestionan con códigos HTTP estándar y mensajes descriptivos.

---

## 🔄 Paginación en la API

- Todos los endpoints de tipo `GET all` soportan paginación mediante los parámetros de query `page` (número de página, empezando en 1) y `size` (tamaño de página).
- Ejemplo de respuesta:

```json
{
  "contenido": [ ... ],
  "paginacion": {
    "paginaActual": 1,
    "tamanoPagina": 5,
    "totalPaginas": 3,
    "totalElementos": 13,
    "esUltima": false,
    "esPrimera": true
  }
}
```

---

## 📝 Ejemplos de uso

### POST /api/cafe

```json
{
  "nombre": "Café de ejemplo",
  "origen": "Colombia",
  "precio": 12.5,
  "descripcion": "Notas a chocolate",
  "intensidad": 7,
  "stock": 20
}
```

### POST /api/clientes

```json
{
  "nombre": "Lola Fernández",
  "email": "lola@email.com",
  "telefono": "600123456",
  "direccion": "Calle Mayor 12, Madrid"
}
```

### POST /api/pedidos

```json
{
  "cliente": { "id": 1 },
  "lineas": [
    { "cafe": { "id": 101 }, "cantidad": 2 },
    { "cafe": { "id": 102 }, "cantidad": 1 }
  ],
  "estado": "PENDIENTE",
  "comentario": "Entrega urgente"
}
```

---

## ⚡ Códigos de respuesta

| Código | Significado             | Cuándo se devuelve                |
|--------|-------------------------|-----------------------------------|
| 200    | OK                      | Consulta, modificación o borrado exitoso |
| 201    | Created                 | Creación exitosa de un recurso    |
| 400    | Bad Request             | Datos inválidos o incompletos     |
| 404    | Not Found               | Recurso no encontrado             |

---

## 🛡️ Validaciones y gestión de errores

- Ejemplo de validación manual en servicios:

```java
// En CafeService.java
public Cafe crearCafe(Cafe cafe) {
    if (cafe.getNombre() == null || cafe.getNombre().isBlank()) {
        throw new BadRequestException("El nombre del café es obligatorio");
    }
    if (cafe.getPrecio() == null || cafe.getPrecio() <= 0) {
        throw new BadRequestException("El precio debe ser mayor que cero");
    }
    // ... resto de validaciones y lógica
    return cafeRepository.guardar(cafe);
}
```

- Ejemplo de respuesta de error estructurada:

```json
{
  "timestamp": "2024-06-01T12:34:56.789+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El nombre del café es obligatorio",
  "path": "/api/cafe"
}
```

---

## 🔐 Seguridad y roles

- El sistema implementa autenticación JWT y roles (`ADMIN`, `USER`).
- **Situación actual:** La lógica de generación y validación de tokens JWT está implementada y los endpoints críticos están protegidos por roles. Sin embargo, el filtro JWT no está conectado explícitamente a la cadena de filtros de Spring Security, por lo que la autenticación JWT puede no estar funcionando al 100% en todas las rutas protegidas. Además, el frontend Thymeleaf no está protegido por JWT, solo la API REST.
- **Advertencia:** Para un entorno profesional, es necesario añadir el filtro JWT a la configuración de seguridad y revisar la protección de rutas web si se requiere.
- Ejemplo de autenticación JWT:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

Respuesta:

```json
{"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."}
```

Usa el token en tus peticiones:

```bash
curl -X GET http://localhost:8080/api/cafe \
  -H "Authorization: Bearer <token>"
```

En Postman, añade el header:

```
Authorization: Bearer {{jwt_token}}
```

---

## Endpoints avanzados

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET    | /clientes/buscarPorNombre?fragmento=ana | Buscar clientes por fragmento de nombre |
| GET    | /clientes/registradosDespuesDe?fecha=2024-06-01T00:00:00 | Buscar clientes registrados después de una fecha |
| GET    | /clientes/activosConEmailVerificado | Buscar clientes activos con email verificado |
| GET    | /clientes/conPedidos | Buscar clientes con pedidos |
| GET    | /cafe/stockBajo?umbral=5 | Cafés con stock bajo |
| GET    | /cafe/intensidadRango?min=3&max=7 | Cafés por rango de intensidad |
| GET    | /cafe/buscarPorPalabra?palabra=desc | Cafés por palabra clave |
| GET    | /cafe/agotados | Cafés agotados |
| GET    | /pedidos/porEstado?estado=ENVIADO | Pedidos por estado |
| GET    | /pedidos/rangoFechas?desde=2024-06-01T00:00:00&hasta=2024-06-30T23:59:59 | Pedidos por rango de fechas |
| GET    | /pedidos/totalMayorA?importe=50.0 | Pedidos con total mayor a un importe |
| GET    | /pedidos/conComentario | Pedidos con comentario |
| GET    | /lineas/cafe/{cafeId} | Líneas de pedido de un café |
| GET    | /lineas/pedido/{pedidoId} | Líneas de pedido de un pedido |
| GET    | /lineas/cantidadMayorA?cantidad=3 | Líneas de pedido con cantidad mayor a un valor |
| GET    | /lineas/subtotalMayorA?subtotal=20.0 | Líneas de pedido con subtotal mayor a un importe |

---

## 📦 Referencias cruzadas

- [README principal](../README.md)
- [Guía del Frontend](./frontend.md)
- [Arquitectura y flujo de peticiones](./arquitectura.md)
- [Estrategia de pruebas](./pruebas.md)
- [Colección Postman](./postman/README_Postman.md)
- [Guía de scripts SQL](./sql/README_sql.md)

---

> **¿Tienes dudas, sugerencias o quieres colaborar?**
>
> **Lola Fernández Fuentes**  
> Proyecto Final Bootcamp Fullstack Web Development (Randstad & GammaTech School)  
> [LinkedIn](https://www.linkedin.com/in/lolafernandezfuentes/)  
> Año: 2025
