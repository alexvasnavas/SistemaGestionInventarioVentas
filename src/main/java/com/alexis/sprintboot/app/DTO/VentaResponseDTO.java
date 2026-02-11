package com.alexis.sprintboot.app.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaResponseDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaVenta;

    private BigDecimal total;
    private List<ItemVentaResponseDTO> items;

    // Constructor vacío
    public VentaResponseDTO() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // Constructor con parámetros
    public VentaResponseDTO(Long id, LocalDateTime fechaVenta, BigDecimal total,
                            List<ItemVentaResponseDTO> items) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.items = items != null ? items : new ArrayList<>();
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

    public List<ItemVentaResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaResponseDTO> items) {
        this.items = items;
    }

    /**
     * Agrega un item a la respuesta
     */
    public void addItem(ItemVentaResponseDTO item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    /**
     * Calcula el total si no está calculado
     */
    public BigDecimal calcularTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal suma = BigDecimal.ZERO;
        for (ItemVentaResponseDTO item : items) {
            if (item.getSubtotal() != null) {
                suma = suma.add(item.getSubtotal());
            }
        }
        this.total = suma;
        return suma;
    }

    /**
     * Obtiene la cantidad total de productos vendidos
     */
    public int getCantidadTotalProductos() {
        if (items == null) return 0;
        return items.stream()
                .filter(item -> item.getCantidad() != null)
                .mapToInt(ItemVentaResponseDTO::getCantidad)
                .sum();
    }

    /**
     * Obtiene la cantidad de productos únicos vendidos
     */
    public int getCantidadProductosUnicos() {
        if (items == null) return 0;
        return (int) items.stream()
                .map(ItemVentaResponseDTO::getProductoId)
                .distinct()
                .count();
    }

    // equals() y hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VentaResponseDTO that = (VentaResponseDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (fechaVenta != null ? !fechaVenta.equals(that.fechaVenta) : that.fechaVenta != null) return false;
        if (total != null ? !total.equals(that.total) : that.total != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fechaVenta != null ? fechaVenta.hashCode() : 0);
        result = 31 * result + (total != null ? total.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    // toString()

    @Override
    public String toString() {
        return "VentaResponseDTO{" +
                "id=" + id +
                ", fechaVenta=" + fechaVenta +
                ", total=" + total +
                ", items=" + items +
                '}';
    }

    // Clase interna ItemVentaResponseDTO
    public static class ItemVentaResponseDTO {
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        // Constructor vacío
        public ItemVentaResponseDTO() {
        }

        // Constructor con parámetros
        public ItemVentaResponseDTO(Long productoId, String productoNombre, Integer cantidad,
                                    BigDecimal precioUnitario, BigDecimal subtotal) {
            this.productoId = productoId;
            this.productoNombre = productoNombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }

        // Constructor simple
        public ItemVentaResponseDTO(Long productoId, String productoNombre, Integer cantidad,
                                    BigDecimal precioUnitario) {
            this.productoId = productoId;
            this.productoNombre = productoNombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }

        // Getters y Setters

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public String getProductoNombre() {
            return productoNombre;
        }

        public void setProductoNombre(String productoNombre) {
            this.productoNombre = productoNombre;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
            calcularSubtotal();
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
            calcularSubtotal();
        }

        public BigDecimal getSubtotal() {
            if (subtotal == null) {
                calcularSubtotal();
            }
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        /**
         * Calcula el subtotal automáticamente
         */
        private void calcularSubtotal() {
            if (precioUnitario != null && cantidad != null) {
                this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            }
        }

        // equals() y hashCode()

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemVentaResponseDTO that = (ItemVentaResponseDTO) o;

            if (productoId != null ? !productoId.equals(that.productoId) : that.productoId != null) return false;
            if (productoNombre != null ? !productoNombre.equals(that.productoNombre) : that.productoNombre != null) return false;
            if (cantidad != null ? !cantidad.equals(that.cantidad) : that.cantidad != null) return false;
            if (precioUnitario != null ? !precioUnitario.equals(that.precioUnitario) : that.precioUnitario != null) return false;
            return subtotal != null ? subtotal.equals(that.subtotal) : that.subtotal == null;
        }

        @Override
        public int hashCode() {
            int result = productoId != null ? productoId.hashCode() : 0;
            result = 31 * result + (productoNombre != null ? productoNombre.hashCode() : 0);
            result = 31 * result + (cantidad != null ? cantidad.hashCode() : 0);
            result = 31 * result + (precioUnitario != null ? precioUnitario.hashCode() : 0);
            result = 31 * result + (subtotal != null ? subtotal.hashCode() : 0);
            return result;
        }

        // toString()

        @Override
        public String toString() {
            return "ItemVentaResponseDTO{" +
                    "productoId=" + productoId +
                    ", productoNombre='" + productoNombre + '\'' +
                    ", cantidad=" + cantidad +
                    ", precioUnitario=" + precioUnitario +
                    ", subtotal=" + subtotal +
                    '}';
        }
    }
}