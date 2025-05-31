package proyecto.tfc.repositories.jpa;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import proyecto.tfc.entity.Cliente;
import proyecto.tfc.repositories.IClienteRepository;

/**
 * Implementación JPA del repositorio de clientes.
 * Permite la gestión completa del recurso Cliente en base de datos relacional
 * usando Spring Data JPA. Implementa la interfaz {@link IClienteRepository}
 * para desacoplar la lógica de negocio de la fuente de persistencia.
 *
 * Incluye operaciones CRUD alineadas con la lógica de la implementación en memoria (usando Map).
 *
 * Esta clase se activa solo cuando el perfil 'jpa' está activo en Spring.
 *
 * @author Lola Fernández
 * @version 1.1
 * @since 2025-05-30
 */
@Repository
@Profile("jpa")
public class ClienteJpaRepositoryImpl implements IClienteRepository {

    private ClienteJpaRepository jpaRepo;

    @Autowired
    public void setJpaRepo(@Lazy ClienteJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /** {@inheritDoc} */
    @Override
    public Map<Long, Cliente> obtenerTodos() {
        return jpaRepo.findAll().stream().collect(java.util.stream.Collectors.toMap(proyecto.tfc.entity.Cliente::getId, java.util.function.Function.identity()));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> obtenerPorId(Long id) {
        return jpaRepo.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existePorId(Long id) {
        return jpaRepo.existsById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente guardar(Cliente cliente) {
        return jpaRepo.save(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> actualizar(Long id, Cliente cliente) {
        if (!jpaRepo.existsById(id)) {
            return Optional.empty();
        }
        cliente.setId(id);
        return Optional.of(jpaRepo.save(cliente));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Cliente> eliminar(Long id) {
        Optional<Cliente> eliminado = jpaRepo.findById(id);
        eliminado.ifPresent(jpaRepo::delete);
        return eliminado;
    }

    /** {@inheritDoc} */
    @Override
    public Page<Cliente> obtenerTodos(Pageable pageable) {
        return jpaRepo.findAll(pageable);
    }
} 