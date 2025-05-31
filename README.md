# ☕ TFC_CafeDeAltura

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/Lola793/TFC_CafeDeAltura/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Backend profesional para la gestión de una tienda de café, desarrollado en Java con Spring Boot. Permite gestionar cafés, clientes y pedidos siguiendo una arquitectura limpia y buenas prácticas de desarrollo.

---

## 🚦 Estado del proyecto

> **Estado:** Estable (v1.0.0) – En desarrollo activo. Se aceptan contribuciones.

---

## 📂 Estructura del repositorio

| Carpeta/Archivo                | Descripción                                 |
|-------------------------------|---------------------------------------------|
| `src/main/java/proyecto/tfc/`  | Código fuente principal                     |
| `docs/`                        | Documentación y recursos                    |
| `docs/postman/`                | Colecciones y docs Postman                  |
| `docs/sql/`                    | Scripts SQL y guía de base de datos         |
| `pom.xml`                      | Configuración Maven                         |
| `README.md`                    | Este archivo                                |

---

## 🛠️ Requisitos previos

| Requisito   | Versión recomendada |
|-------------|---------------------|
| Java        | 17 o superior       |
| Maven       | 3.8+                |
| MySQL       | 8+ (opcional)       |
| Git         | Última              |

---

## ✨ Mejoras implementadas respecto a lo exigido

| Mejora implementada                | Descripción breve                                                                 |
|------------------------------------|----------------------------------------------------------------------------------|
| Seguridad JWT y roles              | Endpoints protegidos y autenticación moderna, roles ADMIN/USER configurables      |
| Gestión de errores profesional     | Respuestas estructuradas y centralizadas en `GlobalExceptionHandler`             |
| Paginación avanzada                | Metadatos completos y ejemplos claros en la API y la documentación               |
| Documentación visual y profesional | Diagramas, tablas, ejemplos y enlaces cruzados                                   |
| Colección Postman avanzada         | Incluye ejemplos y pruebas manuales ([docs/postman/README_Postman.md](docs/postman/README_Postman.md)) |
| Scripts SQL robustos               | Incluyen índices, claves foráneas y datos de ejemplo                             |
| Guía de despliegue y configuración | Explicaciones para entornos de desarrollo y producción                            |
| Firma profesional y canales de contacto | Información de contacto y autoría en toda la documentación                  |
| Frontend visual integrado          | Paginación visual en la web, gestión de carrito, vistas Thymeleaf                |

---

## 🚀 Mejoras futuras

- Implementación de tests automáticos (JUnit, Spring Boot Test) para lógica de negocio y endpoints.
- Integración de cobertura de código (JaCoCo) y generación de informes.
- Automatización de pruebas y despliegue continuo (CI/CD).
- **Conexión real del filtro JWT a la cadena de filtros de Spring Security para garantizar la autenticación y autorización en todos los endpoints protegidos.**
- **Protección de rutas web (frontend) mediante autenticación y roles si se requiere en el futuro.**

---

## ⚠️ Advertencias y buenas prácticas

> ⚠️ **Advertencia:** Las credenciales mostradas en los ejemplos de configuración de base de datos son solo para desarrollo local. **Nunca uses estas credenciales en producción.** Define siempre tus propias variables de entorno seguras.

- Todas las validaciones de entrada **se realizan manualmente en los servicios**, siguiendo el enunciado del proyecto. Puede haber anotaciones automáticas (`@Valid`, `@NotNull`, etc.) en algunos DTOs o controladores para validaciones básicas, pero **la lógica principal de validación y la gestión de errores se implementa manualmente en la capa de servicios**.
- Se asegura la integridad de los datos recibidos y se gestionan los errores de forma centralizada, devolviendo respuestas estructuradas y mensajes claros.

---

## ⚙️ Instalación y configuración

1. Clona el repositorio:

```bash
git clone https://github.com/Lola793/TFC_CafeDeAltura
cd TFC_CafeDeAltura
```

2. Configura la base de datos (opcional):
   - Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tfc_cafedealtura?useSSL=false&serverTimezone=UTC
spring.datasource.username=cafedbuser
spring.datasource.password=cafedbpass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

   - Ejecuta los scripts de `docs/sql/` según la [guía SQL](docs/sql/README_sql.md).

---

## 🚀 Ejecución y despliegue

- Ejecuta la aplicación en desarrollo:

```bash
./mvnw spring-boot:run
```

- Para producción:

```bash
./mvnw clean package
java -jar target/TFC_CafeDeAltura-1.0.0.jar
```

---

## 🧩 Ejemplo de uso básico

```bash
curl -X GET http://localhost:8080/cafe
```

Respuesta:

```json
[
  {
    "id": 1,
    "nombre": "Altura Colombia",
    "precio": 10.5,
    "origen": "Colombia",
    "intensidad": 7,
    "stock": 50
  }
]
```

Consulta la [documentación de la API](docs/api.md) para ver todos los endpoints, ejemplos de uso y advertencias.

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

## 🔄 Paginación en la API y la web

- Todos los endpoints de tipo `GET all` soportan paginación mediante los parámetros de query `page` (número de página, empezando en 1) y `size` (tamaño de página).
- Ejemplo visual en la web:

```
<< < Página 2 de 5 > >>
```

El usuario puede navegar entre páginas haciendo clic en los enlaces "Anterior", "Siguiente" o en los números de página. Solo se muestran los elementos de la página actual, mejorando la experiencia y el rendimiento.

---

## 🔐 Seguridad y roles

- El sistema implementa autenticación JWT y roles (`ADMIN`, `USER`).
- **Situación actual:** La lógica de generación y validación de tokens JWT está implementada y los endpoints críticos están protegidos por roles. Sin embargo, el filtro JWT no está conectado explícitamente a la cadena de filtros de Spring Security, por lo que la autenticación JWT puede no estar funcionando al 100% en todas las rutas protegidas. Además, el frontend Thymeleaf no está protegido por JWT, solo la API REST.
- **Advertencia:** Para un entorno profesional, es necesario añadir el filtro JWT a la configuración de seguridad y revisar la protección de rutas web si se requiere.
- Ejemplo de autenticación JWT:

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

Respuesta:

```json
{"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."}
```

Usa el token en tus peticiones:

```bash
curl -X GET http://localhost:8080/cafe \
  -H "Authorization: Bearer <token>"
```

En Postman, añade el header:

```
Authorization: Bearer {{jwt_token}}
```

---

## 🗝️ Variables de entorno recomendadas

Debes definir las siguientes variables de entorno antes de ejecutar la aplicación (por ejemplo, en un archivo `.env` **que nunca debes subir a GitHub**):

```env
# Clave secreta para JWT (obligatoria y crítica para seguridad)
JWT_SECRET_KEY=pon_aqui_una_clave_secreta_segura

# Usuario y contraseña de la base de datos
DB_USER=tu_usuario
DB_PASS=tu_contraseña
```

- **JWT_SECRET_KEY**: Clave secreta utilizada para firmar y validar los tokens JWT. Si no está definida, la aplicación no funcionará correctamente y la seguridad estará comprometida.
- **DB_USER / DB_PASS**: Credenciales de acceso a la base de datos MySQL.

> ⚠️ **Nunca subas el archivo `.env` a un repositorio público.**

---

## 🖥️ Frontend y vistas web

El proyecto incluye un frontend integrado basado en vistas Thymeleaf (plantillas HTML) y recursos estáticos (CSS, imágenes) servidos por Spring Boot. Este frontend permite interactuar con la API de forma visual y amigable, facilitando la gestión de cafés, clientes, pedidos y el carrito de compra.

| Ruta                | Vista/Plantilla                | Descripción breve                         |
|---------------------|--------------------------------|-------------------------------------------|
| `/`                 | `index.html`                   | Página de inicio/resumen                  |
| `/cafes`            | `cafes.html`                   | Listado de cafés                          |
| `/cafes/{id}`       | `cafe-detalle.html`            | Detalle de un café                        |
| `/clientes`         | `clientes.html`                | Listado de clientes                       |
| `/clientes/{id}`    | `cliente-detalle.html`         | Detalle de cliente                        |
| `/pedidos`          | `pedidos.html`                 | Listado de pedidos                        |
| `/pedidos/{id}`     | `pedido-detalle.html`          | Detalle de pedido                         |
| `/carrito`          | `carrito.html`                 | Visualización y gestión del carrito       |
| `/login`            | `login.html`                   | Formulario de inicio de sesión            |
| `/registro`         | `registro.html`                | Formulario de registro de usuario         |
| `/error`            | `error.html`                   | Página de error personalizada             |

Más detalles en la [guía del frontend](docs/frontend.md).

---

## 📦 Referencias cruzadas

- [Documentación de la API](docs/api.md)
- [Guía del Frontend](docs/frontend.md)
- [Arquitectura y flujo de peticiones](docs/arquitectura.md)
- [Colección Postman](docs/postman/README_Postman.md)
- [Guía de scripts SQL](docs/sql/README_sql.md)

---

## 👤 Firma profesional y contacto

> **¿Tienes dudas, sugerencias o quieres colaborar?**
>
> **Lola Fernández Fuentes**  
> Proyecto Final Bootcamp Fullstack Web Development (Randstad & GammaTech School)  
> [LinkedIn](https://www.linkedin.com/in/lolafernandezfuentes/)  
> Año: 2025