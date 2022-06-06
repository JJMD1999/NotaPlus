package com.example.notaplus.adaptadores;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notaplus.R;
import com.example.notaplus.actividades.MainActivity;
import com.example.notaplus.actividades.SettingsActivity;
import com.example.notaplus.listener.ListenerNotas;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para las notas.
 */
public class AdaptadorNotas extends RecyclerView.Adapter<AdaptadorNotas.ViewHolder_Nota> {

    private List<Nota> notas;
    private ListenerNotas listenerNotas;
    private List<Nota> copiaNotas;

    public AdaptadorNotas(List<Nota> notas, ListenerNotas listenerNotas) {
        this.notas = notas;
        this.listenerNotas = listenerNotas;
        copiaNotas = notas;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
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
     * Filtro de búsqueda que permite visualizar las notas que contengan el título o el contenido indicado en el <i>SearchView</i>.
     *
     * @param busqueda Filtro de búsqueda.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void buscarNotas(String busqueda) {
        if (busqueda.trim().isEmpty()) {
            notas = copiaNotas;
        } else {
            ArrayList<Nota> listaTemporal = new ArrayList<>();
            for (Nota nota : copiaNotas) {
                if (nota.getTitulo().toLowerCase().contains(busqueda.toLowerCase())
                        || nota.getTexto().toLowerCase().contains(busqueda.toLowerCase())
                        || nota.getEtiqueta() != null && nota.getEtiqueta().toLowerCase().contains(busqueda.toLowerCase())) {
                    listaTemporal.add(nota);
                }
            }
            notas = listaTemporal;
        }
        this.notifyDataSetChanged();
    }

    /**
     * ViewHolder para las notas.
     */
    static class ViewHolder_Nota extends RecyclerView.ViewHolder {

        TextView titulo, fecha, textoNota;
        LinearLayout plantilla_nota;
        ImageView imagen;

        public ViewHolder_Nota(@NonNull View itemView) {
            super(itemView);
            plantilla_nota = itemView.findViewById(R.id.plantilla_nota);
            titulo = itemView.findViewById(R.id.titulo);
            fecha = itemView.findViewById(R.id.fecha);
            textoNota = itemView.findViewById(R.id.textoNota);
            imagen = itemView.findViewById(R.id.imagen);
            asignarFuentes();
        }

        void setNota(Nota nota) {
            titulo.setText(nota.getTitulo());
            fecha.setText(nota.getFecha());
            String texto;
            if (nota.getTexto() == null || nota.getTexto().isEmpty()) {
                textoNota.setVisibility(View.GONE);
            } else {
                textoNota.setVisibility(View.VISIBLE);
                texto = nota.getTexto();
                if (texto.length() > 100) {
                    texto = nota.getTexto().substring(0, 100) + " ...";
                }
                textoNota.setText(texto);
            }

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

        /**
         * Asigna un tipo de fuente según lo especificado en las opciones.
         */
        private void asignarFuentes() {
            SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(MainActivity.contexto);
            String fuente = preferencias.getString(SettingsActivity.KEY_PREF_FUENTE, "roboto");

            switch (fuente) {
                case "roboto":
                    titulo.setTypeface(MainActivity.fuentes.get("roboto_bold"));
                    fecha.setTypeface(MainActivity.fuentes.get("roboto_medium"));
                    textoNota.setTypeface(MainActivity.fuentes.get("roboto_regular"));
                    break;
                case "alegreya":
                    titulo.setTypeface(MainActivity.fuentes.get("alegreya_sans_bold"));
                    fecha.setTypeface(MainActivity.fuentes.get("alegreya_sans_medium"));
                    textoNota.setTypeface(MainActivity.fuentes.get("alegreya_sans_regular"));
                    break;
                case "prompt":
                    titulo.setTypeface(MainActivity.fuentes.get("prompt_bold"));
                    fecha.setTypeface(MainActivity.fuentes.get("prompt_medium"));
                    textoNota.setTypeface(MainActivity.fuentes.get("alegreya_sans_regular"));
                    break;
            }
        }
    }
}
