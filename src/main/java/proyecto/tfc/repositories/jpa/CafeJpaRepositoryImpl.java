package proyecto.tfc.repositories.jpa;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.repositories.ICafeRepository;

/**
 * Implementación JPA del repositorio de cafés.
 * Permite la gestión completa del recurso Café en base de datos relacional
 * usando Spring Data JPA. Implementa la interfaz {@link ICafeRepository}
 * para desacoplar la lógica de negocio de la fuente de persistencia.
 *
 * Incluye operaciones CRUD, búsqueda por origen y actualización parcial,
 * alineadas con la lógica de la implementación en memoria (usando Map).
 *
 * Esta clase se activa solo cuando el perfil 'jpa' está activo en Spring.
 *
 * @author Lola Fernández
 * @version 1.3
 * @since 2025-05-30
 */
@Repository
@Profile("jpa")
public class CafeJpaRepositoryImpl implements ICafeRepository {

    private CafeJpaRepository jpaRepo;

    @Autowired
    public void setJpaRepo(@Lazy CafeJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /** {@inheritDoc} */
    @Override
    public Map<Long, Cafe> obtenerTodos() {
        List<Cafe> lista = jpaRepo.findAll();
        return lista.stream().collect(Collectors.toMap(Cafe::getId, Function.identity()));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> obtenerPorId(Long id) {
        return jpaRepo.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return jpaRepo.existsById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cafe guardar(Cafe cafe) {
        return jpaRepo.save(cafe);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> actualizar(Long id, Cafe cafe) {
        if (!jpaRepo.existsById(id)) {
            return Optional.empty();
        }
        cafe.setId(id);
        return Optional.of(jpaRepo.save(cafe));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> modificarParcial(Long id, Cafe parcialCafe) {
        Optional<Cafe> existenteOpt = jpaRepo.findById(id);
        if (existenteOpt.isEmpty()) {
            return Optional.empty();
        }
        Cafe existente = existenteOpt.get();
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
        if (Objects.nonNull(parcialCafe.getIntensidad()) && parcialCafe.getIntensidad() > 0) {
            existente.setIntensidad(parcialCafe.getIntensidad());
        }
        if (Objects.nonNull(parcialCafe.getStock()) && parcialCafe.getStock() >= 0) {
            existente.setStock(parcialCafe.getStock());
        }
        return Optional.of(jpaRepo.save(existente));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cafe> eliminar(Long id) {
        Optional<Cafe> eliminado = jpaRepo.findById(id);
        eliminado.ifPresent(jpaRepo::delete);
        return eliminado;
    }

    /**
     * Busca cafés por origen usando una consulta personalizada.
     * @param origen país o región de origen
     * @return mapa de cafés de ese origen
     */
    public Map<Long, Cafe> buscarPorOrigen(String origen) {
        List<Cafe> lista = jpaRepo.buscarPorOrigen(origen);
        return lista.stream().collect(Collectors.toMap(Cafe::getId, Function.identity()));
    }

    /** {@inheritDoc} */
    @Override
    public Page<Cafe> obtenerTodos(Pageable pageable) {
        return jpaRepo.findAll(pageable);
    }

    @Override
    public java.util.List<Cafe> buscarPorStockMenorQue(int umbral) {
        return jpaRepo.findByStockLessThan(umbral);
    }

    @Override
    public java.util.List<Cafe> buscarPorIntensidadEnRango(int min, int max) {
        return jpaRepo.findByIntensidadBetween(min, max);
    }

    @Override
    public java.util.List<Cafe> buscarPorNombreODescripcion(String palabra) {
        return jpaRepo.buscarPorNombreODescripcion(palabra);
    }

    @Override
    public java.util.List<Cafe> buscarCafesAgotados() {
        return jpaRepo.findByStockEquals(0);
    }
} 