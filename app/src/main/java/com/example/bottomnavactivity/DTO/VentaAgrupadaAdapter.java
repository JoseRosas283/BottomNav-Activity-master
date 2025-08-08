package com.example.bottomnavactivity.DTO;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnavactivity.R;

import java.util.List;

public class VentaAgrupadaAdapter extends RecyclerView.Adapter<VentaAgrupadaAdapter.ViewHolder> {

    private List<VentaAgrupadaDTO> ventasAgrupadas;

    public VentaAgrupadaAdapter(List<VentaAgrupadaDTO> ventasAgrupadas) {
        this.ventasAgrupadas = ventasAgrupadas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VentaAgrupadaDTO venta = ventasAgrupadas.get(position);

        // Información básica de la venta
        holder.tvFecha.setText("Fecha: " + formatearFecha(venta.getFechaVenta()));
        holder.tvVentaId.setText(venta.getVentaId());
        holder.tvTotal.setText("$" + String.format("%.2f", venta.getTotal()));

        // ✅ Crear texto con productos separados por saltos de línea
        StringBuilder productosTexto = new StringBuilder();
        StringBuilder preciosTexto = new StringBuilder();
        int totalCantidadProductos = 0;

        for (int i = 0; i < venta.getProductos().size(); i++) {
            VistaDetalleVentaDTO producto = venta.getProductos().get(i);

            // Separar con salto de línea en lugar de coma
            if (i > 0) {
                productosTexto.append("\n");
                preciosTexto.append("\n");
            }

            // Producto con cantidad
            productosTexto.append(producto.getProducto());
            if (producto.getCantidad() > 1) {
                productosTexto.append(" (x").append(producto.getCantidad()).append(")");
            }

            // Precio individual
            preciosTexto.append("$").append(String.format("%.2f", producto.getPrecio()));

            totalCantidadProductos += producto.getCantidad();
        }

        // Mostrar productos separados por líneas
        holder.tvProducto.setText(productosTexto.toString());
        holder.tvPrecio.setText(preciosTexto.toString());

        // Mostrar total de unidades
        holder.tvCantidad.setText(totalCantidadProductos + " unidades");

        // Estado del primer producto
        if (venta.getProductos().size() > 0) {
            holder.tvEstado.setText(venta.getProductos().get(0).getEstado());
        }
    }

    @Override
    public int getItemCount() {
        return ventasAgrupadas.size();
    }

    // Método auxiliar para formatear la fecha
    private String formatearFecha(String fechaISO) {
        try {
            if (fechaISO != null && fechaISO.contains("T")) {
                String[] partes = fechaISO.split("T")[0].split("-");
                if (partes.length >= 3) {
                    String year = partes[0];
                    String month = partes[1];
                    String day = partes[2];

                    String[] meses = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

                    int monthInt = Integer.parseInt(month);
                    return day + " " + meses[monthInt] + " " + year;
                }
            }
            return fechaISO;
        } catch (Exception e) {
            return fechaISO;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvVentaId, tvProducto, tvCantidad, tvPrecio, tvTotal, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvVentaId = itemView.findViewById(R.id.tvVentaId);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
