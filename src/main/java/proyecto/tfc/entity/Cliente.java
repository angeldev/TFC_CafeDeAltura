package proyecto.tfc.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Clase que representa un cliente dentro del sistema de gestión de la tienda de café online.
 * Contiene los atributos principales que describen a un cliente, incluyendo información de contacto,
 * dirección, fecha de registro y estado de actividad. Esta clase se utiliza como modelo de datos
 * en las operaciones del backend y está preparada para futuras ampliaciones.
 *
 * Los campos obligatorios son: nombre y email (con formato válido).
 * El campo fechaRegistro se asigna automáticamente al crear el cliente.
 * El campo activo indica si el cliente está dado de alta en el sistema.
 *
 * Ejemplo de uso:
 * Cliente cliente = new Cliente("Juan Pérez", "juan@email.com", "600123456", "Calle Mayor, 1");
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column
    private String telefono;

    @Column
    private String direccion;

    @Column
    private java.time.LocalDateTime fechaRegistro;

    @Column
    private boolean activo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Pedido> pedidos = new java.util.ArrayList<>();

    /**
     * Constructor vacío necesario para frameworks y serialización/deserialización.
     * El campo activo se inicializa a true por defecto.
     */
    public Cliente() {
        this.activo = true;
    }

    /**
     * Constructor completo para inicializar los campos principales de un cliente.
     * El id y la fechaRegistro se asignan automáticamente en el repositorio/servicio.
     *
     * @param nombre nombre completo del cliente (obligatorio)
     * @param email correo electrónico del cliente (obligatorio, formato válido)
     * @param telefono teléfono de contacto (opcional)
     * @param direccion dirección principal (opcional)
     */
    public Cliente(String nombre, String email, String telefono, String direccion) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    /**
     * Devuelve el identificador único del cliente.
     * @return id del cliente
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del cliente.
     * @param id identificador a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre completo del cliente.
     * @return nombre del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del cliente.
     * @param nombre nombre a asignar
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el correo electrónico del cliente.
     * @return email del cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del cliente.
     * @param email correo electrónico a asignar
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el teléfono de contacto del cliente.
     * @return teléfono del cliente
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono de contacto del cliente.
     * @param telefono número de teléfono a asignar
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Devuelve la dirección principal del cliente.
     * @return dirección del cliente
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección principal del cliente.
     * @param direccion dirección a asignar
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve la fecha y hora de registro del cliente.
     * @return fecha de registro
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha y hora de registro del cliente.
     * @param fechaRegistro fecha de registro a asignar
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Indica si el cliente está activo en el sistema.
     * @return true si está activo, false si está dado de baja
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del cliente.
     * @param activo true para activo, false para baja
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Devuelve una representación textual del objeto Cliente para depuración.
     * @return cadena de texto con los valores de los atributos
     */
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }

    /**
     * Compara dos objetos Cliente por su ID.
     * @param o objeto a comparar
     * @return true si tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    /**
     * Calcula el código hash del objeto basado en su ID.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public java.util.List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(java.util.List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
