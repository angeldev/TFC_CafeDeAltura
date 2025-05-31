package proyecto.tfc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Clase que representa un café dentro del sistema de gestión de la tienda de café.
 * Contiene los atributos principales que describen un producto de tipo café,
 * incluyendo información como su nombre, descripción, precio, origen e intensidad.
 * Esta clase se utiliza como modelo de datos en las operaciones del backend.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@Entity
public class Cafe {

    public static final int INTENSIDAD_MINIMA = 1;
    public static final int INTENSIDAD_MAXIMA = 10;

    /** Identificador único del café, generado automáticamente en memoria */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre comercial del café, obligatorio para su creación */
    @Column(nullable = false)
    private String nombre;

    /** Descripción opcional del café (aroma, características, notas de cata, etc.) */
    @Column
    private String descripcion;

    /** Precio unitario del café en euros. Debe ser mayor que cero. */
    @Column(nullable = false)
    private Double precio;

    /** Origen geográfico del café (país o región de procedencia) */
    @Column(nullable = false)
    private String origen;

    /** Nivel de intensidad del café (escala orientativa del 1 al 10) */
    @Column(nullable = false)
    private Integer intensidad;

    /** Stock disponible de este café en la tienda */
    @Column(nullable = false)
    private Integer stock;

    /** Nombre del archivo de imagen asociado al café (opcional) */
    @Column
    private String imagen;

    @OneToMany(mappedBy = "cafe")
    private java.util.List<LineaDePedido> lineasDePedido = new java.util.ArrayList<>();

    /**
     * Constructor por defecto necesario para frameworks como Spring Boot
     * o bibliotecas de serialización/deserialización como Jackson.
     */
    public Cafe() {
    }

    /**
     * Constructor con todos los campos excepto el ID, que se asignará automáticamente
     * en el repositorio en memoria.
     *
     * @param nombre nombre del café
     * @param descripcion descripción detallada del café
     * @param precio precio unitario en euros
     * @param origen país o región de origen del café
     * @param intensidad nivel de intensidad del café (de 1 a 10)
     */
    public Cafe(String nombre, String descripcion, Double precio, String origen, int intensidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.origen = origen;
        this.intensidad = intensidad;
    }

    /**
     * Constructor completo para inicializar todos los campos de un café.
     * @param nombre nombre del café
     * @param descripcion descripción del café
     * @param precio precio del café
     * @param origen país de origen
     * @param intensidad intensidad del café
     * @param stock stock disponible
     */
    public Cafe(String nombre, String descripcion, Double precio, String origen, Integer intensidad, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.origen = origen;
        this.intensidad = intensidad;
        this.stock = stock;
    }

    /**
     * Constructor completo para inicializar todos los campos de un café.
     * @param nombre nombre del café
     * @param descripcion descripción del café
     * @param precio precio del café
     * @param origen país de origen
     * @param intensidad intensidad del café
     * @param stock stock disponible
     * @param imagen nombre del archivo de imagen asociado al café (opcional)
     */
    public Cafe(String nombre, String descripcion, Double precio, String origen, Integer intensidad, int stock, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.origen = origen;
        this.intensidad = intensidad;
        this.stock = stock;
        this.imagen = imagen;
    }

    /**
     * Devuelve el ID único del café.
     * @return identificador del café
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del café.
     * @param id identificador único
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del café.
     * @return nombre del café
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del café.
     * @param nombre nombre comercial
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción del café.
     * @return descripción detallada
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del café.
     * @param descripcion descripción textual
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el precio del café.
     * @return precio en euros
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del café.
     * @param precio valor numérico en euros
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Devuelve el origen del café.
     * @return país o región de origen
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Establece el origen del café.
     * @param origen país o región de origen
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * Devuelve el nivel de intensidad del café.
     * @return intensidad del 1 al 10
     */
    public Integer getIntensidad() {
        return intensidad;
    }

    /**
     * Establece el nivel de intensidad del café.
     * @param intensidad nivel del 1 al 10
     */
    public void setIntensidad(Integer intensidad) {
        this.intensidad = intensidad;
    }

    /**
     * Devuelve el stock disponible del café.
     * @return stock disponible
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Establece el stock disponible del café.
     * @param stock stock disponible
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Devuelve el nombre del archivo de imagen asociado al café.
     * @return nombre del archivo de imagen
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Establece el nombre del archivo de imagen asociado al café.
     * @param imagen nombre del archivo de imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * Devuelve una representación textual del objeto Café para depuración.
     * @return cadena de texto con los valores de los atributos
     */
    @Override
    public String toString() {
        return "Cafe{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", origen='" + origen + '\'' +
                ", intensidad=" + intensidad +
                ", stock=" + stock +
                ", imagen='" + imagen + '\'' +
                '}';
    }

    /**
     * Compara dos objetos Café por su ID.
     * @param o objeto a comparar
     * @return true si tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cafe cafe = (Cafe) o;

        return id != null ? id.equals(cafe.id) : cafe.id == null;
    }

    /**
     * Calcula el código hash del objeto basado en su ID.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public java.util.List<LineaDePedido> getLineasDePedido() {
        return lineasDePedido;
    }

    public void setLineasDePedido(java.util.List<LineaDePedido> lineasDePedido) {
        this.lineasDePedido = lineasDePedido;
    }
}
