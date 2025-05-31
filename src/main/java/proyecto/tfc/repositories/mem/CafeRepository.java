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

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.repositories.ICafeRepository;

/**
 * Este repositorio solo se activa cuando el perfil de Spring activo es "mem".
 * Permite alternar entre persistencia en memoria y JPA según el entorno de ejecución.
 * La anotación @Profile("mem") asegura que este bean no se cree si el perfil activo es otro.
 *
 * Repositorio en memoria para gestionar entidades de tipo {@link Cafe}.
 * Implementa la interfaz {@link ICafeRepository} para permitir el desacoplamiento
 * de la lógica de negocio respecto a la fuente de persistencia.
 *
 * Utiliza un {@code Map} como almacén temporal simulado sin persistencia real.
 * El ID se asigna automáticamente. El acceso externo a los datos está protegido
 * devolviendo estructuras inmodificables.
 *
 * Se incluye trazabilidad mediante Logger para facilitar el diagnóstico.
 *
 * @author Lola Fernández
 * @version 1.4
 * @since 2025-05-30
 */
@Profile("mem")
@Repository
public class CafeRepository implements ICafeRepository {

    private static final Logger log = LoggerFactory.getLogger(CafeRepository.class);

    /** Mapa que almacena los cafés usando el ID como clave */
    private final Map<Long, Cafe> cafes = new HashMap<>();

    /** Secuencia para generar IDs únicos en memoria */
    private Long secuenciaId = 1L;

    /**
     * {@inheritDoc}
     * Devuelve un mapa inmodificable de todos los cafés almacenados.
     */
    @Override
    public Map<Long, Cafe> obtenerTodos() {
        log.info("Recuperando todos los cafés ({} encontrados)", cafes.size());
        return Collections.unmodifiableMap(cafes);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> obtenerPorId(Long id) {
        Cafe cafe = cafes.get(id);
        if (cafe != null) {
            log.info("Café encontrado con ID {}: {}", id, cafe);
        } else {
            log.warn("No se encontró café con ID {}", id);
        }
        return Optional.ofNullable(cafe);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return cafes.containsKey(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cafe guardar(Cafe cafe) {
        if (cafe.getId() == null) {
            cafe.setId(generarNuevoId());
        }
        cafes.put(cafe.getId(), cafe);
        log.info("Café guardado: {}", cafe);
        return cafe;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> actualizar(Long id, Cafe cafe) {
        if (!cafes.containsKey(id) || cafe == null) {
            log.warn("No se pudo actualizar: café inexistente o entrada nula (ID {}).", id);
            return Optional.empty();
        }
        cafe.setId(id);
        cafes.put(id, cafe);
        log.info("Café actualizado con ID {}: {}", id, cafe);
        return Optional.of(cafe);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> modificarParcial(Long id, Cafe parcialCafe) {
        Cafe existente = cafes.get(id);
        if (existente == null || parcialCafe == null) {
            log.warn("No se encontró café con ID {} para modificación parcial.", id);
            return Optional.empty();
        }
        if (parcialCafe.getNombre() != null && !parcialCafe.getNombre().isBlank()) {
            existente.setNombre(parcialCafe.getNombre());
        }
        if (parcialCafe.getDescripcion() != null) {
            existente.setDescripcion(parcialCafe.getDescripcion());
        }
        if (parcialCafe.getPrecio() != null && parcialCafe.getPrecio() > 0) {
            existente.setPrecio(parcialCafe.getPrecio());
        }
        if (parcialCafe.getOrigen() != null) {
            existente.setOrigen(parcialCafe.getOrigen());
        }
        if (parcialCafe.getIntensidad() > 0) {
            existente.setIntensidad(parcialCafe.getIntensidad());
        }
        if (parcialCafe.getStock() >= 0) {
            existente.setStock(parcialCafe.getStock());
        }
        log.info("Café modificado parcialmente con ID {}: {}", id, existente);
        return Optional.of(existente);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> eliminar(Long id) {
        Cafe eliminado = cafes.remove(id);
        if (eliminado == null) {
            log.warn("Intento de eliminar café inexistente con ID {}.", id);
            return Optional.empty();
        }
        log.info("Café eliminado con ID {}: {}", id, eliminado);
        return Optional.of(eliminado);
    }

    /**
     * Genera un nuevo ID único para el siguiente café.
     *
     * @return ID autogenerado
     */
    private Long generarNuevoId() {
        return secuenciaId++;
    }

    @Override
    public Page<Cafe> obtenerTodos(Pageable pageable) {
        java.util.List<Cafe> lista = new java.util.ArrayList<>(cafes.values());
        int total = lista.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        java.util.List<Cafe> sublist = (start > end) ? java.util.Collections.emptyList() : lista.subList(start, end);
        return new PageImpl<>(sublist, pageable, total);
    }

    @Override
    public java.util.List<Cafe> buscarPorStockMenorQue(int umbral) {
        return cafes.values().stream()
                .filter(c -> c.getStock() < umbral)
                .toList();
    }

    @Override
    public java.util.List<Cafe> buscarPorIntensidadEnRango(int min, int max) {
        return cafes.values().stream()
                .filter(c -> c.getIntensidad() >= min && c.getIntensidad() <= max)
                .toList();
    }

    @Override
    public java.util.List<Cafe> buscarPorNombreODescripcion(String palabra) {
        String p = palabra == null ? "" : palabra.toLowerCase();
        return cafes.values().stream()
                .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(p)) ||
                             (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(p)))
                .toList();
    }

    @Override
    public java.util.List<Cafe> buscarCafesAgotados() {
        return cafes.values().stream()
                .filter(c -> c.getStock() == 0)
                .toList();
    }
}
