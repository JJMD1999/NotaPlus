package com.example.notaplus.actividades;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.notaplus.R;
import com.example.notaplus.adaptadores.AdaptadorNotas;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity principal. Desde aquí se visualizan las notas creadas
 *
 * @author Julio José Meijueiro Dacosta
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /**
     * RequestCode para <i>CrearNotaActivity</i>
     */
    private static final int ANADIR_NOTA = 1;

    private RecyclerView recyclerViewNotas;
    private List<Nota> listaNotas;
    private AdaptadorNotas adaptadorNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AbrirCrearNota();
        CargarRecycler();
        GetNotas();

        // SeleccionarTema. Más adelante será una opción en la aplicación
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Abre la Activity <i>CrearNotaActivity</i> para añadir una nueva nota.
     */
    private void AbrirCrearNota() {
        FloatingActionButton anadirNota = findViewById(R.id.añadirNota);

        anadirNota.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CrearNotaActivity.class), ANADIR_NOTA));
    }

    /**
     * Asigna el <i>RecyclerView</i>, la disposición de las notas y el adaptador
     */
    private void CargarRecycler() {
        recyclerViewNotas = findViewById(R.id.recyclerViewNotas);
        recyclerViewNotas.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        listaNotas = new ArrayList<>();
        adaptadorNotas = new AdaptadorNotas(listaNotas);
        recyclerViewNotas.setAdapter(adaptadorNotas);
    }

    /**
     * Devuelve las notas de la base de datos para mostralos en pantalla
     */
    private void GetNotas() {
        // Hilo secundario, ya que no se permiten operaciones de bases de datos en el hilo principal
        class TareaGetNotas extends AsyncTask<Void, Void, List<Nota>> {

            @Override
            protected List<Nota> doInBackground(Void... voids) {
                return BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().getAll();
            }

            @Override
            protected void onPostExecute(List<Nota> notas) {
                super.onPostExecute(notas);
                // Se insertan todas las notas de la base de datos si la lista está vacía (Si se ha iniciado la aplicación)
                if (listaNotas.size() == 0) {
                    listaNotas.addAll(notas);
                    adaptadorNotas.notifyDataSetChanged();

                }
                // Si no, se añade solamente la última nota creada al principio
                else {
                    listaNotas.add(0, notas.get(0));
                    adaptadorNotas.notifyItemInserted(0);
                }
                recyclerViewNotas.smoothScrollToPosition(0); // Volver al principio
            }
        }
        new TareaGetNotas().execute();
    }

    /**
     * Una vez se termine de crear una nota, se carga en el <i>MainActivity</i> al volver
     *
     * @param requestCode Código de solicitud
     * @param resultCode Código de resultado
     * @param data Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ANADIR_NOTA && resultCode == RESULT_OK) {
            GetNotas();
        }
    }
}