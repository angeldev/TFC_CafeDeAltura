package proyecto.tfc.dto;

import java.util.List;

/**
 * DTO para respuestas paginadas uniformes en todos los perfiles.
 * @param <T> tipo de contenido
 *
 * Clase genérica para encapsular la respuesta paginada de la API REST.
 * <p>
 * Esta clase se utiliza para devolver tanto la lista de elementos de la página solicitada
 * como los metadatos de paginación (número de página, tamaño, totales, etc.) en los endpoints GET all.
 * <p>
 * Ejemplo de respuesta JSON:
 * <pre>
 * {
 *   "contenido": [ ... ],
 *   "paginacion": {
 *     "paginaActual": 1,
 *     "tamanoPagina": 10,
 *     "totalPaginas": 5,
 *     "totalElementos": 47,
 *     "esUltima": false,
 *     "esPrimera": true
 *   }
 * }
 * </pre>
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-28
 */
public class RespuestaPaginada<T> {

    /**
     * Lista de elementos de la página actual.
     */
    private List<T> contenido;

    /**
     * Metadatos de la paginación (número de página, tamaño, totales, etc.).
     */
    private MetadatosPaginacion paginacion;

    /**
     * Constructor completo para inicializar la respuesta paginada.
     *
     * @param contenido  lista de elementos de la página
     * @param paginacion metadatos de la paginación
     */
    public RespuestaPaginada(List<T> contenido, MetadatosPaginacion paginacion) {
        this.contenido = contenido;
        this.paginacion = paginacion;
    }

    /**
     * Devuelve la lista de elementos de la página actual.
     *
     * @return lista de elementos
     */
    public List<T> getContenido() {
        return contenido;
    }

    /**
     * Establece la lista de elementos de la página actual.
     *
     * @param contenido lista de elementos
     */
    public void setContenido(List<T> contenido) {
        this.contenido = contenido;
    }

    /**
     * Devuelve los metadatos de la paginación.
     *
     * @return metadatos de paginación
     */
    public MetadatosPaginacion getPaginacion() {
        return paginacion;
    }

    /**
     * Establece los metadatos de la paginación.
     *
     * @param paginacion metadatos de paginación
     */
    public void setPaginacion(MetadatosPaginacion paginacion) {
        this.paginacion = paginacion;
    }

    /**
     * Clase interna que representa los metadatos de paginación asociados a una respuesta paginada.
     * <p>
     * Incluye información sobre la página actual, tamaño, totales y si es la primera/última página.
     * <p>
     * Ejemplo de uso:
     * <pre>
     * new MetadatosPaginacion(1, 10, 5, 47, false, true);
     * </pre>
     */
    public static class MetadatosPaginacion {
        /** Número de la página actual (empieza en 1). */
        private int paginaActual;
        /** Tamaño de la página (número de elementos por página). */
        private int tamanoPagina;
        /** Número total de páginas disponibles. */
        private int totalPaginas;
        /** Número total de elementos en la colección. */
        private long totalElementos;
        /** Indica si es la última página. */
        private boolean esUltima;
        /** Indica si es la primera página. */
        private boolean esPrimera;

        /**
         * Constructor completo para los metadatos de paginación.
         *
         * @param paginaActual   número de la página actual (empieza en 1)
         * @param tamanoPagina   tamaño de la página (número de elementos por página)
         * @param totalPaginas   número total de páginas
         * @param totalElementos número total de elementos en la colección
         * @param esUltima       true si es la última página
         * @param esPrimera      true si es la primera página
         */
        public MetadatosPaginacion(int paginaActual, int tamanoPagina, int totalPaginas, long totalElementos, boolean esUltima, boolean esPrimera) {
            this.paginaActual = paginaActual;
            this.tamanoPagina = tamanoPagina;
            this.totalPaginas = totalPaginas;
            this.totalElementos = totalElementos;
            this.esUltima = esUltima;
            this.esPrimera = esPrimera;
        }

        /**
         * Devuelve el número de la página actual (empieza en 1).
         * @return número de página actual
         */
        public int getPaginaActual() {
            return paginaActual;
        }

        /**
         * Establece el número de la página actual.
         * @param paginaActual número de página actual
         */
        public void setPaginaActual(int paginaActual) {
            this.paginaActual = paginaActual;
        }

        /**
         * Devuelve el tamaño de la página (número de elementos por página).
         * @return tamaño de página
         */
        public int getTamanoPagina() {
            return tamanoPagina;
        }

        /**
         * Establece el tamaño de la página.
         * @param tamanoPagina tamaño de página
         */
        public void setTamanoPagina(int tamanoPagina) {
            this.tamanoPagina = tamanoPagina;
        }

        /**
         * Devuelve el número total de páginas.
         * @return total de páginas
         */
        public int getTotalPaginas() {
            return totalPaginas;
        }

        /**
         * Establece el número total de páginas.
         * @param totalPaginas total de páginas
         */
        public void setTotalPaginas(int totalPaginas) {
            this.totalPaginas = totalPaginas;
        }

        /**
         * Devuelve el número total de elementos en la colección.
         * @return total de elementos
         */
        public long getTotalElementos() {
            return totalElementos;
        }

        /**
         * Establece el número total de elementos en la colección.
         * @param totalElementos total de elementos
         */
        public void setTotalElementos(long totalElementos) {
            this.totalElementos = totalElementos;
        }

        /**
         * Indica si es la última página.
         * @return true si es la última página
         */
        public boolean isEsUltima() {
            return esUltima;
        }

        /**
         * Establece si es la última página.
         * @param esUltima true si es la última página
         */
        public void setEsUltima(boolean esUltima) {
            this.esUltima = esUltima;
        }

        /**
         * Indica si es la primera página.
         * @return true si es la primera página
         */
        public boolean isEsPrimera() {
            return esPrimera;
        }

        /**
         * Establece si es la primera página.
         * @param esPrimera true si es la primera página
         */
        public void setEsPrimera(boolean esPrimera) {
            this.esPrimera = esPrimera;
        }
    }
} 