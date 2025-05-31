package proyecto.tfc.repositories;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import proyecto.tfc.entity.Cliente;

/**
 * Interfaz de acceso a datos para la gestión de entidades {@link Cliente}.
 * Permite desacoplar la lógica de negocio de la fuente de persistencia concreta (memoria o JPA).
 * Define las operaciones CRUD básicas y puede ser implementada tanto por repositorios en memoria
 * como por repositorios basados en base de datos (Spring Data JPA).
 *
 * <p>Esta interfaz es el punto de entrada para todas las operaciones de acceso a datos
 * relacionadas con clientes en el sistema. Su uso permite alternar entre diferentes
 * mecanismos de persistencia sin modificar la lógica de negocio.</p>
 *
 * @author Lola Fernández
 * @version 1.2
 * @since 2025-05-30
 */
public interface IClienteRepository {
    /**
     * Recupera todos los clientes almacenados en el sistema como un Map con el ID como clave.
     *
     * @return mapa inmodificable de clientes
     */
    Map<Long, Cliente> obtenerTodos();

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id identificador del cliente
     * @return Optional con el cliente si existe, vacío si no se encuentra
     */
    Optional<Cliente> obtenerPorId(Long id);

    /**
     * Guarda un nuevo cliente en el sistema.
     *
     * @param cliente objeto {@link Cliente} a guardar
     * @return el cliente guardado, con ID asignado
     */
    Cliente guardar(Cliente cliente);

    /**
     * Actualiza completamente un cliente existente.
     *
     * @param id identificador del cliente a actualizar
     * @param cliente datos nuevos del cliente (sin ID)
     * @return Optional con el cliente actualizado, o vacío si no existe
     */
    Optional<Cliente> actualizar(Long id, Cliente cliente);

    /**
     * Elimina un cliente por su identificador único.
     *
     * @param id identificador del cliente a eliminar
     * @return Optional con el cliente eliminado, o vacío si no existía
     */
    Optional<Cliente> eliminar(Long id);

    /**
     * Verifica si existe un cliente con el identificador proporcionado.
     *
     * @param id identificador del cliente
     * @return true si el cliente existe, false en caso contrario
     */
    boolean existePorId(Long id);

    // Método para paginación
    Page<Cliente> obtenerTodos(Pageable pageable);
} 