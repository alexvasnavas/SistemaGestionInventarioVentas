package com.alexis.sprintboot.app.DTO;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class VentaRequestDTO {

    @Valid
    @NotEmpty(message = "La venta debe contener al menos un producto")
    private List<ItemVentaDTO> items;

    // Constructor vacío
    public VentaRequestDTO() {
        this.items = new ArrayList<>();
    }

    // Constructor con items
    public VentaRequestDTO(List<ItemVentaDTO> items) {
        this.items = items;
    }

    // Getters y Setters

    public List<ItemVentaDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaDTO> items) {
        if (items == null) {
            throw new IllegalArgumentException("La lista de items no puede ser nula");
        }
        this.items = items;
    }

    /**
     * Agrega un item a la lista
     */
    public void addItem(ItemVentaDTO item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    /**
     * Agrega un item con productoId y cantidad
     */
    public void addItem(Long productoId, Integer cantidad) {
        ItemVentaDTO item = new ItemVentaDTO();
        item.setProductoId(productoId);
        item.setCantidad(cantidad);
        addItem(item);
    }

    /**
     * Verifica si el DTO es válido
     */
    public boolean isValid() {
        return items != null && !items.isEmpty() &&
                items.stream().allMatch(ItemVentaDTO::isValid);
    }

    /**
     * Obtiene la cantidad total de productos en la venta
     */
    public int getCantidadTotalProductos() {
        if (items == null) return 0;
        return items.stream()
                .filter(item -> item.getCantidad() != null)
                .mapToInt(ItemVentaDTO::getCantidad)
                .sum();
    }

    // equals() y hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VentaRequestDTO that = (VentaRequestDTO) o;

        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    // toString()

    @Override
    public String toString() {
        return "VentaRequestDTO{" +
                "items=" + items +
                ", cantidadItems=" + (items != null ? items.size() : 0) +
                '}';
    }

    // Clase interna ItemVentaDTO
    public static class ItemVentaDTO {

        @NotNull(message = "El producto es requerido")
        private Long productoId;

        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        // Constructor vacío
        public ItemVentaDTO() {
        }

        // Constructor con parámetros
        public ItemVentaDTO(Long productoId, Integer cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        // Getters y Setters

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
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

        /**
         * Verifica si el item es válido
         */
        public boolean isValid() {
            return productoId != null && productoId > 0 &&
                    cantidad != null && cantidad > 0;
        }

        // equals() y hashCode()

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemVentaDTO that = (ItemVentaDTO) o;

            if (productoId != null ? !productoId.equals(that.productoId) : that.productoId != null)
                return false;
            return cantidad != null ? cantidad.equals(that.cantidad) : that.cantidad == null;
        }

        @Override
        public int hashCode() {
            int result = productoId != null ? productoId.hashCode() : 0;
            result = 31 * result + (cantidad != null ? cantidad.hashCode() : 0);
            return result;
        }

        // toString()

        @Override
        public String toString() {
            return "ItemVentaDTO{" +
                    "productoId=" + productoId +
                    ", cantidad=" + cantidad +
                    '}';
        }
    }
}