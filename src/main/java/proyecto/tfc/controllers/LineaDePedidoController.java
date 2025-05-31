package proyecto.tfc.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proyecto.tfc.entity.LineaDePedido;
import proyecto.tfc.services.LineaDePedidoService;

/**
 * Controlador REST para la gestión y consulta de líneas de pedido.
 * Proporciona endpoints para búsquedas avanzadas por café, pedido, cantidad y subtotal.
 *
 * Ejemplo de uso:
 *   GET /lineas/cafe/1
 *   GET /lineas/pedido/2
 *   GET /lineas/cantidadMayorA?cantidad=3
 *   GET /lineas/subtotalMayorA?subtotal=20.0
 */
@RestController
@RequestMapping("/api/lineas")
public class LineaDePedidoController {
    private final LineaDePedidoService lineaService;

    public LineaDePedidoController(LineaDePedidoService lineaService) {
        this.lineaService = lineaService;
    }

    /**
     * GET /lineas/cafe/{cafeId}
     * Busca líneas de pedido asociadas a un café concreto.
     */
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<LineaDePedido>> buscarPorCafe(@PathVariable Long cafeId) {
        return ResponseEntity.ok(lineaService.buscarPorCafe(cafeId));
    }

    /**
     * GET /lineas/pedido/{pedidoId}
     * Busca líneas de pedido asociadas a un pedido concreto.
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<LineaDePedido>> buscarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(lineaService.buscarPorPedido(pedidoId));
    }

    /**
     * GET /lineas/cantidadMayorA?cantidad=3
     * Busca líneas de pedido con cantidad mayor a un valor dado.
     */
    @GetMapping("/cantidadMayorA")
    public ResponseEntity<List<LineaDePedido>> buscarPorCantidadMayorA(@RequestParam int cantidad) {
        return ResponseEntity.ok(lineaService.buscarPorCantidadMayorA(cantidad));
    }

    /**
     * GET /lineas/subtotalMayorA?subtotal=20.0
     * Busca líneas de pedido con subtotal mayor a un importe dado.
     */
    @GetMapping("/subtotalMayorA")
    public ResponseEntity<List<LineaDePedido>> buscarPorSubtotalMayorA(@RequestParam double subtotal) {
        return ResponseEntity.ok(lineaService.buscarPorSubtotalMayorA(subtotal));
    }
} 