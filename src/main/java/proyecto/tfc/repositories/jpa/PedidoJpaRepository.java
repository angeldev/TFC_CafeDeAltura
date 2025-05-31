package proyecto.tfc.repositories.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import proyecto.tfc.entity.Pedido;

/**
 * Repositorio JPA para la entidad Pedido.
 * Permite operaciones CRUD sobre la base de datos PostgreSQL usando Spring Data JPA.
 *
 * @author Lola Fernández
 */
public interface PedidoJpaRepository extends JpaRepository<Pedido, Long> {
    /**
     * Busca todos los pedidos de un cliente concreto, ordenados por fecha de creación ascendente.
     * @param clienteId identificador del cliente
     * @return lista de pedidos
     */
    List<Pedido> findByClienteIdOrderByFechaCreacionAsc(Long clienteId);

    /**
     * Busca todos los pedidos ordenados por fecha de creación ascendente.
     * @return lista de pedidos
     */
    List<Pedido> findAllByOrderByFechaCreacionAsc();

    /**
     * Busca todos los pedidos de un cliente concreto, ordenados por fecha de creación ascendente.
     * @param clienteId identificador del cliente
     * @param pageable objeto de paginación
     * @return página de pedidos
     */
    Page<Pedido> findByClienteIdOrderByFechaCreacionAsc(Long clienteId, Pageable pageable);

    /**
     * Busca pedidos por estado exacto.
     * @param estado estado del pedido
     * @return lista de pedidos
     */
    List<Pedido> findByEstado(String estado);

    /**
     * Busca pedidos realizados entre dos fechas (inclusive).
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return lista de pedidos
     */
    List<Pedido> findByFechaCreacionBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    /**
     * Busca pedidos cuyo total es mayor a un importe dado.
     * @param importe mínimo
     * @return lista de pedidos
     */
    List<Pedido> findByTotalGreaterThan(double importe);

    /**
     * Busca pedidos que tienen comentario no nulo y no vacío.
     * @return lista de pedidos con comentario
     */
    @Query("SELECT p FROM Pedido p WHERE p.comentario IS NOT NULL AND p.comentario <> ''")
    List<Pedido> buscarPedidosConComentario();

} 