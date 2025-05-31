package proyecto.tfc.repositories;

import java.util.Collection;
import java.util.Optional;

import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;

/**
 * Interfaz para repositorio de carritos de compra.
 * Define operaciones comunes para la persistencia de carritos,
 * independientemente de la implementación (memoria o JPA).
 *
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-31
 */
public interface ICarritoRepository {
    
    /**
     * Busca un carrito por cliente.
     * 
     * @param cliente el cliente propietario del carrito
     * @return un Optional que contiene el carrito si existe, o vacío si no existe
     */
    Optional<Carrito> obtenerPorCliente(Cliente cliente);
    
    /**
     * Guarda o actualiza un carrito.
     * 
     * @param carrito el carrito a guardar o actualizar
     * @return el carrito guardado con posibles cambios (como ID generado)
     */
    Carrito guardar(Carrito carrito);
    
    /**
     * Elimina un carrito.
     * 
     * @param carrito el carrito a eliminar
     */
    void eliminar(Carrito carrito);
    
    /**
     * Obtiene todos los carritos.
     * 
     * @return colección de todos los carritos disponibles
     */
    Collection<Carrito> obtenerTodos();
} 