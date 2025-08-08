package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductoResponse {
    @SerializedName("productos") // si así se llama en el JSON
    private ArrayList<ProductoDTO> listaProductos;

    public ArrayList<ProductoDTO> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<ProductoDTO> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
