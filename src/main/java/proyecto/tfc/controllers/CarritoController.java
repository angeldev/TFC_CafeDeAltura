package proyecto.tfc.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.tfc.entity.Carrito;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.services.CarritoService;

/**
 * Controlador REST para la gestión del carrito de compra.
 * Proporciona endpoints para consultar, modificar y confirmar el carrito de un cliente.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-27
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private CarritoService carritoService;

    @Autowired
    public void setCarritoService(@Lazy CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /**
     * Devuelve el carrito de un cliente.
     * @param clienteId identificador del cliente
     * @return 200 OK con el carrito
     */
    @GetMapping("/{clienteId}")
    public ResponseEntity<Carrito> verCarrito(@PathVariable Long clienteId) {
        Carrito carrito = carritoService.obtenerOCrearCarrito(clienteId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Añade un café y cantidad al carrito del cliente.
     * @param clienteId identificador del cliente
     * @param body JSON con campos: idCafe, cantidad
     * @return 200 OK con el carrito actualizado
     */
    @PostMapping("/{clienteId}/anadir")
    public ResponseEntity<Carrito> anadirCafe(@PathVariable Long clienteId, @RequestBody Map<String, Object> body) {
        Long idCafe = Long.valueOf(body.get("idCafe").toString());
        int cantidad = Integer.parseInt(body.get("cantidad").toString());
        Carrito carrito = carritoService.anadirCafe(clienteId, idCafe, cantidad);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Elimina un café del carrito del cliente.
     * @param clienteId identificador del cliente
     * @param body JSON con campo: idCafe
     * @return 200 OK con el carrito actualizado
     */
    @PostMapping("/{clienteId}/eliminar")
    public ResponseEntity<Carrito> eliminarCafe(@PathVariable Long clienteId, @RequestBody Map<String, Object> body) {
        Long idCafe = Long.valueOf(body.get("idCafe").toString());
        Carrito carrito = carritoService.eliminarCafe(clienteId, idCafe);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Modifica la cantidad de un café en el carrito del cliente.
     * @param clienteId identificador del cliente
     * @param body JSON con campos: idCafe, cantidad
     * @return 200 OK con el carrito actualizado
     */
    @PostMapping("/{clienteId}/modificar")
    public ResponseEntity<Carrito> modificarCantidad(@PathVariable Long clienteId, @RequestBody Map<String, Object> body) {
        Long idCafe = Long.valueOf(body.get("idCafe").toString());
        int cantidad = Integer.parseInt(body.get("cantidad").toString());
        Carrito carrito = carritoService.modificarCantidad(clienteId, idCafe, cantidad);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Vacía el carrito del cliente.
     * @param clienteId identificador del cliente
     * @return 200 OK con el carrito vacío
     */
    @PostMapping("/{clienteId}/vaciar")
    public ResponseEntity<Carrito> vaciarCarrito(@PathVariable Long clienteId) {
        Carrito carrito = carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Confirma el carrito del cliente, crea un pedido y vacía el carrito.
     * @param clienteId identificador del cliente
     * @return 201 Created con el pedido creado
     */
    @PostMapping("/{clienteId}/confirmar")
    public ResponseEntity<Pedido> confirmarCarrito(@PathVariable Long clienteId) {
        Pedido pedido = carritoService.confirmarCarrito(clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
} 