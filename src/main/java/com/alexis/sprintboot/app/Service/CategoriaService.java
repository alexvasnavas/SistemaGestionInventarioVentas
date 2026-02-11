package com.alexis.sprintboot.app.Service;


import com.alexis.sprintboot.app.DTO.CategoriaDTO;
import com.alexis.sprintboot.app.Model.Categoria;
import com.alexis.sprintboot.app.Repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Crear una nueva categoría
     */
    public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
        // Validar que el nombre no exista
        if (categoriaRepository.existsByNombre(categoriaDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaDTO.getNombre());
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return convertirADTO(categoriaGuardada);
    }

    /**
     * Obtener todas las categorías
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerTodasCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener categoría por ID
     */
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
        return convertirADTO(categoria);
    }

    /**
     * Actualizar categoría
     */
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que el nuevo nombre no exista (si se cambió)
        if (categoriaDTO.getNombre() != null && !categoriaDTO.getNombre().equals(categoria.getNombre())) {
            if (categoriaRepository.existsByNombre(categoriaDTO.getNombre())) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaDTO.getNombre());
            }
            categoria.setNombre(categoriaDTO.getNombre());
        }

        if (categoriaDTO.getDescripcion() != null) {
            categoria.setDescripcion(categoriaDTO.getDescripcion());
        }

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return convertirADTO(categoriaActualizada);
    }

    /**
     * Eliminar categoría
     */
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));

        // Verificar que la categoría no tenga productos asociados
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede eliminar la categoría porque tiene " +
                            categoria.getProductos().size() + " productos asociados"
            );
        }

        categoriaRepository.deleteById(id);
    }

    /**
     * Buscar categorías por nombre
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertir Categoria a CategoriaDTO
     */
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());

        if (categoria.getProductos() != null) {
            dto.setCantidadProductos(categoria.getProductos().size());
        }

        return dto;
    }
}