package proyecto.tfc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import proyecto.tfc.dto.ClienteDTO;
import proyecto.tfc.dto.RespuestaPaginada;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.services.ClienteService;

/**
 * Controlador REST para la gestión de clientes.
 * Proporciona endpoints para operaciones CRUD y búsquedas avanzadas sobre clientes.
 * Las validaciones y lógica de negocio se delegan al servicio correspondiente.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private ClienteService clienteService;

    @Autowired
    public void setClienteService(@Lazy ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Devuelve la lista de todos los clientes registrados.
     *
     * @return 200 OK con la colección de clientes
     */
    @GetMapping
    public ResponseEntity<RespuestaPaginada<Cliente>> listarTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1 || size < 1) {
            throw new proyecto.tfc.exceptions.BadRequestException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }
        log.info("[GET] /clientes - Listando clientes paginados (page={}, size={})", page, size);
        Page<Cliente> pageClientes = clienteService.listarClientesPaginado(page, size);
        RespuestaPaginada.MetadatosPaginacion meta = new RespuestaPaginada.MetadatosPaginacion(
                pageClientes.getNumber() + 1,
                pageClientes.getSize(),
                pageClientes.getTotalPages(),
                pageClientes.getTotalElements(),
                pageClientes.isLast(),
                pageClientes.isFirst()
        );
        RespuestaPaginada<Cliente> respuesta = new RespuestaPaginada<>(pageClientes.getContent(), meta);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Devuelve un cliente específico por su ID.
     *
     * @param id identificador del cliente
     * @return 200 OK con el cliente o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        log.info("[GET] /clientes/{} - Buscando cliente", id);
        Cliente cliente = clienteService.obtenerClientePorId(id);
        log.info("[GET] /clientes/{} - Cliente encontrado: {}", id, cliente);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Crea un nuevo cliente a partir del JSON recibido.
     *
     * @param clienteDTO objeto recibido en el cuerpo de la petición
     * @return 201 Created si es válido, 400 Bad Request si no lo es
     */
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente(
            clienteDTO.getNombre(),
            clienteDTO.getEmail(),
            clienteDTO.getTelefono(),
            clienteDTO.getDireccion()
        );
        Cliente creado = clienteService.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Reemplaza por completo un cliente existente.
     *
     * @param id identificador del cliente a reemplazar
     * @param clienteDTO datos nuevos del cliente
     * @return 200 OK si se actualiza, 400 si los datos son inválidos, 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente nuevoCliente = new Cliente(
            clienteDTO.getNombre(),
            clienteDTO.getEmail(),
            clienteDTO.getTelefono(),
            clienteDTO.getDireccion()
        );
        Cliente actualizado = clienteService.actualizarCliente(id, nuevoCliente);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id ID del cliente a eliminar
     * @return 200 OK si se elimina, 404 Not Found si no existe
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("[DELETE] /clientes/{} - Eliminando cliente", id);
        clienteService.eliminarCliente(id);
        log.info("[DELETE] /clientes/{} - Cliente eliminado", id);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /clientes/buscarPorNombre?fragmento=...
     * Busca clientes cuyo nombre contiene un fragmento (ignorando mayúsculas/minúsculas).
     * Ejemplo: /clientes/buscarPorNombre?fragmento=ana
     */
    @GetMapping("/buscarPorNombre")
    public ResponseEntity<java.util.List<Cliente>> buscarPorNombre(@RequestParam String fragmento) {
        return ResponseEntity.ok(clienteService.buscarPorNombreConteniendo(fragmento));
    }

    /**
     * GET /clientes/registradosDespuesDe?fecha=2024-06-01T00:00:00
     * Busca clientes registrados después de una fecha dada (formato ISO-8601).
     * Ejemplo: /clientes/registradosDespuesDe?fecha=2024-06-01T00:00:00
     */
    @GetMapping("/registradosDespuesDe")
    public ResponseEntity<java.util.List<Cliente>> buscarRegistradosDespuesDe(@RequestParam String fecha) {
        java.time.LocalDateTime f = java.time.LocalDateTime.parse(fecha);
        return ResponseEntity.ok(clienteService.buscarRegistradosDespuesDe(f));
    }

    /**
     * GET /clientes/activosConEmailVerificado
     * Busca clientes activos cuyo email parece verificado (contiene '@').
     * Ejemplo: /clientes/activosConEmailVerificado
     */
    @GetMapping("/activosConEmailVerificado")
    public ResponseEntity<java.util.List<Cliente>> buscarActivosConEmailVerificado() {
        return ResponseEntity.ok(clienteService.buscarActivosConEmailVerificado());
    }

    /**
     * GET /clientes/conPedidos
     * Busca clientes que han realizado al menos un pedido.
     * Ejemplo: /clientes/conPedidos
     */
    @GetMapping("/conPedidos")
    public ResponseEntity<java.util.List<Cliente>> buscarClientesConPedidos() {
        return ResponseEntity.ok(clienteService.buscarClientesConPedidos());
    }
}
