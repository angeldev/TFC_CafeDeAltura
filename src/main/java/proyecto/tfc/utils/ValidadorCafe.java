package proyecto.tfc.utils;

import proyecto.tfc.entity.Cafe;

/**
 * Clase utilitaria para validar objetos de tipo Cafe.
 * Realiza comprobaciones de negocio sin usar anotaciones ni validadores externos.
 * 
 * Permite verificar si un café tiene datos válidos completos o al menos un campo para actualización parcial.
 * 
 * Se utiliza desde CafeService para validar operaciones POST, PUT y PATCH.
 * 
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
public class ValidadorCafe {

    /**
     * Valida un objeto Cafe según reglas de negocio.
     * @param cafe objeto a validar
     * @return true si es válido
     */
    public static boolean esValido(Cafe cafe) {
        if (cafe == null) return false;
        if (cafe.getNombre() == null || cafe.getNombre().trim().isEmpty()) return false;
        if (cafe.getDescripcion() == null || cafe.getDescripcion().trim().length() < ConstantesCafe.DESCRIPCION_MIN) return false;
        if (cafe.getPrecio() == null || cafe.getPrecio() < ConstantesCafe.PRECIO_MIN) return false;
        return cafe.getIntensidad() >= ConstantesCafe.INTENSIDAD_MIN && cafe.getIntensidad() <= ConstantesCafe.INTENSIDAD_MAX;
    }

    /**
     * Verifica que al menos un campo del café sea válido para modificación parcial.
     *
     * @param cafe objeto a validar parcialmente
     * @return true si tiene al menos un campo no nulo o significativo, false en caso contrario
     */
    public static boolean tieneAlMenosUnCampoValido(Cafe cafe) {
        return cafe != null && (
            (cafe.getNombre() != null && !cafe.getNombre().isBlank()) ||
            cafe.getDescripcion() != null ||
            (cafe.getPrecio() != null && cafe.getPrecio() > 0) ||
            cafe.getOrigen() != null ||
            (cafe.getIntensidad() > 0)
        );
    }
}
