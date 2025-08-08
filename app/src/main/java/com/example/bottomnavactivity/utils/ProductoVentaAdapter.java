package com.example.bottomnavactivity.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnavactivity.Models.ProductoModel;
import com.example.bottomnavactivity.R;

import java.math.BigDecimal;
import java.util.List;

public class ProductoVentaAdapter extends RecyclerView.Adapter<ProductoVentaAdapter.ProductoViewHolder> {
    private List<ProductoModel> productos;
    private Runnable onCambioCallback;

    public ProductoVentaAdapter(List<ProductoModel> productos) {
        this.productos = productos;
    }

    public void setOnCambioCallback(Runnable callback) {
        this.onCambioCallback = callback;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venta, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoModel producto = productos.get(position);

        holder.tvNombre.setText(producto.getNombreProducto());
        holder.tvPrecio.setText("$" + producto.getPrecio());

        // CRUCIAL: Limpiar TextWatcher anterior ANTES de establecer el texto
        holder.etCantidad.removeTextChangedListener((TextWatcher) holder.etCantidad.getTag());
        holder.etCantidad.setText(String.valueOf(producto.getCantidad()));

        // Configurar botón más
        holder.btnMas.setOnClickListener(v -> {
            int nuevaCantidad = producto.getCantidad() + 1;
            producto.setCantidad(nuevaCantidad);
            // Actualizar sin disparar TextWatcher
            holder.etCantidad.removeTextChangedListener((TextWatcher) holder.etCantidad.getTag());
            holder.etCantidad.setText(String.valueOf(nuevaCantidad));
            setupTextWatcher(holder, producto);
            ejecutarCallback();
        });

        // Configurar botón menos
        holder.btnMenos.setOnClickListener(v -> {
            int nuevaCantidad = producto.getCantidad() - 1;
            if (nuevaCantidad > 0) {
                producto.setCantidad(nuevaCantidad);
                // Actualizar sin disparar TextWatcher
                holder.etCantidad.removeTextChangedListener((TextWatcher) holder.etCantidad.getTag());
                holder.etCantidad.setText(String.valueOf(nuevaCantidad));
                setupTextWatcher(holder, producto);
                ejecutarCallback();
            }
        });

        // Configurar botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            int posicionActual = holder.getAdapterPosition();
            if (posicionActual != RecyclerView.NO_POSITION) {
                eliminarProducto(posicionActual);
            }
        });
        // Configurar TextWatcher
        setupTextWatcher(holder, producto);
    }

    private void setupTextWatcher(ProductoViewHolder holder, ProductoModel producto) {
        // Crear nuevo TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                try {
                    String text = s.toString().trim();
                    if (!text.isEmpty()) {
                        int cantidad = Integer.parseInt(text);
                        if (cantidad > 0 && cantidad != producto.getCantidad()) {
                            producto.setCantidad(cantidad);
                            ejecutarCallback();
                        } else if (cantidad <= 0) {
                            // Si es 0 o negativo, establecer en 1
                            isUpdating = true;
                            holder.etCantidad.setText("1");
                            producto.setCantidad(1);
                            isUpdating = false;
                            ejecutarCallback();
                        }
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número válido, restaurar el valor anterior
                    isUpdating = true;
                    holder.etCantidad.setText(String.valueOf(producto.getCantidad()));
                    isUpdating = false;
                }
            }
        };

        // Guardar referencia del TextWatcher en el tag y agregarlo
        holder.etCantidad.setTag(textWatcher);
        holder.etCantidad.addTextChangedListener(textWatcher);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < productos.size(); i++) {
            ProductoModel producto = productos.get(i);
            if (producto.getPrecio() != null && producto.getCantidad() > 0) {
                BigDecimal subtotal = producto.getPrecio()
                        .multiply(BigDecimal.valueOf(producto.getCantidad()));
                total = total.add(subtotal);
            }
        }

        return total;
    }

    private void ejecutarCallback() {
        if (onCambioCallback != null) {
            onCambioCallback.run();
        }
    }

    // LOGICA PARA AGREGAR PRODUCTO
    public void agregarProducto(ProductoModel nuevo) {
        if (nuevo == null) return;

        // AQUI LA CANTIDAD SIEMPRE SE ESTABLECE EN 1 (PORQUE LA TRAE DE LA TABLA PRODUCTOS Y PUEDE SER QUE EL PRODUCTO GEL TIENE 10 EN CANTIDAD PERO LO QUE NECESITAMOS ES
        // QUE CUANDO SE AGREGA UN PRODUCTO SE ESTABLESCA EN 1 YA QUE SE ESCANEO UNA VEZ )
        nuevo.setCantidad(1);

        // SI NO HAY CODIGO AGREGAR DIRECTO YA SEA PORQUE EL USUARIO PUSO LA CANTIDAD O LE CAMBIO CON LOS BOTONES
        if (nuevo.getCodigoProducto() == null || nuevo.getCodigoProducto().trim().isEmpty()) {
            productos.add(nuevo);
            notifyItemInserted(productos.size() - 1);
            ejecutarCallback();
            return;
        }

        // BUSCAR SI EL PRODUCTO YA EXISTE POR CODIGO
        for (int i = 0; i < productos.size(); i++) {
            ProductoModel existente = productos.get(i);

            if (existente.getCodigoProducto() != null &&
                    existente.getCodigoProducto().equals(nuevo.getCodigoProducto())) {

                // SI YA EXISTE SOLO SUMALE 1
                existente.setCantidad(existente.getCantidad() + 1);
                notifyItemChanged(i);
                ejecutarCallback();
                return;
            }
        }

        // SI NO EXISTE AGREGA UNO NUEVO
        productos.add(nuevo);
        notifyItemInserted(productos.size() - 1);
        ejecutarCallback();
    }

    // METODO PARA ELIMINAR PRODUCTO
    public void eliminarProducto(int position) {
        if (position >= 0 && position < productos.size()) {

            productos.remove(position);
            notifyItemRemoved(position);

            // ACTUALIZAR POSICIONES
            if (position < productos.size()) {
                notifyItemRangeChanged(position, productos.size() - position);
            }

            ejecutarCallback();
        }
    }

    //METODO PARA VERIFICAR SI LA LISTA ESTA VACIA
    public boolean estaVacia() {
        return productos.isEmpty();
    }

    public int getCantidadProducto(String productoId) {
        for (ProductoModel producto : productos) {
            if (producto.getProductoId() != null && producto.getProductoId().equals(productoId)) {
                return producto.getCantidad();
            }
        }
        return 1; // Valor por defecto si no se encuentra el producto
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        EditText etCantidad;
        ImageButton btnMas, btnMenos;
        ImageView btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName);
            tvPrecio = itemView.findViewById(R.id.tvProductPrice);
            etCantidad = itemView.findViewById(R.id.etQuantity);
            btnMas = itemView.findViewById(R.id.btnPlus);
            btnMenos = itemView.findViewById(R.id.btnMinus);
            btnEliminar = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}
