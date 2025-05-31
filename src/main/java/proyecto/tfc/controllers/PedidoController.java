package proyecto.tfc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import proyecto.tfc.dto.PedidoDTO;
import proyecto.tfc.dto.RespuestaPaginada;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.services.CafeService;
import proyecto.tfc.services.ClienteService;
import proyecto.tfc.services.PedidoService;

/**
 * Controlador REST para la gestión de pedidos.
 * Proporciona endpoints para operaciones CRUD y búsquedas avanzadas sobre pedidos.
 * Las validaciones y lógica de negocio se delegan al servicio correspondiente.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    private PedidoService pedidoService;
    private final ClienteService clienteService;
    private final CafeService cafeService;

    @Autowired
    public void setPedidoService(@Lazy PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public PedidoController(ClienteService clienteService, CafeService cafeService) {
        this.clienteService = clienteService;
        this.cafeService = cafeService;
    }

    /**
     * Devuelve la lista de todos los pedidos registrados, ordenados por fecha de creación ascendente.
     *
     * @return 200 OK con la colección de pedidos
     */
    @GetMapping
    public ResponseEntity<RespuestaPaginada<Pedido>> listarTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1 || size < 1) {
            throw new proyecto.tfc.exceptions.BadRequestException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }
        log.info("[GET] /pedidos - Listando pedidos paginados (page={}, size={})", page, size);
        Page<Pedido> pagePedidos = pedidoService.listarPedidosPaginado(page, size);
        RespuestaPaginada.MetadatosPaginacion meta = new RespuestaPaginada.MetadatosPaginacion(
                pagePedidos.getNumber() + 1,
                pagePedidos.getSize(),
                pagePedidos.getTotalPages(),
                pagePedidos.getTotalElements(),
                pagePedidos.isLast(),
                pagePedidos.isFirst()
        );
        RespuestaPaginada<Pedido> respuesta = new RespuestaPaginada<>(pagePedidos.getContent(), meta);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Devuelve todos los pedidos realizados por un cliente concreto, ordenados por fecha de creación ascendente.
     *
     * @param clienteId identificador del cliente
     * @return 200 OK con la lista de pedidos del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<RespuestaPaginada<Pedido>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1 || size < 1) {
            throw new proyecto.tfc.exceptions.BadRequestException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }
        log.info("[GET] /pedidos/cliente/{} - Listando pedidos por cliente paginados (page={}, size={})", clienteId, page, size);
        Page<Pedido> pagePedidos = pedidoService.listarPedidosPorClientePaginado(clienteId, page, size);
        RespuestaPaginada.MetadatosPaginacion meta = new RespuestaPaginada.MetadatosPaginacion(
                pagePedidos.getNumber() + 1,
                pagePedidos.getSize(),
                pagePedidos.getTotalPages(),
                pagePedidos.getTotalElements(),
                pagePedidos.isLast(),
                pagePedidos.isFirst()
        );
        RespuestaPaginada<Pedido> respuesta = new RespuestaPaginada<>(pagePedidos.getContent(), meta);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Devuelve un pedido específico por su ID.
     *
     * @param id identificador del pedido
     * @return 200 OK con el pedido o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        log.info("[GET] /pedidos/{} - Buscando pedido", id);
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        log.info("[GET] /pedidos/{} - Pedido encontrado: {}", id, pedido);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Crea un nuevo pedido a partir del JSON recibido.
     *
     * @param pedidoDTO objeto recibido en el cuerpo de la petición
     * @return 201 Created si es válido, 400 Bad Request si no lo es
     */
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        // Mapear PedidoDTO a Pedido
        Pedido pedido = new Pedido();
        pedido.setEstado(pedidoDTO.getEstado());
        pedido.setComentario(pedidoDTO.getComentario());
        // Asignar cliente
        pedido.setCliente(clienteService.obtenerClientePorId(pedidoDTO.getCliente().getId()));
        // Mapear líneas
        java.util.List<proyecto.tfc.entity.LineaDePedido> lineas = new java.util.ArrayList<>();
        for (PedidoDTO.LineaDTO lineaDTO : pedidoDTO.getLineas()) {
            proyecto.tfc.entity.Cafe cafe = cafeService.obtenerCafePorId(lineaDTO.getCafe().getId());
            proyecto.tfc.entity.LineaDePedido linea = new proyecto.tfc.entity.LineaDePedido(
                cafe,
                cafe.getPrecio(),
                lineaDTO.getCantidad()
            );
            lineas.add(linea);
        }
        pedido.setLineas(lineas);
        Pedido creado = pedidoService.crearPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Elimina un pedido por su identificador.
     *
     * @param id ID del pedido a eliminar
     * @return 200 OK si se elimina, 404 Not Found si no existe
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        log.info("[DELETE] /pedidos/{} - Eliminando pedido", id);
        pedidoService.eliminarPedido(id);
        log.info("[DELETE] /pedidos/{} - Pedido eliminado", id);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /pedidos/porEstado?estado=ENVIADO
     * Busca pedidos por estado exacto.
     * Ejemplo: /pedidos/porEstado?estado=ENVIADO
     */
    @GetMapping("/porEstado")
    public ResponseEntity<java.util.List<Pedido>> buscarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.buscarPorEstado(estado));
    }

    /**
     * GET /pedidos/rangoFechas?desde=2024-06-01T00:00:00&hasta=2024-06-30T23:59:59
     * Busca pedidos realizados entre dos fechas (formato ISO-8601).
     * Ejemplo: /pedidos/rangoFechas?desde=2024-06-01T00:00:00&hasta=2024-06-30T23:59:59
     */
    @GetMapping("/rangoFechas")
    public ResponseEntity<java.util.List<Pedido>> buscarPorRangoDeFechas(@RequestParam String desde, @RequestParam String hasta) {
        java.time.LocalDateTime d = java.time.LocalDateTime.parse(desde);
        java.time.LocalDateTime h = java.time.LocalDateTime.parse(hasta);
        return ResponseEntity.ok(pedidoService.buscarPorRangoDeFechas(d, h));
    }

    /**
     * GET /pedidos/totalMayorA?importe=50.0
     * Busca pedidos cuyo total es mayor a un importe dado.
     * Ejemplo: /pedidos/totalMayorA?importe=50.0
     */
    @GetMapping("/totalMayorA")
    public ResponseEntity<java.util.List<Pedido>> buscarPorTotalMayorA(@RequestParam double importe) {
        return ResponseEntity.ok(pedidoService.buscarPorTotalMayorA(importe));
    }

    /**
     * GET /pedidos/conComentario
     * Busca pedidos que tienen comentario no nulo y no vacío.
     * Ejemplo: /pedidos/conComentario
     */
    @GetMapping("/conComentario")
    public ResponseEntity<java.util.List<Pedido>> buscarPedidosConComentario() {
        return ResponseEntity.ok(pedidoService.buscarPedidosConComentario());
    }
}
