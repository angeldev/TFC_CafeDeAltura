package proyecto.tfc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import proyecto.tfc.dto.CafeDTO;
import proyecto.tfc.dto.RespuestaPaginada;
import proyecto.tfc.entity.Cafe;
import proyecto.tfc.services.CafeService;

/**
 * Controlador REST para la gestión de cafés.
 * Proporciona endpoints para operaciones CRUD y consultas avanzadas sobre cafés.
 * Las validaciones y lógica de negocio se delegan al servicio correspondiente.
 *
 * @author Lola Fernández Fuentes
 * @version 1.0
 * @since 2025-05-23
 */
@RestController
@RequestMapping("/api/cafe")
public class CafeController {

    private static final Logger log = LoggerFactory.getLogger(CafeController.class);

    private CafeService cafeService;

    @Autowired
    public void setCafeService(@Lazy CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping
    public ResponseEntity<RespuestaPaginada<Cafe>> listarTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1 || size < 1) {
            throw new proyecto.tfc.exceptions.BadRequestException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }
        log.info("[GET] /cafe - Listando cafés paginados (page={}, size={})", page, size);
        org.springframework.data.domain.Page<Cafe> pageCafes = cafeService.listarCafesPaginado(page, size);
        RespuestaPaginada.MetadatosPaginacion meta = new RespuestaPaginada.MetadatosPaginacion(
                pageCafes.getNumber() + 1,
                pageCafes.getSize(),
                pageCafes.getTotalPages(),
                pageCafes.getTotalElements(),
                pageCafes.isLast(),
                pageCafes.isFirst()
        );
        RespuestaPaginada<Cafe> respuesta = new RespuestaPaginada<>(pageCafes.getContent(), meta);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cafe> obtenerPorId(@PathVariable Long id) {
        Cafe cafe = cafeService.obtenerCafePorId(id);
        log.info("[GET] /cafe/{} - Café encontrado: {}", id, cafe);
        return ResponseEntity.ok(cafe);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Cafe> crearCafe(@Valid @RequestBody CafeDTO cafeDTO) {
        Cafe cafe = new Cafe(
            cafeDTO.getNombre(),
            cafeDTO.getDescripcion(),
            cafeDTO.getPrecio(),
            cafeDTO.getOrigen(),
            cafeDTO.getIntensidad(),
            cafeDTO.getStock()
        );
        Cafe creado = cafeService.crearCafe(cafe);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Cafe> actualizarCafe(@PathVariable Long id, @Valid @RequestBody CafeDTO cafeDTO) {
        Cafe nuevoCafe = new Cafe(
            cafeDTO.getNombre(),
            cafeDTO.getDescripcion(),
            cafeDTO.getPrecio(),
            cafeDTO.getOrigen(),
            cafeDTO.getIntensidad(),
            cafeDTO.getStock()
        );
        Cafe actualizado = cafeService.actualizarCafe(id, nuevoCafe);
        return ResponseEntity.ok(actualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Cafe> modificarParcial(@PathVariable Long id, @Valid @RequestBody CafeDTO cafeDTO) {
        Cafe cafe = new Cafe(
            cafeDTO.getNombre(),
            cafeDTO.getDescripcion(),
            cafeDTO.getPrecio(),
            cafeDTO.getOrigen(),
            cafeDTO.getIntensidad(),
            cafeDTO.getStock()
        );
        Cafe modificado = cafeService.modificarParcialCafe(id, cafe);
        return ResponseEntity.ok(modificado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCafe(@PathVariable Long id) {
        log.info("[DELETE] /cafe/{} - Eliminando café", id);
        cafeService.eliminarCafe(id);
        log.info("[DELETE] /cafe/{} - Café eliminado", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stockBajo")
    public ResponseEntity<java.util.List<Cafe>> buscarCafesConStockBajo(@RequestParam int umbral) {
        return ResponseEntity.ok(cafeService.buscarCafesConStockBajo(umbral));
    }

    @GetMapping("/intensidadRango")
    public ResponseEntity<java.util.List<Cafe>> buscarPorIntensidadEnRango(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(cafeService.buscarPorIntensidadEnRango(min, max));
    }

    @GetMapping("/buscarPorPalabra")
    public ResponseEntity<java.util.List<Cafe>> buscarPorNombreODescripcion(@RequestParam String palabra) {
        return ResponseEntity.ok(cafeService.buscarPorNombreODescripcion(palabra));
    }

    @GetMapping("/agotados")
    public ResponseEntity<java.util.List<Cafe>> buscarCafesAgotados() {
        return ResponseEntity.ok(cafeService.buscarCafesAgotados());
    }
}
