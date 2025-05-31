package proyecto.tfc.repositories.mem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Pedido;
import proyecto.tfc.repositories.IPedidoRepository;

/**
 * Este repositorio solo se activa cuando el perfil de Spring activo es "mem".
 * Permite alternar entre persistencia en memoria y JPA según el entorno de ejecución.
 * La anotación @Profile("mem") asegura que este bean no se cree si el perfil activo es otro.
 *
 * Repositorio en memoria para gestionar entidades de tipo {@link Pedido}.
 * Implementa la interfaz {@link IPedidoRepository} para permitir el desacoplamiento
 * de la lógica de negocio respecto a la fuente de persistencia.
 *
 * Utiliza un {@code Map} como almacén temporal simulado sin persistencia real.
 * El ID se asigna automáticamente. El acceso externo a los datos está protegido
 * devolviendo estructuras inmodificables.
 *
 * Se incluye trazabilidad mediante Logger para facilitar el diagnóstico.
 *
 * @author Lola Fernández
 * @version 1.2
 * @since 2025-05-30
 */
@Profile("mem")
@Repository
public class PedidoRepository implements IPedidoRepository {

    private static final Logger log = LoggerFactory.getLogger(PedidoRepository.class);

    /** Mapa que almacena los pedidos usando el ID como clave */
    private final Map<Long, Pedido> pedidos = new HashMap<>();

    /** Secuencia para generar IDs únicos en memoria */
    private Long secuenciaId = 1L;

    /**
     * {@inheritDoc}
     * Devuelve un mapa inmodificable de todos los pedidos almacenados.
     */
    @Override
    public Map<Long, Pedido> obtenerTodos() {
        log.info("Recuperando todos los pedidos ({} encontrados)", pedidos.size());
        return Collections.unmodifiableMap(pedidos);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> obtenerPorId(Long id) {
        Pedido pedido = pedidos.get(id);
        if (pedido != null) {
            log.info("Pedido encontrado con ID {}: {}", id, pedido);
        } else {
            log.warn("No se encontró pedido con ID {}", id);
        }
        return Optional.ofNullable(pedido);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return pedidos.containsKey(id);
    }

    /** {@inheritDoc} */
    @Override
    public Pedido guardar(Pedido pedido) {
        if (pedido.getId() == null) {
            pedido.setId(generarNuevoId());
        }
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(java.time.LocalDateTime.now());
        }
        pedidos.put(pedido.getId(), pedido);
        log.info("Pedido guardado: {}", pedido);
        return pedido;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> actualizar(Long id, Pedido pedido) {
        if (!pedidos.containsKey(id) || pedido == null) {
            log.warn("No se pudo actualizar: pedido inexistente o entrada nula (ID {}).", id);
            return Optional.empty();
        }
        pedido.setId(id);
        pedidos.put(id, pedido);
        log.info("Pedido actualizado con ID {}: {}", id, pedido);
        return Optional.of(pedido);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> eliminar(Long id) {
        Pedido eliminado = pedidos.remove(id);
        if (eliminado == null) {
            log.warn("Intento de eliminar pedido inexistente con ID {}.", id);
            return Optional.empty();
        }
        log.info("Pedido eliminado con ID {}: {}", id, eliminado);
        return Optional.of(eliminado);
    }

    /**
     * Genera un nuevo ID único para el siguiente pedido.
     *
     * @return ID autogenerado
     */
    private Long generarNuevoId() {
        return secuenciaId++;
    }

    @Override
    public Page<Pedido> obtenerTodos(Pageable pageable) {
        java.util.List<Pedido> lista = new java.util.ArrayList<>(pedidos.values());
        int total = lista.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        java.util.List<Pedido> sublist = (start > end) ? java.util.Collections.emptyList() : lista.subList(start, end);
        return new PageImpl<>(sublist, pageable, total);
    }

    @Override
    public org.springframework.data.domain.Page<Pedido> buscarPorCliente(Long clienteId, org.springframework.data.domain.Pageable pageable) {
        java.util.List<Pedido> lista = pedidos.values().stream()
            .filter(p -> p.getCliente() != null && p.getCliente().getId() != null && p.getCliente().getId().equals(clienteId))
            .sorted(java.util.Comparator.comparing(Pedido::getFechaCreacion))
            .toList();
        int total = lista.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        java.util.List<Pedido> sublist = (start > end) ? java.util.Collections.emptyList() : lista.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(sublist, pageable, total);
    }
} 