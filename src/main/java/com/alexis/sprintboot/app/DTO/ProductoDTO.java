package com.alexis.sprintboot.app.DTO;


import java.math.BigDecimal;

public class ProductoDTO {
    private Long id;
    private String sku;
    private String nombre;
    private BigDecimal precio;
    private Integer stockActual;
    private Long categoriaId;
    private String categoriaNombre;

    // Constructor vacío
    public ProductoDTO() {
    }

    // Constructor con todos los campos
    public ProductoDTO(Long id, String sku, String nombre, BigDecimal precio,
                       Integer stockActual, Long categoriaId, String categoriaNombre) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.precio = precio;
        this.stockActual = stockActual;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    // Constructor sin ID (para creación)
    public ProductoDTO(String sku, String nombre, BigDecimal precio,
                       Integer stockActual, Long categoriaId) {
        this.sku = sku;
        this.nombre = nombre;
        this.precio = precio;
        this.stockActual = stockActual;
        this.categoriaId = categoriaId;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    // Métodos de negocio/validación

    /**
     * Verifica si el DTO es válido para creación (sin ID)
     */
    public boolean isValidForCreate() {
        return sku != null && !sku.trim().isEmpty() &&
                nombre != null && !nombre.trim().isEmpty() &&
                precio != null && precio.compareTo(BigDecimal.ZERO) >= 0 &&
                stockActual != null && stockActual >= 0 &&
                categoriaId != null;
    }

    /**
     * Verifica si el DTO es válido para actualización (con ID)
     */
    public boolean isValidForUpdate() {
        return id != null && isValidForCreate();
    }

    /**
     * Calcula el valor total del inventario (precio * stock)
     */
    public BigDecimal calcularValorInventario() {
        if (precio == null || stockActual == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(new BigDecimal(stockActual));
    }

    // equals() y hashCode() basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductoDTO that = (ProductoDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // toString() para debugging

    @Override
    public String toString() {
        return "ProductoDTO{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stockActual=" + stockActual +
                ", categoriaId=" + categoriaId +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                '}';
    }
}