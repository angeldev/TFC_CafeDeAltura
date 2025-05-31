package proyecto.tfc.exceptions;

/**
 * Excepción personalizada para indicar que un recurso solicitado no existe en el sistema.
 * Se utiliza para devolver respuestas HTTP 404 Not Found desde el manejador global de excepciones.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
} 