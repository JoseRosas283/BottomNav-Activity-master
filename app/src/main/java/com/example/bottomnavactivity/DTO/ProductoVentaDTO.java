package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class ProductoVentaDTO {
    @SerializedName("mensaje")
    private String mensaje;

    // Para respuesta de UN solo producto (PR CODIGO DE BARRAS)
    @SerializedName("producto")  // ← PARA UNO
    private Producto producto;

    // Para respuesta de MÚLTIPLES productos (lista completa)
    @SerializedName("productos")  // ← PARA MUCHOS
    private List<Producto> productos;

    // Constructores
    public ProductoVentaDTO() {}

    // Getters y setters
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Producto getProducto() { return producto; }  // ← Para un producto
    public void setProducto(Producto producto) { this.producto = producto; }

    public List<Producto> getProductos() { return productos; }  // ← Para múltiples
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    // METODO PARA OBTENER EL PRODUCTO SIN IMPORTAR LA ESTRUCTURA
    public Producto getPrimerProducto() {
        // Si viene como objeto singular, devolverlo
        if (producto != null) return producto;

        // Si viene como array, devolver el primero
        if (productos != null && !productos.isEmpty()) return productos.get(0);

        return null;
    }

    // Clase interna para el producto individual
    public static class Producto {
        @SerializedName("productoId")
        private String productoId;

        @SerializedName("nombreProducto")
        private String nombreProducto;

        @SerializedName("codigoProducto")
        private String codigoProducto;

        @SerializedName("cantidad")
        private int cantidad;

        @SerializedName("precio")
        private BigDecimal precio;

        public Producto() {}

        public Producto(String productoId, String nombreProducto, String codigoProducto, int cantidad, BigDecimal precio) {
            this.productoId = productoId;
            this.nombreProducto = nombreProducto;
            this.codigoProducto = codigoProducto;
            this.cantidad = cantidad;
            this.precio = precio;
        }

        // Getters y setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public String getCodigoProducto() { return codigoProducto; }
        public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
    }
}
