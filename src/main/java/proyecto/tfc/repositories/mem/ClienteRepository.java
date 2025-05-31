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

import proyecto.tfc.entity.Cliente;
import proyecto.tfc.repositories.IClienteRepository;

/**
 * Este repositorio solo se activa cuando el perfil de Spring activo es "mem".
 * Permite alternar entre persistencia en memoria y JPA según el entorno de ejecución.
 * La anotación @Profile("mem") asegura que este bean no se cree si el perfil activo es otro.
 *
 * Repositorio en memoria para gestionar entidades de tipo {@link Cliente}.
 * Implementa la interfaz {@link IClienteRepository} para permitir el desacoplamiento
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
public class ClienteRepository implements IClienteRepository {

    private static final Logger log = LoggerFactory.getLogger(ClienteRepository.class);

    /** Mapa que almacena los clientes usando el ID como clave */
    private final Map<Long, Cliente> clientes = new HashMap<>();

    /** Secuencia para generar IDs únicos en memoria */
    private Long secuenciaId = 1L;

    /**
     * {@inheritDoc}
     * Devuelve un mapa inmodificable de todos los clientes almacenados.
     */
    @Override
    public Map<Long, Cliente> obtenerTodos() {
        log.info("Recuperando todos los clientes ({} encontrados)", clientes.size());
        return Collections.unmodifiableMap(clientes);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> obtenerPorId(Long id) {
        Cliente cliente = clientes.get(id);
        if (cliente != null) {
            log.info("Cliente encontrado con ID {}: {}", id, cliente);
        } else {
            log.warn("No se encontró cliente con ID {}", id);
        }
        return Optional.ofNullable(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return clientes.containsKey(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente guardar(Cliente cliente) {
        cliente.setId(generarNuevoId());
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(java.time.LocalDateTime.now());
        }
        clientes.put(cliente.getId(), cliente);
        log.info("Cliente guardado: {}", cliente);
        return cliente;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> actualizar(Long id, Cliente cliente) {
        if (cliente == null || !clientes.containsKey(id)) {
            log.warn("No se pudo actualizar: cliente inexistente o entrada nula (ID {}).", id);
            return Optional.empty();
        }
        cliente.setId(id);
        clientes.put(id, cliente);
        log.info("Cliente actualizado con ID {}: {}", id, cliente);
        return Optional.of(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> eliminar(Long id) {
        Cliente eliminado = clientes.remove(id);
        if (eliminado == null) {
            log.warn("Intento de eliminar cliente inexistente con ID {}.", id);
            return Optional.empty();
        }
        log.info("Cliente eliminado con ID {}: {}", id, eliminado);
        return Optional.of(eliminado);
    }

    /**
     * Genera un nuevo ID único para el siguiente cliente.
     *
     * @return ID autogenerado
     */
    private Long generarNuevoId() {
        return secuenciaId++;
    }

    @Override
    public Page<Cliente> obtenerTodos(Pageable pageable) {
        java.util.List<Cliente> lista = new java.util.ArrayList<>(clientes.values());
        int total = lista.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        java.util.List<Cliente> sublist = (start > end) ? java.util.Collections.emptyList() : lista.subList(start, end);
        return new PageImpl<>(sublist, pageable, total);
    }
}
