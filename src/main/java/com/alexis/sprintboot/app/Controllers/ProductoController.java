package com.alexis.sprintboot.app.Controllers;


import com.alexis.sprintboot.app.DTO.ProductoDTO;
import com.alexis.sprintboot.app.DTO.ProductoFilterDTO;
import com.alexis.sprintboot.app.Service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@Validated
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * POST /api/productos
     * Crear un nuevo producto
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado exitosamente");
            response.put("data", nuevoProducto);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error de validación",
                    e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error interno del servidor",
                    "No se pudo crear el producto"
            ));
        }
    }

    /**
     * GET /api/productos/{id}
     * Obtener un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(crearSuccessResponse(producto));

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
            ));
        }
    }

    /**
     * GET /api/productos/sku/{sku}
     * Obtener un producto por SKU
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<?> obtenerProductoPorSku(@PathVariable String sku) {
        try {
            ProductoDTO producto = productoService.obtenerProductoPorSku(sku);
            return ResponseEntity.ok(crearSuccessResponse(producto));

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con SKU: " + sku
            ));
        }
    }

    /**
     * GET /api/productos
     * Listar productos con filtros y paginación
     */
    @GetMapping
    public ResponseEntity<?> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre,asc") String[] sort,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Long categoriaId) {

        try {
            // Crear objeto de filtro
            ProductoFilterDTO filtro = new ProductoFilterDTO();
            filtro.setNombre(nombre);
            filtro.setPrecioMin(precioMin);
            filtro.setPrecioMax(precioMax);
            filtro.setCategoriaId(categoriaId);
            filtro.setPage(page);
            filtro.setSize(size);

            // Validar filtros de precio
            if (filtro.getPrecioMin() != null && filtro.getPrecioMax() != null) {
                if (filtro.getPrecioMin().compareTo(filtro.getPrecioMax()) > 0) {
                    return ResponseEntity.badRequest().body(crearErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            "Parámetros inválidos",
                            "El precio mínimo no puede ser mayor al precio máximo"
                    ));
                }
            }

            // Ejecutar búsqueda
            Page<ProductoDTO> productosPage;

            if (filtro.hasAnyFilter()) {
                productosPage = productoService.filtrarProductos(filtro);
            } else {
                // Configurar paginación con ordenamiento
                List<Sort.Order> orders = new ArrayList<>();
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(
                            _sort.length > 1 && _sort[1].equalsIgnoreCase("desc") ?
                                    Sort.Direction.DESC : Sort.Direction.ASC,
                            _sort[0]
                    ));
                }
                Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
                productosPage = productoService.listarProductos(pageable);
            }

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productosPage.getContent());
            response.put("page", productosPage.getNumber());
            response.put("size", productosPage.getSize());
            response.put("totalElements", productosPage.getTotalElements());
            response.put("totalPages", productosPage.getTotalPages());
            response.put("last", productosPage.isLast());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Parámetros de búsqueda inválidos",
                    e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error al listar productos",
                    e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/productos/{id}
     * Actualizar un producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);

            Map<String, Object> response = crearSuccessResponse(productoActualizado);
            response.put("message", "Producto actualizado exitosamente");

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
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
     * DELETE /api/productos/{id}
     * Eliminar un producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado exitosamente");
            response.put("deletedId", id);

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
            ));
        }
    }

    /**
     * POST /api/productos/{id}/stock/aumentar
     * Aumentar stock de un producto
     */
    @PostMapping("/{id}/stock/aumentar")
    public ResponseEntity<?> aumentarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        try {
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().body(crearErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Cantidad inválida",
                        "La cantidad debe ser mayor a 0"
                ));
            }

            ProductoDTO productoActualizado = productoService.aumentarStock(id, cantidad);

            Map<String, Object> response = crearSuccessResponse(productoActualizado);
            response.put("message", "Stock aumentado exitosamente");
            response.put("cantidadAumentada", cantidad);

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error al aumentar stock",
                    e.getMessage()
            ));
        }
    }

    /**
     * POST /api/productos/{id}/stock/disminuir
     * Disminuir stock de un producto
     */
    @PostMapping("/{id}/stock/disminuir")
    public ResponseEntity<?> disminuirStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        try {
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().body(crearErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Cantidad inválida",
                        "La cantidad debe ser mayor a 0"
                ));
            }

            ProductoDTO productoActualizado = productoService.disminuirStock(id, cantidad);

            Map<String, Object> response = crearSuccessResponse(productoActualizado);
            response.put("message", "Stock disminuido exitosamente");
            response.put("cantidadDisminuida", cantidad);

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error al disminuir stock",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/productos/{id}/stock/disponible
     * Verificar stock disponible
     */
    @GetMapping("/{id}/stock/disponible")
    public ResponseEntity<?> verificarStockDisponible(
            @PathVariable Long id,
            @RequestParam Integer cantidadRequerida) {
        try {
            if (cantidadRequerida == null || cantidadRequerida <= 0) {
                return ResponseEntity.badRequest().body(crearErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Cantidad inválida",
                        "La cantidad requerida debe ser mayor a 0"
                ));
            }

            boolean disponible = productoService.verificarStockDisponible(id, cantidadRequerida);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productoId", id);
            response.put("cantidadRequerida", cantidadRequerida);
            response.put("stockDisponible", disponible);

            if (!disponible) {
                // Obtener stock actual para dar información más detallada
                ProductoDTO producto = productoService.obtenerProductoPorId(id);
                response.put("stockActual", producto.getStockActual());
                response.put("faltante", cantidadRequerida - producto.getStockActual());
            }

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Producto no encontrado",
                    "No existe un producto con ID: " + id
            ));
        }
    }

    /**
     * GET /api/productos/inventario/valor-total
     * Obtener valor total del inventario
     */
    @GetMapping("/inventario/valor-total")
    public ResponseEntity<?> obtenerValorTotalInventario() {
        try {
            BigDecimal valorTotal = productoService.calcularValorTotalInventario();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valorTotalInventario", valorTotal);
            response.put("moneda", "USD");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error al calcular inventario",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/productos/buscar
     * Búsqueda rápida por nombre (para autocompletado)
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarProductosPorNombre(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty() || query.length() < 2) {
                return ResponseEntity.ok(crearSuccessResponse(List.of()));
            }

            List<ProductoDTO> productos = productoService.buscarPorNombre(query.trim());
            return ResponseEntity.ok(crearSuccessResponse(productos));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error en búsqueda",
                    e.getMessage()
            ));
        }
    }

    /**
     * GET /api/productos/bajo-stock
     * Obtener productos con stock bajo
     */
    @GetMapping("/bajo-stock")
    public ResponseEntity<?> obtenerProductosBajoStock(
            @RequestParam(defaultValue = "10") Integer umbral) {
        try {
            // Este método necesitaría ser implementado en ProductoService
            // List<ProductoDTO> productos = productoService.obtenerProductosBajoStock(umbral);

            // Por ahora, simulamos la respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Endpoint en desarrollo");
            response.put("umbral", umbral);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error al obtener productos con bajo stock",
                    e.getMessage()
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