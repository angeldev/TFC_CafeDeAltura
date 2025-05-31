package proyecto.tfc.repositories.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;

/**
 * Repositorio JPA para operaciones CRUD sobre entidades Carrito.
 * Extiende JpaRepository para obtener funcionalidades básicas de persistencia.
 * 
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-31
 */
@Repository
public interface CarritoJpaRepository extends JpaRepository<Carrito, Long> {
    
    /**
     * Busca un carrito por cliente.
     * 
     * @param cliente el cliente propietario del carrito
     * @return un Optional que contiene el carrito si existe, o vacío si no existe
     */
    Optional<Carrito> findByCliente(Cliente cliente);
    
    /**
     * Obtiene todos los carritos ordenados por fecha de actualización descendente.
     * 
     * @return lista de carritos ordenados por fecha (más recientes primero)
     */
    List<Carrito> findAllByOrderByFechaActualizacionDesc();
} 