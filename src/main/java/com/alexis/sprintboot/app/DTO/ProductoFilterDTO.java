package com.alexis.sprintboot.app.DTO;


import java.math.BigDecimal;

public class ProductoFilterDTO {
    private String nombre;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private Long categoriaId;
    private Integer page;
    private Integer size;

    // Constructor vacío
    public ProductoFilterDTO() {
        this.page = 0;
        this.size = 10;
    }

    // Constructor con parámetros
    public ProductoFilterDTO(String nombre, BigDecimal precioMin, BigDecimal precioMax,
                             Long categoriaId, Integer page, Integer size) {
        this.nombre = nombre;
        this.precioMin = precioMin;
        this.precioMax = precioMax;
        this.categoriaId = categoriaId;
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
    }

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(BigDecimal precioMin) {
        this.precioMin = precioMin;
    }

    public BigDecimal getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(BigDecimal precioMax) {
        this.precioMax = precioMax;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null ? page : 0;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }

    // Métodos de validación

    public boolean hasNombreFilter() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    public boolean hasPrecioFilter() {
        return precioMin != null || precioMax != null;
    }

    public boolean hasCategoriaFilter() {
        return categoriaId != null;
    }

    public boolean hasAnyFilter() {
        return hasNombreFilter() || hasPrecioFilter() || hasCategoriaFilter();
    }

    /**
     * Valida que los filtros de precio sean consistentes
     */
    public boolean isPrecioFilterValid() {
        if (precioMin != null && precioMax != null) {
            return precioMin.compareTo(precioMax) <= 0;
        }
        return true;
    }

    /**
     * Valida que la paginación sea correcta
     */
    public boolean isPaginationValid() {
        return page >= 0 && size > 0 && size <= 100; // Límite de 100 items por página
    }

    // equals() y hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductoFilterDTO that = (ProductoFilterDTO) o;

        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (precioMin != null ? !precioMin.equals(that.precioMin) : that.precioMin != null) return false;
        if (precioMax != null ? !precioMax.equals(that.precioMax) : that.precioMax != null) return false;
        if (categoriaId != null ? !categoriaId.equals(that.categoriaId) : that.categoriaId != null) return false;
        if (page != null ? !page.equals(that.page) : that.page != null) return false;
        return size != null ? size.equals(that.size) : that.size == null;
    }

    @Override
    public int hashCode() {
        int result = nombre != null ? nombre.hashCode() : 0;
        result = 31 * result + (precioMin != null ? precioMin.hashCode() : 0);
        result = 31 * result + (precioMax != null ? precioMax.hashCode() : 0);
        result = 31 * result + (categoriaId != null ? categoriaId.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    // toString()

    @Override
    public String toString() {
        return "ProductoFilterDTO{" +
                "nombre='" + nombre + '\'' +
                ", precioMin=" + precioMin +
                ", precioMax=" + precioMax +
                ", categoriaId=" + categoriaId +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}