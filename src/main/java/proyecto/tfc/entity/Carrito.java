package proyecto.tfc.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

/**
 * Clase que representa el carrito de compra temporal de un cliente en la tienda online de café.
 * Permite almacenar las líneas de pedido (cafés y cantidades) que el cliente va seleccionando
 * antes de confirmar la compra y generar un pedido definitivo.
 *
 * El carrito está asociado a un cliente concreto y se almacena en memoria.
 * No descuenta stock hasta que se confirma el pedido.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-26
 */
@Entity
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineaCarrito> lineas = new ArrayList<>();

    /**
     * Fecha y hora de la última actualización del carrito (añadido, modificado o vaciado).
     */
    private LocalDateTime fechaActualizacion;

    /**
     * Constructor vacío necesario para frameworks y serialización.
     * Inicializa el mapa de líneas vacío y la fecha de actualización a ahora.
     */
    public Carrito() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Constructor completo para inicializar el carrito de un cliente.
     * @param cliente cliente asociado al carrito
     */
    public Carrito(Cliente cliente) {
        this.cliente = cliente;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<LineaCarrito> getLineas() { return lineas; }
    public void setLineas(List<LineaCarrito> lineas) { this.lineas = lineas; }

    /**
     * Devuelve la fecha y hora de la última actualización del carrito.
     * @return fecha de actualización
     */
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    /**
     * Establece la fecha y hora de la última actualización del carrito.
     * @param fechaActualizacion fecha de actualización
     */
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    /**
     * Devuelve una representación textual del carrito para depuración.
     * @return cadena con los valores de los atributos
     */
    @Override
    public String toString() {
        return "Carrito{" +
                "id=" + id +
                ", clienteId=" + (cliente != null ? cliente.getId() : null) +
                ", lineas=" + lineas +
                '}';
    }

    /**
     * Compara dos carritos por el ID del cliente.
     * @param o objeto a comparar
     * @return true si pertenecen al mismo cliente, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrito carrito = (Carrito) o;
        return Objects.equals(id, carrito.id);
    }

    /**
     * Calcula el código hash del carrito basado en el ID del cliente.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 