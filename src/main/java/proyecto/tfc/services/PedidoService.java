package proyecto.tfc.services;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.entity.LineaDePedido;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.exceptions.BadRequestException;
import proyecto.tfc.exceptions.ResourceNotFoundException;
import proyecto.tfc.repositories.ICafeRepository;
import proyecto.tfc.repositories.IClienteRepository;
import proyecto.tfc.repositories.IPedidoRepository;

/**
 * Servicio de negocio para la gestión de pedidos.
 * Valida los datos y coordina la lógica entre el controlador y el repositorio.
 * Incluye validaciones manuales y logging profesional.
 *
 * <p>Esta clase es independiente de la tecnología de persistencia (memoria o JPA).</p>
 *
 * <b>Reglas de validación:</b>
 * <ul>
 *   <li>El cliente debe existir.</li>
 *   <li>Todas las líneas deben tener cafés existentes y cantidades &gt; 0.</li>
 *   <li>No se permiten líneas duplicadas ni pedidos vacíos.</li>
 *   <li>El total se calcula automáticamente.</li>
 * </ul>
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private IPedidoRepository pedidoRepository;
    private IClienteRepository clienteRepository;
    private ICafeRepository cafeRepository;

    @Autowired
    public void setPedidoRepository(@Lazy IPedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
    @Autowired
    public void setClienteRepository(@Lazy IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    @Autowired
    public void setCafeRepository(@Lazy ICafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    /**
     * Devuelve todos los pedidos registrados en el sistema.
     *
     * @return colección inmodificable de objetos Pedido
     */
    public Collection<Pedido> listarPedidos() {
        log.info("Listando todos los pedidos");
        return pedidoRepository.obtenerTodos().values();
    }

    /**
     * Busca un pedido existente por su identificador.
     *
     * @param id identificador único del pedido
     * @return el pedido si existe
     * @throws ResourceNotFoundException si el pedido no existe
     */
    public Pedido obtenerPedidoPorId(Long id) {
        log.info("Buscando pedido con id: {}", id);
        return pedidoRepository.obtenerPorId(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado con id: {}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                });
    }

    /**
     * Crea un nuevo pedido tras validar todos sus atributos y calcular el total.
     *
     * @param pedido objeto Pedido sin ID
     * @return el pedido creado
     * @throws BadRequestException si los datos no son válidos
     */
    public Pedido crearPedido(Pedido pedido) {
        log.info("Intentando crear pedido: {}", pedido);
        // Validar cliente
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null || !clienteRepository.existePorId(pedido.getCliente().getId())) {
            log.warn("Intento de crear pedido con cliente inexistente");
            throw new BadRequestException("El cliente no existe");
        }
        // Validar líneas
        if (pedido.getLineas() == null || pedido.getLineas().isEmpty()) {
            throw new BadRequestException("El pedido debe tener al menos una línea");
        }
        java.util.Set<Long> cafesEnPedido = new java.util.HashSet<>();
        double total = 0.0;
        for (LineaDePedido linea : pedido.getLineas()) {
            if (linea.getCantidad() <= 0) {
                throw new BadRequestException("La cantidad debe ser mayor que cero");
            }
            if (linea.getCafe() == null || linea.getCafe().getId() == null || !cafeRepository.existePorId(linea.getCafe().getId())) {
                log.warn("Intento de crear pedido con café inexistente");
                throw new BadRequestException("Uno o más cafés no existen");
            }
            if (!cafesEnPedido.add(linea.getCafe().getId())) {
                log.warn("Café duplicado en pedido");
                throw new BadRequestException("No se permiten cafés duplicados en el pedido");
            }
            Cafe cafe = cafeRepository.obtenerPorId(linea.getCafe().getId()).get();
            if (cafe.getNombre() == null || cafe.getNombre().isEmpty()) {
                throw new BadRequestException("El nombre del café no puede ser nulo ni vacío en la línea de pedido");
            }
            if (cafe.getPrecio() == null) {
                throw new BadRequestException("El precio unitario no puede ser nulo en la línea de pedido");
            }
            linea.setPedido(pedido);
            linea.setPrecioUnitario(cafe.getPrecio());
            linea.setSubtotal(cafe.getPrecio() * linea.getCantidad());
            total += linea.getSubtotal();
        }
        if (total < 0) {
            throw new BadRequestException("El total del pedido debe ser mayor o igual a cero");
        }
        // Validar estado permitido
        java.util.Set<String> estadosPermitidos = java.util.Set.of("PENDIENTE", "ENVIADO", "CANCELADO");
        if (pedido.getEstado() == null || !estadosPermitidos.contains(pedido.getEstado())) {
            throw new BadRequestException("El estado del pedido no es válido");
        }
        // Validar comentario
        if (pedido.getComentario() != null) {
            if (pedido.getComentario().length() > 255) {
                throw new BadRequestException("El comentario no puede superar los 255 caracteres");
            }
            if (pedido.getComentario().matches(".*[\\r\\n\\t].*")) {
                throw new BadRequestException("El comentario contiene caracteres no permitidos");
            }
        }
        pedido.setTotal(total);
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(java.time.LocalDateTime.now());
        }
        Pedido creado = pedidoRepository.guardar(pedido);
        log.info("Pedido creado correctamente: {}", creado);
        return creado;
    }

    /**
     * Elimina un pedido por su ID si existe en el sistema.
     *
     * @param id identificador del pedido a eliminar
     * @return el pedido eliminado
     * @throws ResourceNotFoundException si el pedido no existe
     */
    public Pedido eliminarPedido(Long id) {
        log.info("Intentando eliminar pedido con id: {}", id);
        Pedido eliminado = pedidoRepository.eliminar(id)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminar pedido no existente con id: {}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                });
        log.info("Pedido eliminado correctamente con id {}: {}", id, eliminado);
        return eliminado;
    }

    /**
     * Devuelve todos los pedidos registrados en el sistema, ordenados por fecha de creación ascendente.
     *
     * @return lista de objetos Pedido ordenados por fecha
     */
    public List<Pedido> listarPedidosOrdenadosPorFecha() {
        log.info("Listando todos los pedidos ordenados por fecha de creación");
        return pedidoRepository.obtenerTodos().values().stream()
                .sorted(java.util.Comparator.comparing(Pedido::getFechaCreacion))
                .toList();
    }

    /**
     * Devuelve todos los pedidos realizados por un cliente concreto, ordenados por fecha de creación ascendente.
     *
     * @param clienteId identificador del cliente
     * @return lista de pedidos del cliente ordenados por fecha
     */
    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        log.info("Listando pedidos para el cliente con id: {}", clienteId);
        return pedidoRepository.obtenerTodos().values().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId() != null && p.getCliente().getId().equals(clienteId))
                .sorted(java.util.Comparator.comparing(Pedido::getFechaCreacion))
                .toList();
    }

    /**
     * Devuelve una página de pedidos según el perfil activo (mem o jpa).
     *
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de pedidos
     */
    public Page<Pedido> listarPedidosPaginado(int page, int size) {
        return pedidoRepository.obtenerTodos(PageRequest.of(page - 1, size));
    }

    /**
     * Devuelve una página de pedidos de un cliente concreto.
     *
     * @param clienteId identificador del cliente
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de pedidos del cliente
     */
    public org.springframework.data.domain.Page<Pedido> listarPedidosPorClientePaginado(Long clienteId, int page, int size) {
        return pedidoRepository.buscarPorCliente(clienteId, org.springframework.data.domain.PageRequest.of(page - 1, size));
    }

    /**
     * Busca pedidos por estado exacto.
     *
     * @param estado estado del pedido
     * @return lista de pedidos
     */
    public java.util.List<Pedido> buscarPorEstado(String estado) {
        return pedidoRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.PedidoJpaRepository) pedidoRepository).findByEstado(estado)
            : java.util.Collections.emptyList();
    }

    /**
     * Busca pedidos realizados entre dos fechas (inclusive).
     *
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return lista de pedidos
     */
    public java.util.List<Pedido> buscarPorRangoDeFechas(java.time.LocalDateTime desde, java.time.LocalDateTime hasta) {
        return pedidoRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.PedidoJpaRepository) pedidoRepository).findByFechaCreacionBetween(desde, hasta)
            : java.util.Collections.emptyList();
    }

    /**
     * Busca pedidos cuyo total es mayor a un importe dado.
     *
     * @param importe mínimo
     * @return lista de pedidos
     */
    public java.util.List<Pedido> buscarPorTotalMayorA(double importe) {
        return pedidoRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.PedidoJpaRepository) pedidoRepository).findByTotalGreaterThan(importe)
            : java.util.Collections.emptyList();
    }

    /**
     * Busca pedidos que tienen comentario no nulo y no vacío.
     *
     * @return lista de pedidos con comentario
     */
    public java.util.List<Pedido> buscarPedidosConComentario() {
        return pedidoRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.PedidoJpaRepository) pedidoRepository).buscarPedidosConComentario()
            : java.util.Collections.emptyList();
    }

    /**
     * Actualiza un pedido existente con los nuevos datos proporcionados.
     * @param id identificador del pedido a actualizar
     * @param pedido datos nuevos del pedido
     * @return el pedido actualizado
     * @throws ResourceNotFoundException si el pedido no existe
     * @throws BadRequestException si los datos no son válidos
     */
    public Pedido actualizarPedido(Long id, Pedido pedido) {
        log.info("Intentando actualizar pedido con id {}: {}", id, pedido);
        // Validar existencia
        Pedido existente = obtenerPedidoPorId(id);
        // Validar cliente
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null || !clienteRepository.existePorId(pedido.getCliente().getId())) {
            throw new BadRequestException("El cliente no existe");
        }
        // Validar líneas
        if (pedido.getLineas() == null || pedido.getLineas().isEmpty()) {
            throw new BadRequestException("El pedido debe tener al menos una línea");
        }
        java.util.Set<Long> cafesEnPedido = new java.util.HashSet<>();
        double total = 0.0;
        for (var linea : pedido.getLineas()) {
            if (linea.getCantidad() <= 0) {
                throw new BadRequestException("La cantidad debe ser mayor que cero");
            }
            if (linea.getCafe() == null || linea.getCafe().getId() == null || !cafeRepository.existePorId(linea.getCafe().getId())) {
                throw new BadRequestException("Uno o más cafés no existen");
            }
            if (!cafesEnPedido.add(linea.getCafe().getId())) {
                throw new BadRequestException("No se permiten cafés duplicados en el pedido");
            }
            Cafe cafe = cafeRepository.obtenerPorId(linea.getCafe().getId()).get();
            if (cafe.getNombre() == null || cafe.getNombre().isEmpty()) {
                throw new BadRequestException("El nombre del café no puede ser nulo ni vacío en la línea de pedido");
            }
            if (cafe.getPrecio() == null) {
                throw new BadRequestException("El precio unitario no puede ser nulo en la línea de pedido");
            }
            linea.setPedido(pedido);
            linea.setPrecioUnitario(cafe.getPrecio());
            linea.setSubtotal(cafe.getPrecio() * linea.getCantidad());
            total += linea.getSubtotal();
        }
        if (total < 0) {
            throw new BadRequestException("El total del pedido debe ser mayor o igual a cero");
        }
        // Validar estado permitido
        java.util.Set<String> estadosPermitidos = java.util.Set.of("PENDIENTE", "ENVIADO", "CANCELADO");
        if (pedido.getEstado() == null || !estadosPermitidos.contains(pedido.getEstado())) {
            throw new BadRequestException("El estado del pedido no es válido");
        }
        // Validar comentario
        if (pedido.getComentario() != null) {
            if (pedido.getComentario().length() > 255) {
                throw new BadRequestException("El comentario no puede superar los 255 caracteres");
            }
            if (pedido.getComentario().matches(".*[\\r\\n\\t].*")) {
                throw new BadRequestException("El comentario contiene caracteres no permitidos");
            }
        }
        pedido.setTotal(total);
        pedido.setId(id);
        pedido.setFechaCreacion(existente.getFechaCreacion()); // Mantener la fecha original
        var actualizado = pedidoRepository.actualizar(id, pedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        log.info("Pedido actualizado correctamente: {}", actualizado);
        return actualizado;
    }

    /**
     * Busca pedidos con filtros y paginación real.
     * @param cliente nombre del cliente
     * @param fechaDesde fecha desde
     * @param fechaHasta fecha hasta
     * @param estado estado del pedido
     * @param orden campo de ordenación
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de pedidos filtrados
     */
    public org.springframework.data.domain.Page<Pedido> buscarPedidosFiltradoPaginado(String cliente, String fechaDesde, String fechaHasta, String estado, String orden, int page, int size) {
        org.springframework.data.domain.Pageable pageable;
        if (orden != null) {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(orden));
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        }
        java.util.List<Pedido> filtrados = new java.util.ArrayList<>(pedidoRepository.obtenerTodos().values());
        if (cliente != null && !cliente.isEmpty()) {
            filtrados = filtrados.stream().filter(p -> p.getCliente() != null && p.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase())).toList();
        }
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            java.time.LocalDate desde = java.time.LocalDate.parse(fechaDesde);
            filtrados = filtrados.stream().filter(p -> p.getFechaCreacion() != null && (p.getFechaCreacion().toLocalDate().isAfter(desde) || p.getFechaCreacion().toLocalDate().isEqual(desde))).toList();
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            java.time.LocalDate hasta = java.time.LocalDate.parse(fechaHasta);
            filtrados = filtrados.stream().filter(p -> p.getFechaCreacion() != null && (p.getFechaCreacion().toLocalDate().isBefore(hasta) || p.getFechaCreacion().toLocalDate().isEqual(hasta))).toList();
        }
        if (estado != null && !estado.isEmpty()) {
            filtrados = filtrados.stream().filter(p -> p.getEstado() != null && p.getEstado().equalsIgnoreCase(estado)).toList();
        }
        if (orden != null) {
            switch (orden) {
                case "fecha":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Pedido::getFechaCreacion)).toList();
                    break;
                case "total":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Pedido::getTotal)).toList();
                    break;
            }
        }
        int start = Math.min((page - 1) * size, filtrados.size());
        int end = Math.min(start + size, filtrados.size());
        java.util.List<Pedido> pageContent = filtrados.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtrados.size());
    }
}
