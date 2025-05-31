package proyecto.tfc.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.tfc.entity.Cliente;

/**
 * Repositorio JPA para la entidad Cliente.
 * Permite operaciones CRUD sobre la base de datos PostgreSQL usando Spring Data JPA.
 *
 * @author Lola Fernández
 */
public interface ClienteJpaRepository extends JpaRepository<Cliente, Long> {
    /**
     * Busca clientes cuyo nombre contiene un fragmento (ignorando mayúsculas/minúsculas).
     * @param fragmento parte del nombre a buscar
     * @return lista de clientes que coinciden
     */
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :fragmento, '%'))")
    java.util.List<Cliente> buscarPorNombreConteniendo(@org.springframework.data.repository.query.Param("fragmento") String fragmento);

    /**
     * Busca clientes registrados después de una fecha dada.
     * @param fecha fecha límite inferior
     * @return lista de clientes
     */
    java.util.List<Cliente> findByFechaRegistroAfter(java.time.LocalDateTime fecha);

    /**
     * Busca clientes activos cuyo email parece verificado (contiene '@').
     * @return lista de clientes activos y con email válido
     */
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Cliente c WHERE c.activo = true AND c.email LIKE '%@%'")
    java.util.List<Cliente> buscarActivosConEmailVerificado();

    /**
     * Busca clientes que han realizado al menos un pedido.
     * @return lista de clientes con pedidos
     */
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Cliente c WHERE EXISTS (SELECT 1 FROM Pedido p WHERE p.cliente = c)")
    java.util.List<Cliente> buscarClientesConPedidos();

} 