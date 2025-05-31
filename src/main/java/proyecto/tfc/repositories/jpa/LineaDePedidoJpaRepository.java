package proyecto.tfc.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.tfc.entity.LineaDePedido;

/**
 * Repositorio JPA para la entidad LineaDePedido.
 * Permite operaciones CRUD sobre la base de datos PostgreSQL usando Spring Data JPA.
 *
 * @author Lola Fernández
 */
public interface LineaDePedidoJpaRepository extends JpaRepository<LineaDePedido, Long> {
    /**
     * Busca líneas de pedido asociadas a un café concreto.
     * @param cafeId identificador del café
     * @return lista de líneas de pedido
     */
    java.util.List<LineaDePedido> findByCafeId(Long cafeId);

    /**
     * Busca líneas de pedido asociadas a un pedido concreto.
     * @param pedidoId identificador del pedido
     * @return lista de líneas de pedido
     */
    java.util.List<LineaDePedido> findByPedidoId(Long pedidoId);

    /**
     * Busca líneas de pedido con cantidad mayor a un valor dado.
     * @param cantidad mínima
     * @return lista de líneas de pedido
     */
    java.util.List<LineaDePedido> findByCantidadGreaterThan(int cantidad);

    /**
     * Busca líneas de pedido con subtotal mayor a un importe dado.
     * @param subtotal mínimo
     * @return lista de líneas de pedido
     */
    java.util.List<LineaDePedido> findBySubtotalGreaterThan(double subtotal);

} 