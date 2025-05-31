package proyecto.tfc.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PedidoDTO {
    @NotNull(message = "El cliente es obligatorio")
    private ClienteId cliente;

    @NotNull(message = "Debe haber al menos una línea de pedido")
    @Size(min = 1, message = "Debe haber al menos una línea de pedido")
    private List<LineaDTO> lineas;

    private String estado;
    private String comentario;

    // Getters y setters
    public ClienteId getCliente() { return cliente; }
    public void setCliente(ClienteId cliente) { this.cliente = cliente; }
    public List<LineaDTO> getLineas() { return lineas; }
    public void setLineas(List<LineaDTO> lineas) { this.lineas = lineas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public static class ClienteId {
        @NotNull(message = "El id del cliente es obligatorio")
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    public static class LineaDTO {
        @NotNull(message = "El café es obligatorio")
        private CafeId cafe;
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;
        public CafeId getCafe() { return cafe; }
        public void setCafe(CafeId cafe) { this.cafe = cafe; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
    public static class CafeId {
        @NotNull(message = "El id del café es obligatorio")
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
} 