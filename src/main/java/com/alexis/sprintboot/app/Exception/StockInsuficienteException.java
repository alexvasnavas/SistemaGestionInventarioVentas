package com.alexis.sprintboot.app.Exception;

public class StockInsuficienteException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // Constructor por defecto
    public StockInsuficienteException() {
        super("Stock insuficiente para completar la operación");
    }

    // Constructor con mensaje personalizado
    public StockInsuficienteException(String message) {
        super(message);
    }

    // Constructor con mensaje y causa
    public StockInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor con producto y cantidad solicitada
    public StockInsuficienteException(String productoSku, String productoNombre,
                                      Integer stockActual, Integer cantidadSolicitada) {
        super(String.format(
                "Stock insuficiente para el producto [SKU: %s, Nombre: %s]. " +
                        "Stock disponible: %d, Cantidad solicitada: %d",
                productoSku, productoNombre, stockActual, cantidadSolicitada
        ));
    }

    // Constructor con ID de producto
    public StockInsuficienteException(Long productoId, Integer stockActual,
                                      Integer cantidadSolicitada) {
        super(String.format(
                "Stock insuficiente para el producto con ID: %d. " +
                        "Stock disponible: %d, Cantidad solicitada: %d",
                productoId, stockActual, cantidadSolicitada
        ));
    }

    // Método estático para crear excepción con formato específico
    public static StockInsuficienteException crearConDetalles(
            String productoSku,
            String productoNombre,
            Integer stockActual,
            Integer cantidadSolicitada) {
        return new StockInsuficienteException(
                productoSku,
                productoNombre,
                stockActual,
                cantidadSolicitada
        );
    }
}
