package proyecto.tfc.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.tfc.entity.Cafe;

/**
 * Repositorio JPA para la entidad Cafe.
 * Permite operaciones CRUD sobre la base de datos PostgreSQL usando Spring Data JPA.
 * Incluye consultas personalizadas mediante @Query.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-26
 */
public interface CafeJpaRepository extends JpaRepository<Cafe, Long> {
    /**
     * Busca cafés por su origen usando una consulta JPQL personalizada.
     * @param origen país o región de origen
     * @return lista de cafés de ese origen
     */
    @Query("SELECT c FROM Cafe c WHERE c.origen = :origen")
    List<Cafe> buscarPorOrigen(@Param("origen") String origen);

    /**
     * Busca cafés cuyo stock es menor a un umbral dado.
     * @param umbral valor máximo de stock
     * @return lista de cafés con stock bajo
     */
    java.util.List<Cafe> findByStockLessThan(int umbral);

    /**
     * Busca cafés cuya intensidad está en un rango dado (inclusive).
     * @param min intensidad mínima
     * @param max intensidad máxima
     * @return lista de cafés en ese rango de intensidad
     */
    java.util.List<Cafe> findByIntensidadBetween(int min, int max);

    /**
     * Busca cafés cuyo nombre o descripción contiene una palabra clave (ignorando mayúsculas/minúsculas).
     * @param palabra palabra clave
     * @return lista de cafés
     */
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Cafe c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :palabra, '%'))")
    java.util.List<Cafe> buscarPorNombreODescripcion(@org.springframework.data.repository.query.Param("palabra") String palabra);

    /**
     * Busca cafés que están agotados (stock = 0).
     * @return lista de cafés sin stock
     */
    java.util.List<Cafe> findByStockEquals(int stock);
} 