# 🖥️ Guía del Frontend – TFC_CafeDeAltura

Esta guía describe el funcionamiento, estructura y personalización del frontend web incluido en el proyecto TFC_CafeDeAltura.

---

## 📋 Descripción general

El frontend está integrado en el backend Spring Boot y utiliza **Thymeleaf** para renderizar vistas HTML dinámicas. Permite a los usuarios interactuar visualmente con la tienda de café: consultar productos, gestionar clientes, realizar pedidos y operar con el carrito de compra.

- **Tecnologías:**
  - Spring Boot MVC
  - Thymeleaf
  - CSS personalizado
  - Recursos estáticos (imágenes, iconos)

---

## 🗂️ Estructura de vistas y rutas principales

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

Las plantillas están en `src/main/resources/templates/`.

---

## 🎨 Personalización visual

- **CSS principal:** `src/main/resources/static/css/styles.css`
- **Imágenes:** `src/main/resources/static/images/`
- Puedes modificar el estilo editando el CSS o añadir nuevas imágenes según tus necesidades.

---

## 🔗 Integración con el backend

Las vistas Thymeleaf obtienen los datos a través de los controladores Spring, que a su vez consumen los servicios y repositorios del backend. Las operaciones de alta, edición y borrado se realizan mediante formularios HTML y peticiones POST/PUT/DELETE gestionadas por los controladores.

- **Consumo de endpoints:**
  - Listados y detalles: GET
  - Formularios de alta/modificación: POST/PUT
  - Eliminación: POST/DELETE (según la vista)

---

## 🛠️ Desarrollo y ampliación

- Para añadir una nueva vista, crea un archivo `.html` en la carpeta de plantillas y define la ruta en el controlador correspondiente.
- Para modificar el comportamiento, edita los controladores en `src/main/java/proyecto/tfc/controllers/WebController.java`.
- El CSS puede ampliarse o personalizarse según la identidad visual deseada.

---

## 📚 Referencias cruzadas

- [Documentación de la API](./api.md)
- [README principal](../README.md)
- [Arquitectura y flujo de peticiones](./arquitectura.md)

---

## 📸 Ejemplo visual de paginación en la web

En el listado de cafés, clientes o pedidos, la paginación se muestra en la parte inferior de la tabla o lista:

```
<< < Página 2 de 5 > >>
```

El usuario puede navegar entre páginas haciendo clic en los enlaces "Anterior", "Siguiente" o en los números de página. Solo se muestran los elementos de la página actual, mejorando la experiencia y el rendimiento.

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

## 🔐 Seguridad y autenticación

> **Nota:** El frontend (vistas Thymeleaf) no está protegido por JWT. Solo la API REST requiere autenticación JWT para los endpoints protegidos.

---

> **¿Tienes dudas, sugerencias o quieres colaborar?**
>
> **Lola Fernández Fuentes**  
> Proyecto Final Bootcamp Fullstack Web Development (Randstad & GammaTech School)  
> [LinkedIn](https://www.linkedin.com/in/lolafernandezfuentes/)  
> Año: 2025
