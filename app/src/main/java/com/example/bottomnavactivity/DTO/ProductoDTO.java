package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductoDTO {
    // 🔧 CORRECCIÓN: Poner las versiones en minúsculas primero (como las devuelve tu API)
    @SerializedName(value = "productoId", alternate = {"ProductoId", "PRODUCTOID", "id", "Id", "ID"})
    private String productoId;

    @SerializedName(value = "nombreProducto", alternate = {"NombreProducto", "NOMBREPRODUCTO", "nombre", "Nombre", "name", "Name"})
    private String nombreProducto;

    @SerializedName(value = "codigoProducto", alternate = {"CodigoProducto", "CODIGOPRODUCTO", "codigo", "Codigo", "code", "Code"})
    private String codigoProducto;

    @SerializedName(value = "cantidad", alternate = {"Cantidad", "CANTIDAD", "quantity", "Quantity"})
    private int cantidad;

    @SerializedName(value = "precio", alternate = {"Precio", "PRECIO", "price", "Price"})
    private double precio;

    // ======== CONSTRUCTORES ========
    public ProductoDTO(String productoId, String nombreProducto, String codigoProducto, int cantidad, double precio) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public ProductoDTO(String nombreProducto, String codigoProducto, int cantidad, double precio) {
        this.nombreProducto = nombreProducto;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    // Constructor vacío requerido por Gson
    public ProductoDTO() {}

    // ======== GETTERS Y SETTERS ========
    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "ProductoDTO{" +
                "productoId='" + productoId + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                '}';
    }

    // ======== CLASE INTERNA PARA RESPUESTA GENERAL ========
    public static class ProductoResponse {
        @SerializedName("productos")
        private ArrayList<ProductoDTO> listaProductos;

        public ArrayList<ProductoDTO> getListaProductos() {
            return listaProductos;
        }

        public void setListaProductos(ArrayList<ProductoDTO> listaProductos) {
            this.listaProductos = listaProductos;
        }
    }

    // ======== CLASE INTERNA PARA RESPUESTA DE UPDATE ========
    public static class ProductoUpdateResponse {
        // 🔧 CORRECCIÓN: También corregir aquí para que coincida con el backend
        @SerializedName(value = "mensaje", alternate = {"Mensaje", "MENSAJE"})
        private String mensaje;

        @SerializedName(value = "producto", alternate = {"Producto", "PRODUCTO"})
        private ProductoDTO producto;

        public String getMensaje() {
            return mensaje;
        }

        public ProductoDTO getProducto() {
            return producto;
        }
    }
}
