package proyecto.tfc.repositories;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import proyecto.tfc.entity.Cafe;

/**
 * Interfaz de acceso a datos para la gestión de entidades {@link Cafe}.
 * Permite desacoplar la lógica de negocio de la fuente de persistencia concreta (memoria o JPA).
 * Define las operaciones CRUD básicas y puede ser implementada tanto por repositorios en memoria
 * como por repositorios basados en base de datos (Spring Data JPA).
 *
 * <p>Esta interfaz es el punto de entrada para todas las operaciones de acceso a datos
 * relacionadas con cafés en el sistema. Su uso permite alternar entre diferentes
 * mecanismos de persistencia sin modificar la lógica de negocio.</p>
 *
 * @author Lola Fernández
 * @version 1.2
 * @since 2025-05-30
 */
public interface ICafeRepository {
    /**
     * Recupera todos los cafés almacenados en el sistema como un Map con el ID como clave.
     *
     * @return mapa inmodificable de cafés
     */
    Map<Long, Cafe> obtenerTodos();

    /**
     * Busca un café por su identificador único.
     *
     * @param id identificador del café
     * @return Optional con el café si existe, vacío si no se encuentra
     */
    Optional<Cafe> obtenerPorId(Long id);

    /**
     * Guarda un nuevo café en el sistema.
     *
     * @param cafe objeto {@link Cafe} a guardar
     * @return el café guardado, con ID asignado
     */
    Cafe guardar(Cafe cafe);

    /**
     * Actualiza completamente un café existente.
     *
     * @param id identificador del café a actualizar
     * @param cafe datos nuevos del café (sin ID)
     * @return Optional con el café actualizado, o vacío si no existe
     */
    Optional<Cafe> actualizar(Long id, Cafe cafe);

    /**
     * Modifica parcialmente los datos de un café existente.
     * Solo se actualizan los campos no nulos o no vacíos.
     *
     * @param id identificador del café a modificar
     * @param parcialCafe objeto con los datos a modificar
     * @return Optional con el café modificado, o vacío si no existe
     */
    Optional<Cafe> modificarParcial(Long id, Cafe parcialCafe);

    /**
     * Elimina un café por su identificador único.
     *
     * @param id identificador del café a eliminar
     * @return Optional con el café eliminado, o vacío si no existía
     */
    Optional<Cafe> eliminar(Long id);

    /**
     * Verifica si existe un café con el identificador proporcionado.
     *
     * @param id identificador del café
     * @return true si el café existe, false en caso contrario
     */
    boolean existePorId(Long id);

    // Método para paginación
    Page<Cafe> obtenerTodos(Pageable pageable);

    // Métodos avanzados de consulta
    /**
     * Busca cafés cuyo stock es menor a un umbral dado.
     * @param umbral valor máximo de stock
     * @return lista de cafés con stock bajo
     */
    java.util.List<Cafe> buscarPorStockMenorQue(int umbral);

    /**
     * Busca cafés cuya intensidad está en un rango dado (inclusive).
     * @param min intensidad mínima
     * @param max intensidad máxima
     * @return lista de cafés en ese rango de intensidad
     */
    java.util.List<Cafe> buscarPorIntensidadEnRango(int min, int max);

    /**
     * Busca cafés cuyo nombre o descripción contiene una palabra clave (ignorando mayúsculas/minúsculas).
     * @param palabra palabra clave
     * @return lista de cafés
     */
    java.util.List<Cafe> buscarPorNombreODescripcion(String palabra);

    /**
     * Busca cafés que están agotados (stock = 0).
     * @return lista de cafés sin stock
     */
    java.util.List<Cafe> buscarCafesAgotados();

} 