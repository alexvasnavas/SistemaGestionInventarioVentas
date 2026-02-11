package com.alexis.sprintboot.app.Repository;


import com. alexis.sprintboot.app.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Buscar categoría por nombre (exacto)
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Buscar categorías por nombre (contiene)
     */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verificar si existe una categoría con el nombre dado
     */
    boolean existsByNombre(String nombre);

    /**
     * Obtener categorías ordenadas por nombre
     */
    List<Categoria> findAllByOrderByNombreAsc();
}