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

import proyecto.tfc.entity.Cliente;
import proyecto.tfc.exceptions.BadRequestException;
import proyecto.tfc.exceptions.ResourceNotFoundException;
import proyecto.tfc.repositories.IClienteRepository;
import proyecto.tfc.utils.ValidadorCliente;

/**
 * Servicio de negocio para la gestión de clientes.
 * Esta capa se encarga de validar los datos y coordinar
 * la lógica entre el controlador y el repositorio en memoria.
 *
 * Realiza validaciones manuales y emite trazas mediante logging.
 *
 * Requiere que el repositorio haya sido anotado con @Repository.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private IClienteRepository clienteRepository;

    @Autowired
    public void setClienteRepository(@Lazy IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Devuelve todos los clientes registrados en el sistema.
     *
     * @return colección inmodificable de objetos Cliente
     */
    public Collection<Cliente> listarClientes() {
        log.info("Listando todos los clientes");
        return clienteRepository.obtenerTodos().values();
    }

    /**
     * Busca un cliente existente por su identificador.
     *
     * @param id identificador único del cliente
     * @return el cliente si existe
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Cliente obtenerClientePorId(Long id) {
        log.info("Buscando cliente con id: {}", id);
        return clienteRepository.obtenerPorId(id)
                .orElseThrow(() -> {
                    log.warn("Cliente no encontrado con id: {}", id);
                    return new ResourceNotFoundException("Cliente no encontrado con id: " + id);
                });
    }

    /**
     * Crea un nuevo cliente tras validar todos sus atributos.
     *
     * @param cliente objeto Cliente sin ID
     * @return el cliente creado
     * @throws BadRequestException si los datos no son válidos
     */
    public Cliente crearCliente(Cliente cliente) {
        log.info("Intentando crear cliente: {}", cliente);
        if (!ValidadorCliente.esValido(cliente)) {
            throw new BadRequestException("Datos de cliente inválidos. Verifica nombre y email.");
        }
        // Comprobación de email único
        if (clienteRepository.obtenerTodos().values().stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(cliente.getEmail()))) {
            throw new BadRequestException("El email ya está registrado.");
        }
        Cliente creado = Optional.ofNullable(clienteRepository.guardar(cliente))
                .orElseThrow(() -> {
                    log.error("No se pudo crear el cliente: {}", cliente);
                    return new BadRequestException("No se pudo crear el cliente");
                });
        log.info("Cliente creado correctamente: {}", creado);
        return creado;
    }

    /**
     * Reemplaza completamente un cliente existente por uno nuevo.
     * Valida los nuevos datos antes de proceder.
     *
     * @param id identificador del cliente a actualizar
     * @param nuevoCliente objeto con los nuevos datos (sin ID)
     * @return el cliente actualizado
     * @throws ResourceNotFoundException si el cliente no existe
     * @throws BadRequestException si los datos son inválidos
     */
    public Cliente actualizarCliente(Long id, Cliente nuevoCliente) {
        log.info("Intentando actualizar cliente con id: {}", id);
        if (clienteRepository.obtenerPorId(id).isEmpty()) {
            log.warn("Intento de actualizar cliente no existente con id: {}", id);
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        if (!ValidadorCliente.esValido(nuevoCliente)) {
            log.warn("Intento de actualizar cliente inválido con id {}: {}", id, nuevoCliente);
            throw new BadRequestException("Datos de cliente inválidos");
        }
        Cliente actualizado = clienteRepository.actualizar(id, nuevoCliente)
                .orElseThrow(() -> {
                    log.error("No se pudo actualizar el cliente con id {}: {}", id, nuevoCliente);
                    return new BadRequestException("No se pudo actualizar el cliente");
                });
        log.info("Cliente actualizado correctamente con id {}: {}", id, actualizado);
        return actualizado;
    }

    /**
     * Elimina un cliente por su ID si existe en el sistema.
     *
     * @param id identificador del cliente a eliminar
     * @return el cliente eliminado
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Cliente eliminarCliente(Long id) {
        log.info("Intentando eliminar cliente con id: {}", id);
        Cliente eliminado = clienteRepository.eliminar(id)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminar cliente no existente con id: {}", id);
                    return new ResourceNotFoundException("Cliente no encontrado con id: " + id);
                });
        log.info("Cliente eliminado correctamente con id {}: {}", id, eliminado);
        return eliminado;
    }

    public Page<Cliente> listarClientesPaginado(int page, int size) {
        return clienteRepository.obtenerTodos(PageRequest.of(page - 1, size));
    }

    /**
     * Busca clientes cuyo nombre contiene un fragmento (ignorando mayúsculas/minúsculas).
     * @param fragmento parte del nombre a buscar
     * @return lista de clientes que coinciden
     * @see proyecto.tfc.repositories.jpa.ClienteJpaRepository#buscarPorNombreConteniendo(String)
     * @example clienteService.buscarPorNombreConteniendo("ana");
     */
    public java.util.List<Cliente> buscarPorNombreConteniendo(String fragmento) {
        return clienteRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.ClienteJpaRepository) clienteRepository).buscarPorNombreConteniendo(fragmento)
            : java.util.Collections.emptyList();
    }

    /**
     * Busca clientes registrados después de una fecha dada.
     * @param fecha fecha límite inferior
     * @return lista de clientes
     */
    public java.util.List<Cliente> buscarRegistradosDespuesDe(java.time.LocalDateTime fecha) {
        return clienteRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.ClienteJpaRepository) clienteRepository).findByFechaRegistroAfter(fecha)
            : java.util.Collections.emptyList();
    }

    /**
     * Busca clientes activos cuyo email parece verificado (contiene '@').
     * @return lista de clientes activos y con email válido
     */
    public java.util.List<Cliente> buscarActivosConEmailVerificado() {
        return clienteRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.ClienteJpaRepository) clienteRepository).buscarActivosConEmailVerificado()
            : java.util.Collections.emptyList();
    }

    /**
     * Busca clientes que han realizado al menos un pedido.
     * @return lista de clientes con pedidos
     */
    public java.util.List<Cliente> buscarClientesConPedidos() {
        return clienteRepository instanceof org.springframework.data.jpa.repository.JpaRepository
            ? ((proyecto.tfc.repositories.jpa.ClienteJpaRepository) clienteRepository).buscarClientesConPedidos()
            : java.util.Collections.emptyList();
    }

    /**
     * Busca clientes con filtros y paginación real.
     * @param nombre nombre a buscar
     * @param fecha fecha de registro después de
     * @param emailVerificado solo email verificado
     * @param conPedidos solo con pedidos
     * @param orden campo de ordenación
     * @param page número de página (empezando en 1)
     * @param size tamaño de página
     * @return página de clientes filtrados
     */
    public org.springframework.data.domain.Page<Cliente> buscarClientesFiltradoPaginado(String nombre, String fecha, Boolean emailVerificado, Boolean conPedidos, String orden, int page, int size) {
        org.springframework.data.domain.Pageable pageable;
        if (orden != null) {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(orden));
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        }
        java.util.List<Cliente> filtrados = new java.util.ArrayList<>(clienteRepository.obtenerTodos().values());
        if (nombre != null && !nombre.isEmpty()) {
            filtrados = filtrados.stream().filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase())).toList();
        }
        if (fecha != null && !fecha.isEmpty()) {
            java.time.LocalDateTime f = java.time.LocalDateTime.parse(fecha);
            filtrados = filtrados.stream().filter(c -> c.getFechaRegistro() != null && c.getFechaRegistro().isAfter(f)).toList();
        }
        if (Boolean.TRUE.equals(emailVerificado)) {
            filtrados = filtrados.stream().filter(c -> c.getEmail() != null && c.getEmail().contains("@")) .toList();
        }
        if (Boolean.TRUE.equals(conPedidos)) {
            filtrados = filtrados.stream().filter(c -> c.getPedidos() != null && !c.getPedidos().isEmpty()).toList();
        }
        if (orden != null) {
            switch (orden) {
                case "nombre":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Cliente::getNombre)).toList();
                    break;
                case "fecha":
                    filtrados = filtrados.stream().sorted(java.util.Comparator.comparing(Cliente::getFechaRegistro)).toList();
                    break;
            }
        }
        int start = Math.min((page - 1) * size, filtrados.size());
        int end = Math.min(start + size, filtrados.size());
        java.util.List<Cliente> pageContent = filtrados.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtrados.size());
    }
}
