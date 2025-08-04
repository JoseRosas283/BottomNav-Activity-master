package com.example.bottomnavactivity.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnavactivity.DTO.UsuarioLoginDTO;
import com.example.bottomnavactivity.R;

import java.util.ArrayList;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    Context context;
    ArrayList<UsuarioLoginDTO> listaUsuarios;

    public OnItemClicked onClick = null;

    public UsuarioAdapter(Context context, ArrayList<UsuarioLoginDTO> listaUsuarios) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UsuarioAdapter.UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_usuario, parent, false);
        return new UsuarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.UsuarioViewHolder holder, int position) {
        UsuarioLoginDTO usuario = listaUsuarios.get(position);

        holder.tvNombre.setText(usuario.getUsuario());
        holder.tvCorreo.setText(usuario.getCorreo());
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvCorreo;
        ImageButton btnEditar;
        ImageButton btnEliminar;


        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

        }
    }

    public interface OnItemClicked {
        void editarUsuario(UsuarioLoginDTO usuario);
        void eliminarUsuario(String usuarioId);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
