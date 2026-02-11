package com.alexis.sprintboot.app.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Version
    private Long version;

    // Constructor vacío (requerido por JPA)
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(String sku, String nombre, BigDecimal precio, Integer stockActual, Categoria categoria) {
        this.sku = sku;
        this.nombre = nombre;
        this.precio = precio;
        this.stockActual = stockActual;
        this.categoria = categoria;
        this.version = 0L; // Versión inicial
    }

    // Constructor con parámetros sin categoría
    public Producto(String sku, String nombre, BigDecimal precio, Integer stockActual) {
        this.sku = sku;
        this.nombre = nombre;
        this.precio = precio;
        this.stockActual = stockActual;
        this.version = 0L;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Métodos de negocio

    /**
     * Verifica si hay stock suficiente para una cantidad dada
     */
    public boolean tieneStockSuficiente(Integer cantidad) {
        return this.stockActual >= cantidad;
    }

    /**
     * Reduce el stock en una cantidad específica
     * @throws IllegalArgumentException si no hay stock suficiente
     */
    public void reducirStock(Integer cantidad) {
        if (!tieneStockSuficiente(cantidad)) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + stockActual + ", Solicitado: " + cantidad
            );
        }
        this.stockActual -= cantidad;
    }

    /**
     * Aumenta el stock en una cantidad específica
     */
    public void aumentarStock(Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.stockActual += cantidad;
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

    // equals() y hashCode() - usar solo ID para evitar problemas de recursividad

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Producto producto = (Producto) o;

        return id != null ? id.equals(producto.id) : producto.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // toString() para debugging (evitar recursividad con categoría)

    @Override
    public String toString() {
        String categoriaNombre = (categoria != null) ? categoria.getNombre() : "null";
        return "Producto{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stockActual=" + stockActual +
                ", categoria=" + categoriaNombre +
                ", version=" + version +
                '}';
    }
}