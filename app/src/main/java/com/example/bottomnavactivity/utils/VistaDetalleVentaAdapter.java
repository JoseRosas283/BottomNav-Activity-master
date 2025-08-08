package com.example.bottomnavactivity.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnavactivity.DTO.VistaDetalleVentaDTO;
import com.example.bottomnavactivity.R;

import java.util.List;

public class VistaDetalleVentaAdapter extends RecyclerView.Adapter<VistaDetalleVentaAdapter.ViewHolder> {
    private List<VistaDetalleVentaDTO> ventas;

    public VistaDetalleVentaAdapter(List<VistaDetalleVentaDTO> ventas) {
        this.ventas = ventas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VistaDetalleVentaDTO venta = ventas.get(position);

        // Formatear y asignar los datos con el nuevo diseño
        holder.tvFecha.setText("Fecha: " + formatearFecha(venta.getFechaVenta()));
        holder.tvVentaId.setText(venta.getVentaId());
        holder.tvCantidad.setText(venta.getCantidad() + " unidades");
        holder.tvProducto.setText(venta.getProducto());
        holder.tvPrecio.setText("$" + String.format("%.2f", venta.getPrecio()));
        holder.tvTotal.setText("$" + String.format("%.2f", venta.getTotal()));
        holder.tvEstado.setText(venta.getEstado());
    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }

    // Método auxiliar para formatear la fecha
    private String formatearFecha(String fechaISO) {
        try {
            // Si la fecha viene en formato ISO como "2024-12-07T10:30:00Z"
            // La puedes formatear como quieras. Aquí un ejemplo simple:
            if (fechaISO != null && fechaISO.contains("T")) {
                String[] partes = fechaISO.split("T")[0].split("-");
                if (partes.length >= 3) {
                    // Convertir de YYYY-MM-DD a DD MMM YYYY
                    String year = partes[0];
                    String month = partes[1];
                    String day = partes[2];

                    String[] meses = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

                    int monthInt = Integer.parseInt(month);
                    return day + " " + meses[monthInt] + " " + year;
                }
            }
            return fechaISO; // Devolver original si no se puede formatear
        } catch (Exception e) {
            return fechaISO; // Devolver original en caso de error
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
