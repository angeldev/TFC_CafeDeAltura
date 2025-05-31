package proyecto.tfc.exceptions;

/**
 * Excepción personalizada para indicar errores de validación o datos incorrectos en las peticiones.
 * Se utiliza para devolver respuestas HTTP 400 Bad Request desde el manejador global de excepciones.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
} 