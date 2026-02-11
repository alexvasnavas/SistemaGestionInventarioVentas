package com.alexis.sprintboot.app.Controllers;


import com.alexis.sprintboot.app.DTO.CategoriaDTO;
import com.alexis.sprintboot.app.Service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@Validated
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * POST /api/categorias
     * Crear una nueva categoría
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        try {
            CategoriaDTO nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("data", nuevaCategoria);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error de validación",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/categorias
     * Obtener todas las categorías
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodasCategorias() {
        try {
            List<CategoriaDTO> categorias = categoriaService.obtenerTodasCategorias();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categorias);
            response.put("total", categorias.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error al obtener categorías",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/categorias/{id}
     * Obtener categoría por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(crearSuccessResponse(categoria));

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Categoría no encontrada",
                    "No existe una categoría con ID: " + id
            ));
        }
    }

    /**
     * PUT /api/categorias/{id}
     * Actualizar categoría
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {
        try {
            CategoriaDTO categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaDTO);

            Map<String, Object> response = crearSuccessResponse(categoriaActualizada);
            response.put("message", "Categoría actualizada exitosamente");

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Categoría no encontrada",
                    "No existe una categoría con ID: " + id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error de validación",
                    e.getMessage()
            ));
        }
    }

    /**
     * DELETE /api/categorias/{id}
     * Eliminar categoría
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría eliminada exitosamente");
            response.put("deletedId", id);

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Categoría no encontrada",
                    "No existe una categoría con ID: " + id
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(crearErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "No se puede eliminar la categoría",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/categorias/buscar
     * Buscar categorías por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCategoriasPorNombre(@RequestParam String nombre) {
        try {
            List<CategoriaDTO> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
            return ResponseEntity.ok(crearSuccessResponse(categorias));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error en búsqueda",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/categorias/{id}/productos
     * Obtener productos de una categoría (esto iría en ProductoController normalmente)
     */
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDeCategoria(@PathVariable Long id) {
        try {
            // Este endpoint normalmente estaría en ProductoController
            // Pero lo incluimos aquí para completar la funcionalidad
            CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);

            Map<String, Object> response = crearSuccessResponse(categoria);
            response.put("message", "Endpoint de productos por categoría");

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Categoría no encontrada",
                    "No existe una categoría con ID: " + id
            ));
        }
    }

    /**
     * Métodos auxiliares para respuestas estandarizadas
     */
    private Map<String, Object> crearSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> crearErrorResponse(int status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}