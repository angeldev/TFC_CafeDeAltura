package proyecto.tfc.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import proyecto.tfc.entity.Cafe;
import proyecto.tfc.entity.Cliente;
import proyecto.tfc.entity.LineaDePedido;
import proyecto.tfc.entity.Pedido;
import proyecto.tfc.services.CafeService;
import proyecto.tfc.services.ClienteService;
import proyecto.tfc.services.PedidoService;

@Component
@Profile({"mem", "jpa"})
public class DataLoader implements CommandLineRunner {

    private CafeService cafeService;
    private ClienteService clienteService;
    private PedidoService pedidoService;

    @Autowired
    public void setServices(@Lazy CafeService cafeService, @Lazy ClienteService clienteService, @Lazy PedidoService pedidoService) {
        this.cafeService = cafeService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @Override
    public void run(String... args) {
        // Carga de cafés
        if (cafeService.listarCafes().isEmpty()) {
            cafeService.crearCafe(new Cafe("Altura Colombia", "Notas a chocolate y frutos rojos", 10.5, "Colombia", 7, 50));
            cafeService.crearCafe(new Cafe("Brasil Cerrado", "Suave, achocolatado, baja acidez", 8.9, "Brasil", 5, 80));
            cafeService.crearCafe(new Cafe("Etiopía Yirgacheffe", "Aromático, floral, cítricos", 12.2, "Etiopía", 8, 30));
            cafeService.crearCafe(new Cafe("Guatemala Antigua", "Cuerpo medio, cacao y nuez", 11.0, "Guatemala", 6, 40));
            cafeService.crearCafe(new Cafe("Sumatra Mandheling", "Intenso, terroso, final largo", 13.8, "Indonesia", 9, 25));
            cafeService.crearCafe(new Cafe("Descafeinado Swiss Water", "Descafeinado natural", 9.5, "Perú", 4, 20));
            System.out.println("Cafés de ejemplo cargados correctamente.");
        }
        // Carga de clientes
        if (clienteService.listarClientes().isEmpty()) {
            clienteService.crearCliente(new Cliente("Marina López", "marina.lopez@gmail.com", "600123456", "Calle Mayor 12, Madrid"));
            clienteService.crearCliente(new Cliente("Javier Ortega", "javier.ortega@empresa.com", "611987654", "Avda. Andalucía 45, Sevilla"));
            clienteService.crearCliente(new Cliente("Sofía Müller", "sofia.muller@correo.de", "699112233", "Plaza Europa 3, Barcelona"));
            System.out.println("Clientes de ejemplo cargados correctamente.");
        }
        // Carga de pedidos
        if (pedidoService.listarPedidos().isEmpty() && !clienteService.listarClientes().isEmpty() && !cafeService.listarCafes().isEmpty()) {
            List<Cliente> clientes = new ArrayList<>(clienteService.listarClientes());
            List<Cafe> cafes = new ArrayList<>(cafeService.listarCafes());
            // Pedido 1
            List<LineaDePedido> lineas1 = new ArrayList<>();
            lineas1.add(new LineaDePedido(cafes.get(0), cafes.get(0).getPrecio(), 2));
            lineas1.add(new LineaDePedido(cafes.get(2), cafes.get(2).getPrecio(), 1));
            Pedido pedido1 = new Pedido(clientes.get(0), lineas1, "PENDIENTE", "Entrega en horario de mañana");
            pedido1.setFechaCreacion(LocalDateTime.now().minusDays(2));
            pedido1.setTotal(lineas1.stream().mapToDouble(l -> l.getPrecioUnitario() * l.getCantidad()).sum());
            // Pedido 2
            List<LineaDePedido> lineas2 = new ArrayList<>();
            lineas2.add(new LineaDePedido(cafes.get(1), cafes.get(1).getPrecio(), 2));
            lineas2.add(new LineaDePedido(cafes.get(5), cafes.get(5).getPrecio(), 1));
            Pedido pedido2 = new Pedido(clientes.get(1), lineas2, "ENVIADO", "Sin azúcar, por favor");
            pedido2.setFechaCreacion(LocalDateTime.now().minusDays(1));
            pedido2.setTotal(lineas2.stream().mapToDouble(l -> l.getPrecioUnitario() * l.getCantidad()).sum());
            // Pedido 3
            List<LineaDePedido> lineas3 = new ArrayList<>();
            lineas3.add(new LineaDePedido(cafes.get(4), cafes.get(4).getPrecio(), 1));
            Pedido pedido3 = new Pedido(clientes.get(2), lineas3, "PENDIENTE", null);
            pedido3.setFechaCreacion(LocalDateTime.now());
            pedido3.setTotal(lineas3.stream().mapToDouble(l -> l.getPrecioUnitario() * l.getCantidad()).sum());
            // Guardar pedidos
            pedidoService.crearPedido(pedido1);
            pedidoService.crearPedido(pedido2);
            pedidoService.crearPedido(pedido3);
            System.out.println("Pedidos de ejemplo cargados correctamente.");
        }
    }
} 