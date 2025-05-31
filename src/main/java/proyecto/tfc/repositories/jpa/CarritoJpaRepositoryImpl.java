package proyecto.tfc.repositories.jpa;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.repositories.ICarritoRepository;

/**
 * Implementación JPA del repositorio de carritos.
 * Esta clase adapta la interfaz específica de JPA a la interfaz común ICarritoRepository.
 * Activada solo cuando el perfil "jpa" está activo.
 *
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-31
 */
@Repository
@Profile("jpa")
public class CarritoJpaRepositoryImpl implements ICarritoRepository {
    
    /**
     * Repositorio JPA específico para operaciones de Carrito
     */
    private CarritoJpaRepository carritoJpaRepository;

    @Autowired
    public void setCarritoJpaRepository(@Lazy CarritoJpaRepository carritoJpaRepository) {
        this.carritoJpaRepository = carritoJpaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Carrito> obtenerPorCliente(Cliente cliente) {
        return carritoJpaRepository.findByCliente(cliente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Carrito guardar(Carrito carrito) {
        return carritoJpaRepository.save(carrito);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(Carrito carrito) {
        carritoJpaRepository.delete(carrito);
    }
    
    /**
     * {@inheritDoc}
     * Obtiene todos los carritos ordenados por fecha de actualización descendente.
     */
    @Override
    public Collection<Carrito> obtenerTodos() {
        return carritoJpaRepository.findAllByOrderByFechaActualizacionDesc();
    }
} 