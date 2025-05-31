# 🗄️ Guía de scripts SQL – TFC_CafeDeAltura

Esta guía explica el uso de los scripts SQL para crear y poblar la base de datos del proyecto, alineados con el modelo de datos y la arquitectura del backend.

---

## 🛠️ Requisitos

- MySQL 8.0 o superior
- Acceso de administrador para crear usuarios y bases de datos

---

## 📦 Contenido de la carpeta

| Archivo                    | Propósito                                                        |
|---------------------------|------------------------------------------------------------------|
| crear_bd_mysql.sql        | Crea la base de datos, tablas, índices y datos de ejemplo         |
| schema.sql                | Solo estructura de tablas, sin datos de ejemplo                  |
| data.sql                  | Inserta datos iniciales si usas autogeneración con Spring Boot   |
| crear_usuario_mysql.sql   | Crea el usuario de base de datos y otorga permisos               |

---

## 🗃️ Explicación de tablas y relaciones

- **cafe:** Productos de café, clave primaria `id`.
- **cliente:** Clientes registrados, clave primaria `id`, email único.
- **pedido:** Pedidos realizados, referencia a `cliente`.
- **linea_de_pedido:** Detalle de cada pedido, referencia a `pedido` y `cafe`.

---

## 📝 Ejemplo de consulta SQL

```sql
SELECT nombre, stock FROM cafe WHERE stock < 10;
```

---

## ♻️ Restaurar copia de seguridad

1. Realiza un backup:
   ```bash
   mysqldump -u cafedbuser -p tfc_cafedealtura > backup.sql
   ```
1. Restaura:
   ```bash
   mysql -u cafedbuser -p tfc_cafedealtura < backup.sql
   ```

---

## 🔄 Migrar datos entre entornos

Utiliza `mysqldump` y `mysql` para exportar/importar datos entre servidores.

---

## 🖼️ Diagrama ER

> Si dispones de un diagrama ER, inclúyelo aquí o enlázalo.

---

## 🚀 Uso de los scripts

1. **Crear usuario y permisos:**
   ```bash
   mysql -u root -p < docs/sql/crear_usuario_mysql.sql
   ```
1. **Crear la base de datos y poblarla:**
   ```bash
   mysql -u cafedbuser -p < docs/sql/crear_bd_mysql.sql
   ```
1. **Uso con Spring Boot:**
   - Copia `schema.sql` y `data.sql` a `src/main/resources/` si usas autogeneración.
   - Configura `application.properties` según el modo de uso.

---

## ⚠️ Advertencias

- No uses los scripts de ejemplo en producción sin revisarlos.
- El script principal elimina tablas antes de crearlas (DROP TABLE IF EXISTS).
- El campo `email` en la tabla `cliente` es único.

---

## 📝 Ejemplo de datos generados

| id | nombre            | origen    | precio | intensidad | stock |
|----|-------------------|-----------|--------|------------|-------|
| 1  | Altura Colombia   | Colombia  | 10.5   | 7          | 50    |
| 2  | Brasil Cerrado    | Brasil    | 9.0    | 5          | 30    |

---

## 🔗 Referencias

- [README principal](../../README.md)
- [Documentación de la API](../api.md)
- [Estrategia de pruebas](../pruebas.md)
- [Arquitectura y diagramas](../arquitectura.md)

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

> ⚠️ **Nota:** La clave JWT solo afecta a la seguridad de la API REST, no al frontend web.

> ⚠️ **Nunca subas el archivo `.env` a un repositorio público.**

---

> **¿Tienes dudas, sugerencias o quieres colaborar?**
> 
> **Lola Fernández Fuentes**  
> Proyecto Final Bootcamp Fullstack Web Development (Randstad & GammaTech School)  
> [LinkedIn](https://www.linkedin.com/in/lola-fernandez-fuentes/)  
> Año: 2025
