package com.alexis.sprintboot.app.Service;


import com.alexis.sprintboot.app.DTO.ProductoDTO;
import com.alexis.sprintboot.app.DTO.ProductoFilterDTO;
import com.alexis.sprintboot.app.Model.Categoria;
import com.alexis.sprintboot.app.Model.Producto;
import com.alexis.sprintboot.app.Repository.CategoriaRepository;
import com.alexis.sprintboot.app.Repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Crear un nuevo producto
     */
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        // Validar SKU único
        if (productoRepository.existsBySku(productoDTO.getSku())) {
            throw new IllegalArgumentException("El SKU ya está en uso: " + productoDTO.getSku());
        }

        // Validar categoría
        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Categoría no encontrada con ID: " + productoDTO.getCategoriaId()
                ));

        // Crear producto
        Producto producto = new Producto();
        producto.setSku(productoDTO.getSku());
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStockActual(productoDTO.getStockActual() != null ? productoDTO.getStockActual() : 0);
        producto.setCategoria(categoria);

        Producto productoGuardado = productoRepository.save(producto);
        return convertirADTO(productoGuardado);
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con ID: " + id
                ));
        return convertirADTO(producto);
    }

    /**
     * Obtener producto por SKU
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorSku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con SKU: " + sku
                ));
        return convertirADTO(producto);
    }

    /**
     * Actualizar producto existente
     */
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con ID: " + id
                ));

        // Validar SKU único si se cambia
        if (productoDTO.getSku() != null && !productoDTO.getSku().equals(producto.getSku())) {
            if (productoRepository.existsBySku(productoDTO.getSku())) {
                throw new IllegalArgumentException("El SKU ya está en uso: " + productoDTO.getSku());
            }
            producto.setSku(productoDTO.getSku());
        }

        // Actualizar campos
        if (productoDTO.getNombre() != null) {
            producto.setNombre(productoDTO.getNombre());
        }

        if (productoDTO.getPrecio() != null) {
            producto.setPrecio(productoDTO.getPrecio());
        }

        if (productoDTO.getStockActual() != null) {
            producto.setStockActual(productoDTO.getStockActual());
        }

        // Actualizar categoría si se proporciona
        if (productoDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Categoría no encontrada con ID: " + productoDTO.getCategoriaId()
                    ));
            producto.setCategoria(categoria);
        }

        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }

    /**
     * Eliminar producto por ID
     */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Listar todos los productos con paginación
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> listarProductos(Pageable pageable) {
        Page<Producto> productos = productoRepository.findAll(pageable);
        return productos.map(this::convertirADTO);
    }

    /**
     * Filtrar productos con múltiples criterios
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> filtrarProductos(ProductoFilterDTO filtro) {
        // Validar filtros de precio
        if (filtro.getPrecioMin() != null && filtro.getPrecioMax() != null) {
            if (filtro.getPrecioMin().compareTo(filtro.getPrecioMax()) > 0) {
                throw new IllegalArgumentException("El precio mínimo no puede ser mayor al precio máximo");
            }
        }

        // Configurar paginación
        Pageable pageable = PageRequest.of(
                filtro.getPage() != null ? filtro.getPage() : 0,
                filtro.getSize() != null ? filtro.getSize() : 10,
                Sort.by(Sort.Direction.ASC, "nombre")
        );

        // Ejecutar consulta con filtros
        Page<Producto> productos = productoRepository.buscarConFiltros(
                filtro.getNombre(),
                filtro.getPrecioMin(),
                filtro.getPrecioMax(),
                filtro.getCategoriaId(),
                pageable
        );

        return productos.map(this::convertirADTO);
    }

    /**
     * Buscar productos por nombre (para autocompletado)
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }

        List<Producto> productos = productoRepository.findAll()
                .stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .limit(10) // Limitar resultados para autocompletado
                .collect(Collectors.toList());

        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Aumentar stock de un producto
     */
    public ProductoDTO aumentarStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con ID: " + productoId
                ));

        producto.setStockActual(producto.getStockActual() + cantidad);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }

    /**
     * Disminuir stock de un producto (con control de concurrencia)
     */
    public ProductoDTO disminuirStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con ID: " + productoId
                ));

        if (producto.getStockActual() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + producto.getStockActual() +
                            ", Solicitado: " + cantidad
            );
        }

        producto.setStockActual(producto.getStockActual() - cantidad);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }

    /**
     * Verificar disponibilidad de stock
     */
    @Transactional(readOnly = true)
    public boolean verificarStockDisponible(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producto no encontrado con ID: " + productoId
                ));

        return producto.getStockActual() >= cantidad;
    }

    /**
     * Obtener valor total del inventario
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalInventario() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .filter(p -> p.getPrecio() != null && p.getStockActual() != null)
                .map(p -> p.getPrecio().multiply(new BigDecimal(p.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Convertir Producto a ProductoDTO
     */
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStockActual(producto.getStockActual());

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return dto;
    }
}