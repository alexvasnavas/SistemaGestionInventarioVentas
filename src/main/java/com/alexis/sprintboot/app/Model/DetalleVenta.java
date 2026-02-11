package com.alexis.sprintboot.app.Model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    // Constructor vacío (requerido por JPA)
    public Venta() {
        this.detalles = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // Constructor con fecha
    public Venta(LocalDateTime fechaVenta) {
        this();
        this.fechaVenta = fechaVenta;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    // Métodos de ayuda para relaciones bidireccionales

    /**
     * Agrega un detalle de venta a la venta actual
     */
    public void addDetalle(DetalleVenta detalle) {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        detalles.add(detalle);
        detalle.setVenta(this);
        calcularTotal();
    }

    /**
     * Agrega un detalle de venta con producto y cantidad
     */
    public void addDetalle(Producto producto, Integer cantidad) {
        if (producto == null || cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("Producto y cantidad deben ser válidos");
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(this);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());

        addDetalle(detalle);
    }

    /**
     * Elimina un detalle de venta
     */
    public void removeDetalle(DetalleVenta detalle) {
        if (detalles != null) {
            detalles.remove(detalle);
            detalle.setVenta(null);
            calcularTotal();
        }
    }

    /**
     * Calcula el total de la venta sumando todos los detalles
     */
    public void calcularTotal() {
        if (detalles == null || detalles.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }

        BigDecimal sumaTotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            if (detalle.getPrecioUnitario() != null && detalle.getCantidad() != null) {
                BigDecimal subtotal = detalle.getPrecioUnitario()
                        .multiply(new BigDecimal(detalle.getCantidad()));
                sumaTotal = sumaTotal.add(subtotal);
            }
        }
        this.total = sumaTotal;
    }

    /**
     * Inicializa la fecha de venta con la fecha y hora actual
     */
    @PrePersist
    public void prePersist() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
        if (total == null) {
            calcularTotal();
        }
    }

    // equals() y hashCode() - usar solo ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Venta venta = (Venta) o;

        return id != null ? id.equals(venta.id) : venta.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // toString() para debugging

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fechaVenta=" + fechaVenta +
                ", total=" + total +
                ", detallesCount=" + (detalles != null ? detalles.size() : 0) +
                '}';
    }
}