package proyecto.tfc.repositories;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import proyecto.tfc.entity.Pedido;

/**
 * Interfaz de acceso a datos para la gestión de entidades {@link Pedido}.
 * Permite desacoplar la lógica de negocio de la fuente de persistencia concreta (memoria o JPA).
 * Define las operaciones CRUD básicas y puede ser implementada tanto por repositorios en memoria
 * como por repositorios basados en base de datos (Spring Data JPA).
 *
 * <p>Esta interfaz es el punto de entrada para todas las operaciones de acceso a datos
 * relacionadas con pedidos en el sistema. Su uso permite alternar entre diferentes
 * mecanismos de persistencia sin modificar la lógica de negocio.</p>
 *
 * @author Lola Fernández
 * @version 1.2
 * @since 2025-05-30
 */
public interface IPedidoRepository {
    /**
     * Recupera todos los pedidos almacenados en el sistema como un Map con el ID como clave.
     *
     * @return mapa inmodificable de pedidos
     */
    Map<Long, Pedido> obtenerTodos();

    /**
     * Busca un pedido por su identificador único.
     *
     * @param id identificador del pedido
     * @return Optional con el pedido si existe, vacío si no se encuentra
     */
    Optional<Pedido> obtenerPorId(Long id);

    /**
     * Guarda un nuevo pedido en el sistema.
     *
     * @param pedido objeto {@link Pedido} a guardar
     * @return el pedido guardado, con ID asignado
     */
    Pedido guardar(Pedido pedido);

    /**
     * Actualiza completamente un pedido existente.
     *
     * @param id identificador del pedido a actualizar
     * @param pedido datos nuevos del pedido (sin ID)
     * @return Optional con el pedido actualizado, o vacío si no existe
     */
    Optional<Pedido> actualizar(Long id, Pedido pedido);

    /**
     * Elimina un pedido por su identificador único.
     *
     * @param id identificador del pedido a eliminar
     * @return Optional con el pedido eliminado, o vacío si no existía
     */
    Optional<Pedido> eliminar(Long id);

    /**
     * Verifica si existe un pedido con el identificador proporcionado.
     *
     * @param id identificador del pedido
     * @return true si el pedido existe, false en caso contrario
     */
    boolean existePorId(Long id);

    // Método para paginación
    Page<Pedido> obtenerTodos(Pageable pageable);

    /**
     * Devuelve los pedidos de un cliente concreto, paginados y ordenados por fecha de creación ascendente.
     * @param clienteId identificador del cliente
     * @param pageable objeto de paginación
     * @return página de pedidos
     */
    Page<Pedido> buscarPorCliente(Long clienteId, Pageable pageable);
} 