package proyecto.tfc.repositories.mem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.repositories.ICarritoRepository;

/**
 * Repositorio en memoria para gestionar carritos de compra de los clientes.
 * Utiliza un Map con el clienteId como clave para almacenar el carrito asociado a cada cliente.
 *
 * Permite operaciones básicas de consulta, guardado y eliminación de carritos.
 * Activada solo cuando el perfil "mem" está activo.
 *
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-31
 */
@Repository
@Profile("mem")
public class CarritoRepository implements ICarritoRepository {
    /** Mapa que almacena los carritos usando el clienteId como clave */
    private final Map<Long, Carrito> carritos = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Carrito> obtenerPorCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) return Optional.empty();
        return Optional.ofNullable(carritos.get(cliente.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Carrito guardar(Carrito carrito) {
        if (carrito.getCliente() != null && carrito.getCliente().getId() != null) {
            carritos.put(carrito.getCliente().getId(), carrito);
        }
        return carrito;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(Carrito carrito) {
        if (carrito.getCliente() != null && carrito.getCliente().getId() != null) {
            carritos.remove(carrito.getCliente().getId());
        }
    }

    /**
     * {@inheritDoc}
     * Recupera todos los carritos almacenados.
     * @return colección inmodificable de carritos
     */
    @Override
    public Collection<Carrito> obtenerTodos() {
        return Collections.unmodifiableCollection(carritos.values());
    }
}
