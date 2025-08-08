package com.example.bottomnavactivity.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnavactivity.DTO.ProductoDTO;
import com.example.bottomnavactivity.R;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    Context context;
    ArrayList<ProductoDTO> listaProductos;
    public OnItemClicked onClick = null;

    public ProductoAdapter(Context context, ArrayList<ProductoDTO> listaProductos) {
        this.context = context;
        this.listaProductos = (listaProductos != null) ? listaProductos : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoDTO producto = listaProductos.get(position);

        holder.tvNombre.setText(producto.getNombreProducto());
        holder.tvCantidad.setText("Cantidad: " + producto.getCantidad());
        holder.tvPrecio.setText("Precio: $" + producto.getPrecio());

        holder.btnEditar.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.editarProducto(producto);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.eliminarProducto(producto.getProductoId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre,  tvCantidad, tvPrecio;
        ImageButton btnEditar, btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    public interface OnItemClicked {
        void editarProducto(ProductoDTO producto);
        void eliminarProducto(String productoId);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
    public void eliminarProductoDeLista(String productoId) {
        for (int i = 0; i < listaProductos.size(); i++) {
            if (listaProductos.get(i).getProductoId().equals(productoId)) {
                listaProductos.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

}
