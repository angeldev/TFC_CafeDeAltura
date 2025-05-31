package proyecto.tfc.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.services.CafeService;
import proyecto.tfc.services.CarritoService;
import proyecto.tfc.services.ClienteService;
import proyecto.tfc.services.LineaDePedidoService;
import proyecto.tfc.services.PedidoService;

/**
 * Controlador MVC para la gestión de vistas web de la tienda de café.
 * Proporciona rutas para la navegación, formularios y operaciones CRUD visuales sobre cafés, clientes, pedidos y carrito.
 *
 * <p>Las operaciones de negocio se delegan a los servicios correspondientes. Las vistas se renderizan con Thymeleaf.</p>
 */
@Controller
public class WebController {

    private final CafeService cafeService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final CarritoService carritoService;
    private final LineaDePedidoService lineaDePedidoService;

    public WebController(CafeService cafeService, ClienteService clienteService, PedidoService pedidoService, CarritoService carritoService, LineaDePedidoService lineaDePedidoService) {
        this.cafeService = cafeService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.carritoService = carritoService;
        this.lineaDePedidoService = lineaDePedidoService;
    }

    /**
     * Página de inicio con productos destacados.
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Cafe> productos = cafeService.listarCafesPaginado(1, 4).getContent();
        model.addAttribute("productos", productos);
        return "index";
    }

    /**
     * Listado de cafés con filtros y paginación para la vista web.
     */
    @GetMapping("/cafes")
    public String cafes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Integer minIntensidad,
            @RequestParam(required = false) Integer maxIntensidad,
            @RequestParam(required = false) Boolean stockBajo,
            @RequestParam(required = false) Boolean agotados,
            @RequestParam(required = false) String orden,
            Model model) {
        if (palabra != null && palabra.isEmpty()) palabra = null;
        if (orden != null && orden.isEmpty()) orden = null;
        var pageCafes = cafeService.buscarCafesFiltradoPaginado(palabra, minIntensidad, maxIntensidad, stockBajo, agotados, orden, page, size);
        int totalPaginas = pageCafes.getTotalPages();
        int paginaSolicitada = Math.max(1, Math.min(page, Math.max(totalPaginas, 1)));
        if (paginaSolicitada != (pageCafes.getNumber() + 1)) {
            pageCafes = cafeService.buscarCafesFiltradoPaginado(palabra, minIntensidad, maxIntensidad, stockBajo, agotados, orden, paginaSolicitada, size);
        }
        model.addAttribute("cafes", pageCafes.getContent());
        model.addAttribute("paginaActual", paginaSolicitada);
        model.addAttribute("tamanoPagina", pageCafes.getSize());
        model.addAttribute("totalPaginas", pageCafes.getTotalPages());
        model.addAttribute("totalElementos", pageCafes.getTotalElements());
        model.addAttribute("param", new org.springframework.ui.ModelMap()
                .addAttribute("palabra", palabra)
                .addAttribute("minIntensidad", minIntensidad)
                .addAttribute("maxIntensidad", maxIntensidad)
                .addAttribute("stockBajo", stockBajo)
                .addAttribute("agotados", agotados)
                .addAttribute("orden", orden));
        return "cafes";
    }

    /**
     * Muestra el detalle de un café.
     */
    @GetMapping("/cafes/{id}")
    public String cafeDetalle(@PathVariable Long id, Model model) {
        Cafe cafe = cafeService.obtenerCafePorId(id);
        model.addAttribute("cafe", cafe);
        return "cafe-detalle";
    }

    /**
     * Listado de clientes con filtros y paginación para la vista web.
     */
    @GetMapping("/clientes")
    public String clientes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Boolean emailVerificado,
            @RequestParam(required = false) Boolean conPedidos,
            @RequestParam(required = false) String orden,
            Model model) {
        if (nombre != null && nombre.isEmpty()) nombre = null;
        if (fecha != null && fecha.isEmpty()) fecha = null;
        if (orden != null && orden.isEmpty()) orden = null;
        var pageClientes = clienteService.buscarClientesFiltradoPaginado(nombre, fecha, emailVerificado, conPedidos, orden, page, size);
        int totalPaginas = pageClientes.getTotalPages();
        int paginaSolicitada = Math.max(1, Math.min(page, Math.max(totalPaginas, 1)));
        if (paginaSolicitada != (pageClientes.getNumber() + 1)) {
            pageClientes = clienteService.buscarClientesFiltradoPaginado(nombre, fecha, emailVerificado, conPedidos, orden, paginaSolicitada, size);
        }
        model.addAttribute("clientes", pageClientes.getContent());
        model.addAttribute("paginaActual", paginaSolicitada);
        model.addAttribute("tamanoPagina", pageClientes.getSize());
        model.addAttribute("totalPaginas", pageClientes.getTotalPages());
        model.addAttribute("totalElementos", pageClientes.getTotalElements());
        model.addAttribute("param", new org.springframework.ui.ModelMap()
                .addAttribute("nombre", nombre)
                .addAttribute("fecha", fecha)
                .addAttribute("emailVerificado", emailVerificado)
                .addAttribute("conPedidos", conPedidos)
                .addAttribute("orden", orden));
        return "clientes";
    }

    /**
     * Muestra el detalle de un cliente.
     */
    @GetMapping("/clientes/{id}")
    public String clienteDetalle(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        model.addAttribute("cliente", cliente);
        return "cliente-detalle";
    }

    /**
     * Listado de pedidos con filtros y paginación para la vista web.
     */
    @GetMapping("/pedidos")
    public String pedidos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String orden,
            Model model) {
        if (cliente != null && cliente.isEmpty()) cliente = null;
        if (fechaDesde != null && fechaDesde.isEmpty()) fechaDesde = null;
        if (fechaHasta != null && fechaHasta.isEmpty()) fechaHasta = null;
        if (estado != null && estado.isEmpty()) estado = null;
        if (orden != null && orden.isEmpty()) orden = null;
        var pagePedidos = pedidoService.buscarPedidosFiltradoPaginado(cliente, fechaDesde, fechaHasta, estado, orden, page, size);
        int totalPaginas = pagePedidos.getTotalPages();
        int paginaSolicitada = Math.max(1, Math.min(page, Math.max(totalPaginas, 1)));
        if (paginaSolicitada != (pagePedidos.getNumber() + 1)) {
            pagePedidos = pedidoService.buscarPedidosFiltradoPaginado(cliente, fechaDesde, fechaHasta, estado, orden, paginaSolicitada, size);
        }
        model.addAttribute("pedidos", pagePedidos.getContent());
        model.addAttribute("paginaActual", paginaSolicitada);
        model.addAttribute("tamanoPagina", pagePedidos.getSize());
        model.addAttribute("totalPaginas", pagePedidos.getTotalPages());
        model.addAttribute("totalElementos", pagePedidos.getTotalElements());
        model.addAttribute("param", new org.springframework.ui.ModelMap()
                .addAttribute("cliente", cliente)
                .addAttribute("fechaDesde", fechaDesde)
                .addAttribute("fechaHasta", fechaHasta)
                .addAttribute("estado", estado)
                .addAttribute("orden", orden));
        return "pedidos";
    }

    /**
     * Muestra el detalle de un pedido.
     */
    @GetMapping("/pedidos/{id}")
    public String pedidoDetalle(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        model.addAttribute("pedido", pedido);
        return "pedido-detalle";
    }

    /**
     * Muestra el carrito del cliente autenticado, permitiendo filtrar por nombre de café o cantidad mínima.
     * <p><b>Nota:</b> Falta implementar la obtención del ID real del cliente autenticado.</p>
     */
    @GetMapping("/carrito")
    public String carrito(Model model, Principal principal,
                         @RequestParam(required = false) String nombre,
                         @RequestParam(required = false) Integer minCantidad) {
        // TODO: Obtener el clienteId real del usuario autenticado desde el contexto de seguridad
        Long clienteId = 1L;
        Carrito carrito = carritoService.obtenerOCrearCarrito(clienteId);
        // Filtrar líneas del carrito si hay búsqueda
        java.util.List<proyecto.tfc.entity.LineaCarrito> lineas = new java.util.ArrayList<>(carrito.getLineas());
        if (nombre != null && !nombre.isEmpty()) {
            lineas = lineas.stream().filter(l -> l.getCafe().getNombre().toLowerCase().contains(nombre.toLowerCase())).toList();
        }
        if (minCantidad != null) {
            lineas = lineas.stream().filter(l -> l.getCantidad() >= minCantidad).toList();
        }
        // Crear un nuevo carrito solo para mostrar las líneas filtradas
        Carrito carritoFiltrado = new Carrito(carrito.getCliente());
        carritoFiltrado.setLineas(lineas);
        model.addAttribute("carrito", carritoFiltrado);
        model.addAttribute("param", new org.springframework.ui.ModelMap()
                .addAttribute("nombre", nombre)
                .addAttribute("minCantidad", minCantidad));
        return "carrito";
    }

    /**
     * Vista de login.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Vista de registro de usuario.
     */
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    /**
     * Procesa el formulario de registro de usuario.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam(required = false) String direccion,
                                   @RequestParam String password,
                                   Model model) {
        try {
            Cliente nuevo = new Cliente(nombre, email, telefono, direccion);
            clienteService.crearCliente(nuevo);
            model.addAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
            return "redirect:/login?registro=ok";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("telefono", telefono);
            model.addAttribute("direccion", direccion);
            return "registro";
        }
    }

    /**
     * Página de error personalizada.
     */
    @GetMapping("/error")
    public String error(Model model, @RequestParam(required = false) String mensaje) {
        model.addAttribute("mensaje", mensaje != null ? mensaje : "Ha ocurrido un error inesperado.");
        return "error";
    }

    /**
     * Formulario para crear un nuevo café.
     */
    @GetMapping("/cafes/nuevo")
    public String nuevoCafeForm(Model model) {
        model.addAttribute("cafe", new Cafe());
        return "cafe-form";
    }

    /**
     * Procesa el formulario de creación de café.
     */
    @PostMapping("/cafes/nuevo")
    public String crearCafe(@ModelAttribute Cafe cafe, RedirectAttributes redirect) {
        try {
            cafeService.crearCafe(cafe);
            redirect.addFlashAttribute("success", "Café creado correctamente.");
            return "redirect:/cafes";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cafes/nuevo";
        }
    }

    /**
     * Formulario para editar un café existente.
     */
    @GetMapping("/cafes/{id}/editar")
    public String editarCafeForm(@PathVariable Long id, Model model) {
        model.addAttribute("cafe", cafeService.obtenerCafePorId(id));
        return "cafe-form";
    }

    /**
     * Procesa el formulario de edición de café.
     */
    @PostMapping("/cafes/{id}/editar")
    public String editarCafe(@PathVariable Long id, @ModelAttribute Cafe cafe, RedirectAttributes redirect) {
        try {
            cafeService.actualizarCafe(id, cafe);
            redirect.addFlashAttribute("success", "Café actualizado correctamente.");
            return "redirect:/cafes";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cafes/" + id + "/editar";
        }
    }

    /**
     * Elimina un café desde la vista web.
     */
    @PostMapping("/cafes/{id}/eliminar")
    public String eliminarCafe(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            cafeService.eliminarCafe(id);
            redirect.addFlashAttribute("success", "Café eliminado correctamente.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cafes";
    }

    /**
     * Formulario para crear un nuevo cliente.
     */
    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-form";
    }

    /**
     * Procesa el formulario de creación de cliente.
     */
    @PostMapping("/clientes/nuevo")
    public String crearCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        try {
            clienteService.crearCliente(cliente);
            redirect.addFlashAttribute("success", "Cliente creado correctamente.");
            return "redirect:/clientes";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/clientes/nuevo";
        }
    }

    /**
     * Formulario para editar un cliente existente.
     */
    @GetMapping("/clientes/{id}/editar")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.obtenerClientePorId(id));
        return "cliente-form";
    }

    /**
     * Procesa el formulario de edición de cliente.
     */
    @PostMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        try {
            clienteService.actualizarCliente(id, cliente);
            redirect.addFlashAttribute("success", "Cliente actualizado correctamente.");
            return "redirect:/clientes";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/clientes/" + id + "/editar";
        }
    }

    /**
     * Elimina un cliente desde la vista web.
     */
    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            clienteService.eliminarCliente(id);
            redirect.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes";
    }

    /**
     * Formulario para crear un nuevo pedido.
     */
    @GetMapping("/pedidos/nuevo")
    public String nuevoPedidoForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("cafes", cafeService.listarCafes());
        return "pedido-form";
    }

    /**
     * Procesa el formulario de creación de pedido.
     */
    @PostMapping("/pedidos/nuevo")
    public String crearPedido(@ModelAttribute Pedido pedido, RedirectAttributes redirect) {
        try {
            // Asignar cliente completo por id
            if (pedido.getCliente() != null && pedido.getCliente().getId() != null) {
                var clienteCompleto = clienteService.obtenerClientePorId(pedido.getCliente().getId());
                pedido.setCliente(clienteCompleto);
            }
            if (pedido.getLineas() != null) {
                for (var linea : pedido.getLineas()) {
                    // Obtener el café completo por id y asignar precio
                    if (linea.getCafe() != null && linea.getCafe().getId() != null) {
                        var cafeCompleto = cafeService.obtenerCafePorId(linea.getCafe().getId());
                        linea.setCafe(cafeCompleto);
                        linea.setPrecioUnitario(cafeCompleto.getPrecio());
                        linea.setSubtotal(cafeCompleto.getPrecio() * linea.getCantidad());
                    }
                    linea.setPedido(pedido);
                }
            }
            pedidoService.crearPedido(pedido);
            redirect.addFlashAttribute("success", "Pedido creado correctamente.");
            return "redirect:/pedidos";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pedidos/nuevo";
        }
    }

    /**
     * Formulario para editar un pedido existente.
     */
    @GetMapping("/pedidos/{id}/editar")
    public String editarPedidoForm(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.obtenerPedidoPorId(id));
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("cafes", cafeService.listarCafes());
        return "pedido-form";
    }

    /**
     * Procesa el formulario de edición de pedido.
     */
    @PostMapping("/pedidos/{id}/editar")
    public String editarPedido(@PathVariable Long id, @ModelAttribute Pedido pedido, RedirectAttributes redirect) {
        try {
            // Asignar cliente completo por id
            if (pedido.getCliente() != null && pedido.getCliente().getId() != null) {
                var clienteCompleto = clienteService.obtenerClientePorId(pedido.getCliente().getId());
                pedido.setCliente(clienteCompleto);
            }
            if (pedido.getLineas() != null) {
                for (var linea : pedido.getLineas()) {
                    // Obtener el café completo por id y asignar precio
                    if (linea.getCafe() != null && linea.getCafe().getId() != null) {
                        var cafeCompleto = cafeService.obtenerCafePorId(linea.getCafe().getId());
                        linea.setCafe(cafeCompleto);
                        linea.setPrecioUnitario(cafeCompleto.getPrecio());
                        linea.setSubtotal(cafeCompleto.getPrecio() * linea.getCantidad());
                    }
                    linea.setPedido(pedido);
                }
            }
            pedidoService.actualizarPedido(id, pedido);
            redirect.addFlashAttribute("success", "Pedido actualizado correctamente.");
            return "redirect:/pedidos";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pedidos/" + id + "/editar";
        }
    }

    /**
     * Elimina un pedido desde la vista web.
     */
    @PostMapping("/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            pedidoService.eliminarPedido(id);
            redirect.addFlashAttribute("success", "Pedido eliminado correctamente.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/pedidos";
    }

    /**
     * Añade un café al carrito desde la vista web.
     */
    @PostMapping("/carrito/anadir")
    public String anadirCarrito(@RequestParam Long idCafe, @RequestParam int cantidad, RedirectAttributes redirect) {
        Long clienteId = 1L; // Demo
        try {
            carritoService.anadirCafe(clienteId, idCafe, cantidad);
            redirect.addFlashAttribute("success", "Café añadido al carrito.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    /**
     * Modifica la cantidad de un café en el carrito desde la vista web.
     */
    @PostMapping("/carrito/modificar")
    public String modificarCarrito(@RequestParam Long idCafe, @RequestParam int cantidad, RedirectAttributes redirect) {
        Long clienteId = 1L; // Demo
        try {
            carritoService.modificarCantidad(clienteId, idCafe, cantidad);
            redirect.addFlashAttribute("success", "Cantidad actualizada.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    /**
     * Elimina un café del carrito desde la vista web.
     */
    @PostMapping("/carrito/eliminar")
    public String eliminarCarrito(@RequestParam Long idCafe, RedirectAttributes redirect) {
        Long clienteId = 1L; // Demo
        try {
            carritoService.eliminarCafe(clienteId, idCafe);
            redirect.addFlashAttribute("success", "Café eliminado del carrito.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    /**
     * Vacía el carrito desde la vista web.
     */
    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(RedirectAttributes redirect) {
        Long clienteId = 1L; // Demo
        try {
            carritoService.vaciarCarrito(clienteId);
            redirect.addFlashAttribute("success", "Carrito vaciado.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    /**
     * Confirma el carrito y crea un pedido desde la vista web.
     */
    @PostMapping("/carrito/confirmar")
    public String confirmarCarrito(RedirectAttributes redirect) {
        Long clienteId = 1L; // Demo
        try {
            carritoService.confirmarCarrito(clienteId);
            redirect.addFlashAttribute("success", "Pedido realizado correctamente.");
            return "redirect:/pedidos";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/carrito";
        }
    }

    /**
     * Listado de líneas de pedido con filtros y paginación para la vista web.
     */
    @GetMapping("/lineas")
    public String lineas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long cafeId,
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) Double subtotal,
            Model model) {
        // Filtros básicos en memoria (puedes adaptar a JPA si lo necesitas)
        java.util.List<proyecto.tfc.entity.LineaDePedido> lineas = new java.util.ArrayList<>(lineaDePedidoService.buscarPorCafe(cafeId != null ? cafeId : -1L));
        if (pedidoId != null) {
            lineas = lineas.stream().filter(l -> l.getPedido() != null && l.getPedido().getId().equals(pedidoId)).toList();
        }
        if (cantidad != null) {
            lineas = lineas.stream().filter(l -> l.getCantidad() > cantidad).toList();
        }
        if (subtotal != null) {
            lineas = lineas.stream().filter(l -> l.getSubtotal() > subtotal).toList();
        }
        int totalElementos = lineas.size();
        int totalPaginas = (int) Math.ceil((double) totalElementos / size);
        int paginaSolicitada = Math.max(1, Math.min(page, Math.max(totalPaginas, 1)));
        int start = Math.min((paginaSolicitada - 1) * size, totalElementos);
        int end = Math.min(start + size, totalElementos);
        java.util.List<proyecto.tfc.entity.LineaDePedido> pageContent = lineas.subList(start, end);
        model.addAttribute("lineas", pageContent);
        model.addAttribute("paginaActual", paginaSolicitada);
        model.addAttribute("tamanoPagina", size);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalElementos", totalElementos);
        model.addAttribute("param", new org.springframework.ui.ModelMap()
                .addAttribute("cafeId", cafeId)
                .addAttribute("pedidoId", pedidoId)
                .addAttribute("cantidad", cantidad)
                .addAttribute("subtotal", subtotal));
        return "lineas";
    }
} 