package proyecto.tfc.services;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.exceptions.BadRequestException;
import proyecto.tfc.exceptions.ResourceNotFoundException;
import proyecto.tfc.repositories.ICafeRepository;
import proyecto.tfc.utils.ValidadorCafe;

/**
 * Servicio de negocio para la gestión de cafés.
 * Valida los datos y coordina la lógica entre el controlador y el repositorio.
 * Incluye validaciones manuales y logging profesional.
 *
 * <p>Esta clase es independiente de la tecnología de persistencia (memoria o JPA).</p>
 * 
 * Requiere que el repositorio haya sido anotado con @Repository.
 * 
 * @author Lola Fernández Fuentes
 * @version 1.2
 * @since 2025-05-23
 */
@Service
public class CafeService {

    private static final Logger log = LoggerFactory.getLogger(CafeService.class);

    private ICafeRepository cafeRepository;

    @Autowired
    public void setCafeRepository(@Lazy ICafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    /**
     * Devuelve todos los cafés registrados en el sistema.
     *
     * @return colección inmodificable de objetos Cafe
     */
    public Collection<Cafe> listarCafes() {
        log.info("Listando todos los cafés");
        return cafeRepository.obtenerTodos().values();
    }

    /**
     * Busca un café existente por su identificador.
     *
     * @param id identificador único del café
     * @return el café si existe
     * @throws ResourceNotFoundException si no existe
     */
    public Cafe obtenerCafePorId(Long id) {
        log.info("Buscando café con id: {}", id);
        return cafeRepository.obtenerPorId(id)
                .orElseThrow(() -> {
                    log.warn("Café no encontrado con id: {}", id);
                    return new ResourceNotFoundException("Café no encontrado con id: " + id);
                });
    }

    /**
     * Crea un nuevo café tras validar todos sus atributos.
     *
     * @param cafe objeto Cafe sin ID
     * @return el café creado
     * @throws BadRequestException si los datos no son válidos
     */
    public Cafe crearCafe(Cafe cafe) {
        log.info("Intentando crear café: {}", cafe);
        if (!ValidadorCafe.esValido(cafe)) {
            throw new BadRequestException("Datos de café inválidos. Verifica nombre, precio, intensidad (1-10), stock y descripción.");
        }
        Cafe creado = Optional.ofNullable(cafeRepository.guardar(cafe))
                .orElseThrow(() -> {
                    log.error("No se pudo crear el café: {}", cafe);
                    return new BadRequestException("No se pudo crear el café");
                });
        log.info("Café creado correctamente: {}", creado);
        return creado;
    }

    /**
     * Reemplaza completamente un café existente por uno nuevo.
     * Valida los nuevos datos antes de proceder.
     *
     * @param id identificador del café a actualizar
     * @param nuevoCafe objeto con los nuevos datos (sin ID)
     * @return el café actualizado
     * @throws ResourceNotFoundException si no existe
     * @throws BadRequestException si los datos son inválidos
     */
    public Cafe actualizarCafe(Long id, Cafe nuevoCafe) {
        log.info("Intentando actualizar café con id: {}", id);
        if (cafeRepository.obtenerPorId(id).isEmpty()) {
            log.warn("Intento de actualizar café no existente con id: {}", id);
            throw new ResourceNotFoundException("Café no encontrado con id: " + id);
        }
        if (!ValidadorCafe.esValido(nuevoCafe)) {
            log.warn("Intento de actualizar café inválido con id {}: {}", id, nuevoCafe);
            throw new BadRequestException("Datos de café inválidos");
        }
        Cafe actualizado = cafeRepository.actualizar(id, nuevoCafe)
                .orElseThrow(() -> {
                    log.error("No se pudo actualizar el café con id {}: {}", id, nuevoCafe);
                    return new BadRequestException("No se pudo actualizar el café");
                });
        log.info("Café actualizado correctamente con id {}: {}", id, actualizado);
        return actualizado;
    }

    /**
     * Modifica parcialmente un café ya existente, si los campos son válidos.
     *
     * @param id identificador del café a modificar
     * @param datosParciales objeto con los nuevos valores de campos
     * @return el café actualizado
     * @throws ResourceNotFoundException si no existe
     * @throws BadRequestException si no hay campos válidos
     */
    public Cafe modificarParcialCafe(Long id, Cafe datosParciales) {
        log.info("Intentando modificar parcialmente café con id: {}", id);
        if (cafeRepository.obtenerPorId(id).isEmpty()) {
            log.warn("Intento de modificar parcialmente café no existente con id: {}", id);
            throw new ResourceNotFoundException("Café no encontrado con id: " + id);
        }
        if (!ValidadorCafe.tieneAlMenosUnCampoValido(datosParciales)) {
            log.warn("Modificación parcial rechazada: sin datos válidos. ID: {}", id);
            throw new BadRequestException("Datos parciales inválidos");
        }
        Cafe modificado = cafeRepository.modificarParcial(id, datosParciales)
                .orElseThrow(() -> {
                    log.error("No se pudo modificar parcialmente el café con id {}: {}", id, datosParciales);
                    return new BadRequestException("No se pudo modificar el café");
                });
        log.info("Café modificado parcialmente con id {}: {}", id, modificado);
        return modificado;
    }

    /**
     * Elimina un café por su ID si existe en el sistema.
     *
     * @param id identificador del café a eliminar
     * @return el café eliminado
     * @throws ResourceNotFoundException si no existe
     */
    public Cafe eliminarCafe(Long id) {
        log.info("Intentando eliminar café con id: {}", id);
        Cafe eliminado = cafeRepository.eliminar(id)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminar café no existente con id: {}", id);
                    return new ResourceNotFoundException("Café no encontrado con id: " + id);
                });
        log.info("Café eliminado correctamente con id {}: {}", id, eliminado);
        return eliminado;
    }

    /**
     * Devuelve una página de cafés según el perfil activo (mem o jpa).
     *
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de cafés
     */
    public Page<Cafe> listarCafesPaginado(int page, int size) {
        return cafeRepository.obtenerTodos(PageRequest.of(page - 1, size));
    }

    /**
     * Busca cafés cuyo stock es menor a un umbral dado.
     *
     * @param umbral valor máximo de stock
     * @return lista de cafés con stock bajo
     */
    public java.util.List<Cafe> buscarCafesConStockBajo(int umbral) {
        return cafeRepository.buscarPorStockMenorQue(umbral);
    }

    /**
     * Busca cafés cuya intensidad está en un rango dado (inclusive).
     *
     * @param min intensidad mínima
     * @param max intensidad máxima
     * @return lista de cafés en ese rango de intensidad
     */
    public java.util.List<Cafe> buscarPorIntensidadEnRango(int min, int max) {
        return cafeRepository.buscarPorIntensidadEnRango(min, max);
    }

    /**
     * Busca cafés cuyo nombre o descripción contiene una palabra clave (ignorando mayúsculas/minúsculas).
     *
     * @param palabra palabra clave
     * @return lista de cafés
     */
    public java.util.List<Cafe> buscarPorNombreODescripcion(String palabra) {
        return cafeRepository.buscarPorNombreODescripcion(palabra);
    }

    /**
     * Busca cafés que están agotados (stock = 0).
     *
     * @return lista de cafés sin stock
     */
    public java.util.List<Cafe> buscarCafesAgotados() {
        return cafeRepository.buscarCafesAgotados();
    }

    /**
     * Busca cafés con filtros y paginación real.
     *
     * @param palabra palabra clave (nombre o descripción)
     * @param minIntensidad intensidad mínima
     * @param maxIntensidad intensidad máxima
     * @param stockBajo si true, solo stock bajo
     * @param agotados si true, solo agotados
     * @param orden campo de ordenación
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de cafés filtrados
     */
    public org.springframework.data.domain.Page<Cafe> buscarCafesFiltradoPaginado(String palabra, Integer minIntensidad, Integer maxIntensidad, Boolean stockBajo, Boolean agotados, String orden, int page, int size) {
        org.springframework.data.domain.Pageable pageable;
        if (orden != null) {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(orden));
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        }
        
        java.util.List<Cafe> filtrados = new java.util.ArrayList<>(cafeRepository.obtenerTodos().values());
        if (palabra != null && !palabra.isEmpty()) {
            filtrados = filtrados.stream().filter(c -> c.getNombre().toLowerCase().contains(palabra.toLowerCase()) || (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(palabra.toLowerCase()))).toList();
        }
        if (minIntensidad != null && maxIntensidad != null) {
            filtrados = filtrados.stream().filter(c -> c.getIntensidad() != null && c.getIntensidad() >= minIntensidad && c.getIntensidad() <= maxIntensidad).toList();
        }
        if (Boolean.TRUE.equals(stockBajo)) {
            filtrados = filtrados.stream().filter(c -> c.getStock() != null && c.getStock() < 5).toList();
        }
        if (Boolean.TRUE.equals(agotados)) {
            filtrados = filtrados.stream().filter(c -> c.getStock() != null && c.getStock() == 0).toList();
        }
        // Ordenación manual si no hay JPA
        if (orden != null) {
            switch (orden) {
                case "precio":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Cafe::getPrecio)).toList();
                    break;
                case "nombre":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Cafe::getNombre)).toList();
                    break;
                case "intensidad":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Cafe::getIntensidad)).toList();
                    break;
            }
        }
        int start = Math.min((page - 1) * size, filtrados.size());
        int end = Math.min(start + size, filtrados.size());
        java.util.List<Cafe> pageContent = filtrados.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtrados.size());
    }
}
