package proyecto.tfc.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Clase que representa un pedido realizado por un cliente en la tienda online de café.
 * Incluye información sobre el cliente, las líneas de pedido, la fecha de creación,
 * el total calculado, el estado del pedido y un comentario opcional.
 *
 * Al confirmar el pedido, el stock de los cafés solicitados se descuenta automáticamente.
 * La validación de stock (que la cantidad solicitada no supere el stock disponible)
 * se realiza en la capa de servicio antes de crear el pedido.
 *
 * Cada pedido está asociado a un cliente existente y contiene una lista de líneas de pedido,
 * donde cada línea representa un café, la cantidad solicitada y un snapshot de su nombre y precio.
 *
 * Esta clase está preparada para futuras ampliaciones (dirección de envío, método de pago, etc.).
 *
 * @author Lola Fernández Fuentes
 * @version 1.1
 * @since 2025-05-23
 */
@Entity
public class Pedido {
    /** Identificador único del pedido, generado automáticamente en memoria */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador del cliente que realiza el pedido */
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /**
     * Lista de líneas de pedido (productos y cantidades).
     * Cada línea representa un café, la cantidad solicitada y un snapshot de nombre y precio.
     * La cantidad de cada línea no puede superar el stock disponible del café en el momento de la compra.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<LineaDePedido> lineas = new java.util.ArrayList<>();

    /** Fecha y hora de creación del pedido (asignada automáticamente) */
    @Column
    private LocalDateTime fechaCreacion;

    /** Total del pedido (calculado automáticamente como suma de subtotales de las líneas) */
    @Column
    private Double total;

    /** Estado del pedido (ej: PENDIENTE, ENVIADO, CANCELADO) */
    @Column
    private String estado;

    /** Comentario opcional del cliente o del sistema */
    @Column
    private String comentario;

    /**
     * Constructor vacío necesario para frameworks y serialización.
     */
    public Pedido() {
    }

    /**
     * Constructor completo para inicializar los campos principales de un pedido.
     * El id y la fechaCreacion se asignan automáticamente en el repositorio/servicio.
     *
     * @param cliente cliente que realiza el pedido
     * @param lineas lista de líneas de pedido
     * @param estado estado inicial del pedido
     * @param comentario comentario opcional
     */
    public Pedido(Cliente cliente, java.util.List<LineaDePedido> lineas, String estado, String comentario) {
        this.cliente = cliente;
        this.lineas = lineas;
        this.fechaCreacion = java.time.LocalDateTime.now();
        this.estado = estado;
        this.comentario = comentario;
    }

    /**
     * Devuelve el identificador único del pedido.
     * @return ID del pedido
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del pedido.
     * @param id ID a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el cliente que realizó el pedido.
     * @return cliente que realizó el pedido
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Establece el cliente que realizó el pedido.
     * @param cliente cliente que realizó el pedido
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Devuelve la lista de líneas de pedido asociadas a este pedido.
     * @return lista de líneas de pedido
     */
    public List<LineaDePedido> getLineas() {
        return lineas;
    }

    /**
     * Establece la lista de líneas de pedido para este pedido.
     * @param lineas lista de líneas de pedido
     */
    public void setLineas(List<LineaDePedido> lineas) {
        this.lineas = lineas;
    }

    /**
     * Devuelve la fecha y hora de creación del pedido.
     * @return fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Establece la fecha y hora de creación del pedido.
     * @param fechaCreacion fecha de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Devuelve el total del pedido (suma de los subtotales de las líneas).
     * @return total del pedido
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Establece el total del pedido.
     * @param total valor total
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Devuelve el estado actual del pedido.
     * @return estado del pedido
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual del pedido.
     * @param estado estado del pedido
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el comentario asociado al pedido (opcional).
     * @return comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Establece el comentario asociado al pedido.
     * @param comentario comentario
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * Devuelve una representación textual del objeto Pedido para depuración.
     * @return cadena de texto con los valores de los atributos
     */
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId=" + (cliente != null ? cliente.getId() : null) +
                ", lineas=" + lineas +
                ", fechaCreacion=" + fechaCreacion +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", comentario='" + comentario + '\'' +
                '}';
    }

    /**
     * Compara dos objetos Pedido por su ID.
     * @param o objeto a comparar
     * @return true si tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    /**
     * Calcula el código hash del objeto basado en su ID.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
