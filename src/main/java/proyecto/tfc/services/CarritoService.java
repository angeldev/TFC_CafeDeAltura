package proyecto.tfc.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.entity.LineaCarrito;
import proyecto.tfc.entity.LineaDePedido;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.exceptions.BadRequestException;
import proyecto.tfc.exceptions.ResourceNotFoundException;
import proyecto.tfc.repositories.ICafeRepository;
import proyecto.tfc.repositories.ICarritoRepository;
import proyecto.tfc.repositories.IClienteRepository;

/**
 * Servicio de negocio para la gestión del carrito de compra de los clientes.
 * Permite añadir, modificar y eliminar cafés en el carrito, así como confirmar la compra
 * y generar un pedido definitivo. Valida el stock disponible antes de cada operación.
 *
 * El carrito se almacena en memoria y está asociado a un cliente concreto.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-26
 */
@Service
public class CarritoService {
    /**
     * Repositorio en memoria para la gestión de carritos de clientes.
     */
    private ICarritoRepository carritoRepository;
    /**
     * Repositorio en memoria para la gestión de clientes.
     */
    private IClienteRepository clienteRepository;
    /**
     * Repositorio en memoria para la gestión de cafés y su stock.
     */
    private ICafeRepository cafeRepository;
    /**
     * Servicio de negocio para la gestión de pedidos.
     */
    private PedidoService pedidoService;

    @Autowired
    public void setCarritoRepository(@Lazy ICarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }
    @Autowired
    public void setClienteRepository(@Lazy IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    @Autowired
    public void setCafeRepository(@Lazy ICafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }
    @Autowired
    public void setPedidoService(@Lazy PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Devuelve el carrito de un cliente. Si no existe, lo crea vacío.
     * @param clienteId identificador del cliente
     * @return carrito del cliente
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Carrito obtenerOCrearCarrito(Long clienteId) {
        Cliente cliente = clienteRepository.obtenerPorId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));
        Optional<Carrito> carritoOpt = carritoRepository.obtenerPorCliente(cliente);
        if (carritoOpt.isPresent()) {
            return carritoOpt.get();
        } else {
            Carrito nuevo = new Carrito(cliente);
                    carritoRepository.guardar(nuevo);
                    return nuevo;
        }
    }

    /**
     * Añade un café y cantidad al carrito del cliente. Si ya existe, suma la cantidad.
     * Valida el stock disponible.
     * @param clienteId identificador del cliente
     * @param idCafe identificador del café
     * @param cantidad cantidad a añadir
     * @return carrito actualizado
     * @throws BadRequestException si la cantidad es inválida, el café no existe o no hay suficiente stock
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Carrito anadirCafe(Long clienteId, Long idCafe, int cantidad) {
        validarCantidadPositiva(cantidad);
        Cafe cafe = obtenerCafeExistente(idCafe);
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        // Buscar si ya existe la línea
        LineaCarrito existente = carrito.getLineas().stream()
                .filter(l -> l.getCafe().getId().equals(idCafe)).findFirst().orElse(null);
        int cantidadTotal = cantidad + (existente != null ? existente.getCantidad() : 0);
        validarStockDisponible(cafe, cantidadTotal);
        if (existente != null) {
            existente.setCantidad(cantidadTotal);
        } else {
            LineaCarrito nueva = new LineaCarrito(carrito, cafe, cantidad);
            carrito.getLineas().add(nueva);
        }
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.guardar(carrito);
        return carrito;
    }

    /**
     * Elimina un café del carrito del cliente.
     * @param clienteId identificador del cliente
     * @param idCafe identificador del café a eliminar
     * @return carrito actualizado
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Carrito eliminarCafe(Long clienteId, Long idCafe) {
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        carrito.getLineas().removeIf(l -> l.getCafe().getId().equals(idCafe));
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.guardar(carrito);
        return carrito;
    }

    /**
     * Modifica la cantidad de un café en el carrito del cliente.
     * Valida el stock disponible.
     * @param clienteId identificador del cliente
     * @param idCafe identificador del café
     * @param nuevaCantidad nueva cantidad a establecer
     * @return carrito actualizado
     * @throws BadRequestException si la cantidad es inválida, el café no existe, no hay suficiente stock o el café no está en el carrito
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Carrito modificarCantidad(Long clienteId, Long idCafe, int nuevaCantidad) {
        validarCantidadPositiva(nuevaCantidad);
        Cafe cafe = obtenerCafeExistente(idCafe);
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        LineaCarrito linea = carrito.getLineas().stream()
                .filter(l -> l.getCafe().getId().equals(idCafe)).findFirst()
                .orElseThrow(() -> new BadRequestException("El café no está en el carrito"));
        validarStockDisponible(cafe, nuevaCantidad);
        linea.setCantidad(nuevaCantidad);
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.guardar(carrito);
        return carrito;
    }

    /**
     * Vacía el carrito del cliente.
     * @param clienteId identificador del cliente
     * @return carrito vacío
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Carrito vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        carrito.getLineas().clear();
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.guardar(carrito);
        return carrito;
    }

    /**
     * Confirma el carrito del cliente, crea un pedido y descuenta el stock.
     * Vacía el carrito tras la compra.
     * @param clienteId identificador del cliente
     * @return pedido creado
     * @throws BadRequestException si el carrito está vacío, algún café no existe o no hay suficiente stock
     * @throws ResourceNotFoundException si el cliente no existe
     */
    public Pedido confirmarCarrito(Long clienteId) {
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        if (carrito.getLineas().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
        // Validar stock de todos los cafés antes de crear el pedido
        for (LineaCarrito linea : carrito.getLineas()) {
            Cafe cafe = obtenerCafeExistente(linea.getCafe().getId());
            validarStockDisponible(cafe, linea.getCantidad());
        }
        // Descontar stock
        for (LineaCarrito linea : carrito.getLineas()) {
            Cafe cafe = obtenerCafeExistente(linea.getCafe().getId());
            descontarStock(cafe, linea.getCantidad());
        }
        Cliente cliente = carrito.getCliente();
        // Convertir las líneas del carrito a líneas de pedido
        java.util.List<LineaDePedido> lineasPedido = new java.util.ArrayList<>();
        for (LineaCarrito linea : carrito.getLineas()) {
            lineasPedido.add(new LineaDePedido(linea.getCafe(), linea.getCafe().getPrecio(), linea.getCantidad()));
        }
        Pedido pedido = new Pedido(cliente, lineasPedido, "PENDIENTE", "");
        Pedido creado = pedidoService.crearPedido(pedido);
        carrito.setFechaActualizacion(LocalDateTime.now());
        vaciarCarrito(clienteId);
        return creado;
    }

    // Métodos privados de validación y lógica común

    /** Valida que la cantidad sea mayor que cero. */
    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) throw new BadRequestException("La cantidad debe ser mayor que cero");
    }

    /** Obtiene un café existente o lanza excepción. */
    private Cafe obtenerCafeExistente(Long idCafe) {
        return cafeRepository.obtenerPorId(idCafe)
                .orElseThrow(() -> new BadRequestException("Café no encontrado con id: " + idCafe));
    }

    /** Valida que haya suficiente stock para la cantidad solicitada. */
    private void validarStockDisponible(Cafe cafe, int cantidadSolicitada) {
        if (cantidadSolicitada > cafe.getStock()) {
            throw new BadRequestException("No hay suficiente stock para el café seleccionado");
        }
    }

    /** Descuenta stock del café y actualiza el repositorio. */
    private void descontarStock(Cafe cafe, int cantidad) {
        cafe.setStock(cafe.getStock() - cantidad);
        cafeRepository.actualizar(cafe.getId(), cafe);
    }
} 