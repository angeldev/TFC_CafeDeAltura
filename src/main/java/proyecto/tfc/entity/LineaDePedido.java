package proyecto.tfc.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Clase que representa una línea individual dentro de un pedido.
 * Cada línea corresponde a un producto concreto (café),
 * con su cantidad solicitada, su precio unitario en el momento del pedido
 * y un subtotal calculado (precioUnitario × cantidad).
 *
 * La cantidad de cada línea no puede superar el stock disponible del café
 * en el momento de la compra. Esta validación se realiza en la capa de servicio.
 *
 * Esta clase forma parte de la estructura del recurso Pedido y se
 * utiliza para representar el detalle de cada producto solicitado.
 * Incluye snapshot de datos clave (nombre y precio del café) para
 * mantener la integridad del pedido aunque el producto cambie después.
 *
 * Ejemplo de uso:
 * LineaDePedido linea = new LineaDePedido(3L, "Etiopía Sidamo", 9.95, 2);
 *
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-26
 */
@Entity
public class LineaDePedido implements Serializable {
    /** Identificador único de la línea de pedido, generado automáticamente por JPA */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Precio del café en el momento de la compra (puede diferir del precio actual) */
    @Column(nullable = false)
    private Double precioUnitario;

    /**
     * Cantidad solicitada de unidades de este café.
     * No puede superar el stock disponible del café en el momento de la compra.
     */
    @Column(nullable = false)
    private int cantidad;

    /** Subtotal de la línea (precioUnitario × cantidad) */
    @Column
    private Double subtotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    /**
     * Constructor vacío necesario para serialización y frameworks como Spring.
     */
    public LineaDePedido() {
    }

    /**
     * Constructor completo para inicializar todos los campos de una línea de pedido.
     * El subtotal se calcula automáticamente.
     *
     * @param precioUnitario precio unitario aplicado en esta línea
     * @param cantidad número de unidades de café en esta línea
     */
    public LineaDePedido(Double precioUnitario, int cantidad) {
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = precioUnitario != null ? precioUnitario * cantidad : 0.0;
    }

    /**
     * Constructor para inicializar todos los campos de una línea de pedido.
     * El subtotal se calcula automáticamente.
     *
     * @param cafe café asociado a esta línea
     * @param precioUnitario precio unitario aplicado en esta línea
     * @param cantidad cantidad de café en esta línea
     */
    public LineaDePedido(Cafe cafe, Double precioUnitario, int cantidad) {
        this.cafe = cafe;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = precioUnitario != null ? precioUnitario * cantidad : 0.0;
    }

    /**
     * Devuelve el identificador único de la línea de pedido.
     * @return id de la línea
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la línea de pedido.
     * @param id identificador a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el precio unitario aplicado en esta línea.
     * @return precio por unidad en euros
     */
    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Asigna el precio unitario para esta línea.
     * @param precioUnitario precio por unidad
     */
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    /**
     * Devuelve la cantidad de unidades solicitadas de este café.
     * @return número de unidades
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de unidades de café en esta línea.
     * @param cantidad número entero de unidades
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    /**
     * Devuelve el subtotal de la línea (precioUnitario × cantidad).
     * @return subtotal calculado
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * Establece el subtotal de la línea (precioUnitario × cantidad).
     * @param subtotal subtotal calculado
     */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Recalcula el subtotal cuando cambian precio o cantidad.
     */
    private void recalcularSubtotal() {
        this.subtotal = Objects.requireNonNullElse(precioUnitario, 0.0) * cantidad;
    }

    /**
     * Representación textual de la línea de pedido.
     * @return cadena con los datos de la línea
     */
    @Override
    public String toString() {
        return "LineaDePedido{" +
                "id=" + id +
                ", cafeId=" + (cafe != null ? cafe.getId() : null) +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                ", precioUnitario=" + precioUnitario +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }

    /**
     * Compara dos líneas de pedido por id.
     * @param o objeto a comparar
     * @return true si tienen el mismo id, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineaDePedido that = (LineaDePedido) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calcula el código hash basado en id.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }
}
