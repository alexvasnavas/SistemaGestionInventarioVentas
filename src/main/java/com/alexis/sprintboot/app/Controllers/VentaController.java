package com.alexis.sprintboot.app.Controllers;


import com.alexis.sprintboot.app.DTO.VentaRequestDTO;
import com.alexis.sprintboot.app.DTO.VentaResponseDTO;
import com.alexis.sprintboot.app.Service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Crear una nueva venta
     * POST /api/ventas
     */
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO ventaRequest) {
        try {
            VentaResponseDTO ventaProcesada = ventaService.procesarVenta(ventaRequest);
            return new ResponseEntity<>(ventaProcesada, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log del error (en producción usarías un logger)
            System.err.println("Error al procesar venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    crearRespuestaError("Error al procesar venta: " + e.getMessage())
            );
        }
    }

    /**
     * Obtener una venta por ID
     * GET /api/ventas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVenta(@PathVariable Long id) {
        try {
            VentaResponseDTO venta = ventaService.obtenerVentaPorId(id);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    crearRespuestaError("Venta no encontrada con ID: " + id)
            );
        }
    }

    /**
     * Listar todas las ventas (con paginación opcional)
     * GET /api/ventas
     */
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            List<VentaResponseDTO> ventas = ventaService.listarVentas(page, size);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    List.of(crearRespuestaError("Error al listar ventas: " + e.getMessage()))
            );
        }
    }

    /**
     * Obtener ventas por rango de fechas
     * GET /api/ventas/filtrar
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<VentaResponseDTO>> filtrarVentasPorFecha(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            List<VentaResponseDTO> ventas = ventaService.filtrarVentasPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    List.of(crearRespuestaError("Error en filtro de fechas: " + e.getMessage()))
            );
        }
    }

    /**
     * Obtener el total de ventas en un período
     * GET /api/ventas/total
     */
    @GetMapping("/total")
    public ResponseEntity<Double> obtenerTotalVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            Double total = ventaService.calcularTotalVentas(fechaInicio, fechaFin);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    /**
     * Obtener estadísticas de ventas
     * GET /api/ventas/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Object estadisticas = ventaService.obtenerEstadisticasVentas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para crear respuestas de error
     */
    private VentaResponseDTO crearRespuestaError(String mensaje) {
        VentaResponseDTO errorResponse = new VentaResponseDTO();
        errorResponse.setId(-1L); // ID negativo indica error
        // Puedes agregar un campo de mensaje en VentaResponseDTO si lo necesitas
        return errorResponse;
    }
}
