package com.example.bottomnavactivity.Mappers;

import com.example.bottomnavactivity.DTO.ProductoVentaDTO;
import com.example.bottomnavactivity.Models.ProductoModel;

public class ProductoMapper {
    /**
     * Convierte una respuesta DTO a ProductoModel
     * Funciona tanto para respuestas de un producto como de lista de productos
     */
    public static ProductoModel fromResponse(ProductoVentaDTO response) {
        if (response == null) return null;

        // Obtener el primer producto disponible
        ProductoVentaDTO.Producto producto = response.getPrimerProducto();

        if (producto == null) return null;

        return new ProductoModel(
                producto.getProductoId(),
                producto.getNombreProducto(),
                producto.getCodigoProducto(),
                producto.getCantidad(),
                producto.getPrecio()
        );
    }
}
