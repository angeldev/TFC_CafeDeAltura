package proyecto.tfc.services;

import java.util.List;

import org.springframework.stereotype.Service;

import proyecto.tfc.entity.LineaDePedido;
import proyecto.tfc.repositories.jpa.LineaDePedidoJpaRepository;

/**
 * Servicio de negocio para la gestión de líneas de pedido.
 * Expone métodos de consulta avanzados del repositorio JPA.
 *
 * @author IA
 */
@Service
public class LineaDePedidoService {
    private final LineaDePedidoJpaRepository lineaRepo;

    public LineaDePedidoService(LineaDePedidoJpaRepository lineaRepo) {
        this.lineaRepo = lineaRepo;
    }

    /**
     * Busca líneas de pedido asociadas a un café concreto.
     * @param cafeId identificador del café
     * @return lista de líneas de pedido
     * @see proyecto.tfc.repositories.jpa.LineaDePedidoJpaRepository#findByCafeId(Long)
     * @example lineaDePedidoService.buscarPorCafe(1L);
     */
    public List<LineaDePedido> buscarPorCafe(Long cafeId) {
        return lineaRepo.findByCafeId(cafeId);
    }

    /**
     * Busca líneas de pedido asociadas a un pedido concreto.
     */
    public List<LineaDePedido> buscarPorPedido(Long pedidoId) {
        return lineaRepo.findByPedidoId(pedidoId);
    }

    /**
     * Busca líneas de pedido con cantidad mayor a un valor dado.
     */
    public List<LineaDePedido> buscarPorCantidadMayorA(int cantidad) {
        return lineaRepo.findByCantidadGreaterThan(cantidad);
    }

    /**
     * Busca líneas de pedido con subtotal mayor a un importe dado.
     */
    public List<LineaDePedido> buscarPorSubtotalMayorA(double subtotal) {
        return lineaRepo.findBySubtotalGreaterThan(subtotal);
    }
} 