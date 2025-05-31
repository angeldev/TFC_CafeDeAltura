# 🧪 Colección Postman – TFC_CafeDeAltura

Esta guía explica el uso y estructura de la colección Postman para pruebas manuales y automáticas de la API del proyecto.

---

## 📥 Cómo importar la colección y el entorno

1. Abre Postman y selecciona "Import".
2. Selecciona el archivo `TFC_CafeDeAltura_API.postman_collection.json` desde `docs/postman/api/`.
3. (Opcional) Importa el entorno `TFC_CafeDeAltura_Entorno.postman_environment.json`.

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

## ⚙️ Variables de entorno

- `base_url`: URL base de la API (ejemplo: `http://localhost:8080`)
- `jwt_token`: Token JWT para endpoints protegidos

---

## 🧪 Ejemplo de test automatizado en Postman

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

---

## ▶️ Ejecutar toda la colección como test suite

1. Haz clic en la colección y selecciona "Run collection".
2. Elige el entorno y ejecuta todos los tests.

---

## 🛠️ ¿Qué hacer si una petición falla?

- Revisa la respuesta y el mensaje de error.
- Verifica las variables de entorno y el token JWT.
- Consulta la [documentación de la API](../api.md).

---

## 📦 Estructura de la colección

| Carpeta   | Descripción                        | Peticiones incluidas                |
|-----------|------------------------------------|-------------------------------------|
| Café      | Endpoints CRUD para café           | GET, POST, PUT, PATCH, DELETE       |
| Clientes  | Endpoints CRUD para clientes       | GET, POST, PUT, DELETE              |
| Pedidos   | Endpoints CRUD para pedidos        | GET, POST, DELETE                   |
| Carrito   | Endpoints para carrito de compra   | GET, POST                           |

---

## 🚀 Ejemplo de uso

```bash
curl -X POST http://localhost:8080/cafe \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Café de ejemplo", "origen": "Colombia", "precio": 12.5, "descripcion": "Notas a chocolate", "intensidad": 7, "stock": 20}'
```

Consulta la [documentación de la API](../../api.md) para más ejemplos y detalles de los endpoints.

---

## 📎 Enlace directo a la colección

- [Descargar colección Postman](./api/TFC_CafeDeAltura_API.postman_collection.json)

---

## 🔗 Referencias

- [README principal](../../README.md)
- [Documentación de la API](../api.md)
- [Estrategia de pruebas](../pruebas.md)
- [Arquitectura y diagramas](../arquitectura.md)
- [Guía de scripts SQL](../sql/README_sql.md)

---

## 🔐 Autenticación y uso de JWT

Para acceder a los endpoints protegidos, debes incluir el header:

```
Authorization: Bearer <token>
```

Puedes obtener el token realizando un POST a `/api/auth/login` con usuario y contraseña válidos. El token debe copiarse en la variable de entorno `jwt_token` de Postman o añadirse manualmente en cada petición protegida.

> ⚠️ **Advertencia:** La lógica de generación y validación de tokens JWT está implementada y los endpoints críticos están protegidos por roles. Sin embargo, el filtro JWT no está conectado explícitamente a la cadena de filtros de Spring Security, por lo que la autenticación JWT puede no estar funcionando al 100% en todas las rutas protegidas. Además, el frontend Thymeleaf no está protegido por JWT, solo la API REST. Para un entorno profesional, es necesario añadir el filtro JWT a la configuración de seguridad y revisar la protección de rutas web si se requiere.

---

> **¿Tienes dudas, sugerencias o quieres colaborar?**
> 
> **Lola Fernández Fuentes**  
> Proyecto Final Bootcamp Fullstack Web Development (Randstad & GammaTech School)  
> [LinkedIn](https://www.linkedin.com/in/lolafernandezfuentes/)  
> Año: 2025.