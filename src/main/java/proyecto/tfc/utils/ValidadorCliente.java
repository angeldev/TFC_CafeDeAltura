package proyecto.tfc.utils;

import proyecto.tfc.entity.Cliente;

/**
 * Clase utilitaria para validar objetos de tipo Cliente.
 * Realiza comprobaciones de negocio de forma manual, sin usar anotaciones ni validadores externos.
 *
 * Permite verificar si un cliente tiene datos válidos completos para su creación o actualización.
 * Se utiliza desde ClienteService para validar operaciones POST y PUT.
 *
 * Reglas de validación:
 * - El nombre no puede ser nulo ni estar vacío.
 * - El email no puede ser nulo, vacío y debe tener formato válido (contener '@' y un punto después).
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
public class ValidadorCliente {
    /**
     * Valida que un cliente tenga todos los datos obligatorios para su creación o actualización completa.
     *
     * @param cliente objeto Cliente a validar
     * @return true si el objeto es válido, false si faltan datos obligatorios o el email es incorrecto
     */
    public static boolean esValido(Cliente cliente) {
        if (cliente == null) return false;
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) return false;
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) return false;
        return esEmailValido(cliente.getEmail());
    }

    /**
     * Verifica que el email tenga un formato válido básico (contiene '@' y un punto después).
     *
     * @param email email a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        int at = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        return at > 0 && dot > at;
    }
} 