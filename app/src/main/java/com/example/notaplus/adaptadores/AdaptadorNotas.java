package com.example.notaplus.adaptadores;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notaplus.R;
import com.example.notaplus.actividades.MainActivity;
import com.example.notaplus.listener.ListenerNotas;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Adaptador para las notas
 */
public class AdaptadorNotas extends RecyclerView.Adapter<AdaptadorNotas.ViewHolder_Nota>{

    private List<Nota> notas;
    private ListenerNotas listenerNotas;

    public AdaptadorNotas(List<Nota> notas, ListenerNotas listenerNotas) {
        this.notas = notas;
        this.listenerNotas = listenerNotas;
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
        holder.plantilla_nota.setOnClickListener(v -> listenerNotas.onClickNota(
                notas.get(holder.getAdapterPosition()),
                holder.getAdapterPosition()));
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
        LinearLayout plantilla_nota;
        ShapeableImageView imagen;

        public ViewHolder_Nota(@NonNull View itemView) {
            super(itemView);
            plantilla_nota = itemView.findViewById(R.id.plantilla_nota);
            titulo = itemView.findViewById(R.id.titulo);
            fecha = itemView.findViewById(R.id.fecha);
            imagen = itemView.findViewById(R.id.imagen);
        }

        void setNota(Nota nota) {
            titulo.setText(nota.getTitulo());
            fecha.setText(nota.getFecha());

            GradientDrawable gradientDrawable = (GradientDrawable) plantilla_nota.getBackground();
            if (nota.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(nota.getColor()));
            } else {
                MainActivity.contexto.getApplicationContext().getResources()
                        .getColor(R.color.fondo_nota, MainActivity.contexto.getApplicationContext().getTheme());
            }
            if (nota.getImagen() != null) {
                imagen.setImageBitmap(BitmapFactory.decodeFile(nota.getImagen()));
                imagen.setVisibility(View.VISIBLE);
            } else {
                imagen.setVisibility(View.GONE);
            }
        }
    }
}
