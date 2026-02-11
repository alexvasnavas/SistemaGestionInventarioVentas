package com.alexis.sprintboot.app.Service;

@Service
@Transactional
public class VentaService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    public VentaResponseDTO procesarVenta(VentaRequestDTO ventaRequest) {
        Venta venta = new Venta();
        venta.setFechaVenta(LocalDateTime.now());
        venta.setDetalles(new ArrayList<>());

        BigDecimal totalVenta = BigDecimal.ZERO;

        // Validar stock y preparar detalles
        for (VentaRequestDTO.ItemVentaDTO item : ventaRequest.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

            // Verificar stock con bloqueo optimista
            if (producto.getStockActual() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: " + producto.getNombre()
                );
            }

            // Crear detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setVenta(venta);

            venta.getDetalles().add(detalle);

            // Calcular subtotal
            BigDecimal subtotal = producto.getPrecio()
                    .multiply(new BigDecimal(item.getCantidad()));
            totalVenta = totalVenta.add(subtotal);

            // Reducir stock (se actualizará automáticamente con @Version)
            producto.setStockActual(producto.getStockActual() - item.getCantidad());
            productoRepository.save(producto);
        }

        venta.setTotal(totalVenta);
        ventaRepository.save(venta);

        return convertirAVentaResponseDTO(venta);
    }
}
