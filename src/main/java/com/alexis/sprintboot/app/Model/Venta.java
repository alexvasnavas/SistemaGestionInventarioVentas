package com.alexis.sprintboot.app.Model;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // Constructor vacío (requerido por JPA)
    public DetalleVenta() {
    }

    // Constructor con parámetros
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Constructor con producto y cantidad (precio se toma del producto)
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad != null && cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        this.precioUnitario = precioUnitario;
    }

    // Métodos de negocio

    /**
     * Calcula el subtotal de este detalle (precio * cantidad)
     */
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }

    /**
     * Verifica si el detalle es válido
     */
    public boolean isValid() {
        return venta != null &&
                producto != null &&
                cantidad != null && cantidad > 0 &&
                precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Actualiza el precio unitario desde el producto actual
     */
    public void actualizarPrecioDesdeProducto() {
        if (producto != null && producto.getPrecio() != null) {
            this.precioUnitario = producto.getPrecio();
        }
    }

    // equals() y hashCode() - usar solo ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetalleVenta that = (DetalleVenta) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // toString() para debugging (evitar recursividad)

    @Override
    public String toString() {
        String productoNombre = (producto != null) ? producto.getNombre() : "null";
        String ventaId = (venta != null) ? String.valueOf(venta.getId()) : "null";

        return "DetalleVenta{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", producto='" + productoNombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}