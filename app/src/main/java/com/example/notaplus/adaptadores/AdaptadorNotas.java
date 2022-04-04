package com.example.notaplus.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notaplus.R;
import com.example.notaplus.tabla.Nota;

import java.util.List;

/**
 * Adaptador para las notas
 */
public class AdaptadorNotas extends RecyclerView.Adapter<AdaptadorNotas.ViewHolder_Nota>{

    private List<Nota> notas;

    public AdaptadorNotas(List<Nota> notas) {
        this.notas = notas;
    }

    @NonNull
    @Override
    public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_Nota(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.plantilla_nota, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Nota holder, int posicion) {
        holder.setNota(notas.get(posicion));
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    @Override
    public int getItemViewType(int posicion) {
        return posicion;
    }

    /**
     * ViewHolder para las notas
     */
    static class ViewHolder_Nota extends RecyclerView.ViewHolder {

        TextView titulo, fecha;

        public ViewHolder_Nota(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            fecha = itemView.findViewById(R.id.fecha);
        }

        void setNota(Nota nota) {
            titulo.setText(nota.getTitulo());
            fecha.setText(nota.getFecha());
        }
    }
}