package proyecto.tfc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CafeDTO {
    @NotBlank(message = "{nombre.obligatorio}")
    private String nombre;

    private String descripcion;

    @NotNull(message = "{precio.invalido}")
    @Min(value = 0, message = "{precio.invalido}")
    private Double precio;

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotNull(message = "{intensidad.invalida}")
    @Min(value = 1, message = "{intensidad.invalida}")
    @Max(value = 10, message = "{intensidad.invalida}")
    private Integer intensidad;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public Integer getIntensidad() { return intensidad; }
    public void setIntensidad(Integer intensidad) { this.intensidad = intensidad; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
} 