package proyecto.tfc.repositories.jpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Pedido;
import proyecto.tfc.repositories.IPedidoRepository;

/**
 * Implementación JPA del repositorio de pedidos.
 * Permite la gestión completa del recurso Pedido en base de datos relacional
 * usando Spring Data JPA. Implementa la interfaz {@link IPedidoRepository}
 * para desacoplar la lógica de negocio de la fuente de persistencia.
 *
 * Incluye operaciones CRUD, búsquedas por cliente y ordenación por fecha,
 * alineadas con la lógica de la implementación en memoria (usando Map).
 *
 * Esta clase se activa solo cuando el perfil 'jpa' está activo en Spring.
 *
 * @author Lola Fernández
 * @version 1.2
 * @since 2025-05-30
 */
@Repository
@Profile("jpa")
public class PedidoJpaRepositoryImpl implements IPedidoRepository {

    private PedidoJpaRepository jpaRepo;

    @Autowired
    public void setJpaRepo(@Lazy PedidoJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /**
     * {@inheritDoc}
     * Devuelve un mapa inmodificable de todos los pedidos almacenados.
     */
    @Override
    public Map<Long, Pedido> obtenerTodos() {
        List<Pedido> lista = jpaRepo.findAll();
        return lista.stream().collect(Collectors.toMap(Pedido::getId, Function.identity()));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> obtenerPorId(Long id) {
        return jpaRepo.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return jpaRepo.existsById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Pedido guardar(Pedido pedido) {
        return jpaRepo.save(pedido);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> actualizar(Long id, Pedido pedido) {
        if (!jpaRepo.existsById(id)) {
            return Optional.empty();
        }
        pedido.setId(id);
        return Optional.of(jpaRepo.save(pedido));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Pedido> eliminar(Long id) {
        Optional<Pedido> eliminado = jpaRepo.findById(id);
        eliminado.ifPresent(jpaRepo::delete);
        return eliminado;
    }

    /**
     * Devuelve todos los pedidos realizados por un cliente concreto, ordenados por fecha de creación ascendente.
     * El resultado se devuelve como un Map con el ID del pedido como clave.
     * @param clienteId identificador del cliente
     * @return mapa de pedidos del cliente
     */
    public Map<Long, Pedido> buscarPorCliente(Long clienteId) {
        List<Pedido> lista = jpaRepo.findByClienteIdOrderByFechaCreacionAsc(clienteId);
        return lista.stream().collect(Collectors.toMap(Pedido::getId, Function.identity()));
    }

    /**
     * Devuelve todos los pedidos ordenados por fecha de creación ascendente.
     * El resultado se devuelve como un Map con el ID del pedido como clave.
     * @return mapa de pedidos ordenados por fecha
     */
    public Map<Long, Pedido> buscarTodosOrdenadosPorFecha() {
        List<Pedido> lista = jpaRepo.findAllByOrderByFechaCreacionAsc();
        return lista.stream().collect(Collectors.toMap(Pedido::getId, Function.identity()));
    }

    /**
     * Devuelve todos los pedidos realizados por un cliente concreto, ordenados por fecha de creación ascendente.
     * El resultado se devuelve como un Page para paginación.
     * @param clienteId identificador del cliente
     * @param pageable objeto de paginación
     * @return página de pedidos del cliente
     */
    @Override
    public org.springframework.data.domain.Page<Pedido> buscarPorCliente(Long clienteId, org.springframework.data.domain.Pageable pageable) {
        return jpaRepo.findByClienteIdOrderByFechaCreacionAsc(clienteId, pageable);
    }

    @Override
    public Page<Pedido> obtenerTodos(Pageable pageable) {
        return jpaRepo.findAll(pageable);
    }
} 