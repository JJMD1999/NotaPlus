package com.example.notaplus.actividades;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.notaplus.R;
import com.example.notaplus.adaptadores.AdaptadorNotas;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity principal. Desde aquí se visualizan las notas guardadas
 *
 * @author Julio José Meijueiro Dacosta
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final int ANADIR_NOTA = 1;
    @SuppressLint("StaticFieldLeak")
    public static Context contexto;
    SharedPreferences sharedPreferences = null;
    private List<Nota> listaNotas;
    private RecyclerView recyclerViewNotas;
    private AdaptadorNotas adaptadorNotas;
    private SwitchCompat switchModo;
    private ImageView iconoModo;

    /**
     * Se ejecuta cuando se inicia la actividad.
     *
     * @param savedInstanceState Estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = getApplicationContext();

        // Elegir temas
        CambiarModo();

        // Abrir "CrearNotaActivity"
        AbrirCrearNota();

        // Carga las notas en pantalla
        CargarRecycler();
        GetNotas();
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
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        listaNotas = new ArrayList<>();
        adaptadorNotas = new AdaptadorNotas(listaNotas);
        recyclerViewNotas.setAdapter(adaptadorNotas);
    }

    /**
     * Devuelve las notas de la base de datos para mostrarlas en pantalla
     */
    private void GetNotas() {
        // Hilo secundario, ya que no se permiten operaciones de bases de datos en el hilo principal
        class TareaGetNotas extends AsyncTask<Void, Void, List<Nota>> {

            @Override
            protected List<Nota> doInBackground(Void... voids) {
                return BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().getAll();
            }

            @SuppressLint("NotifyDataSetChanged")
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
     * Detecta el tema del dispositivo y permite cambiarlo mediante un <i>switch</i>.
     *
     * Los temas alternan entre <b>"Claro"</b> y <b>"Oscuro"</b>.
     */
    private void CambiarModo() {
        switchModo = findViewById(R.id.cambiarModo);
        iconoModo = findViewById(R.id.iconoModo);

        sharedPreferences = getSharedPreferences("night", 0);
        boolean modoOscuro = sharedPreferences.getBoolean("modo_oscuro", true);

        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchModo.setChecked(true);
            iconoModo.setImageResource(R.drawable.ic_oscuro);
        }

        switchModo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                switchModo.setChecked(true);
                iconoModo.setImageResource(R.drawable.ic_oscuro);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modo_oscuro", true);
                editor.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                switchModo.setChecked(false);
                iconoModo.setImageResource(R.drawable.ic_claro);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("modo_oscuro", false);
                editor.apply();
            }
        });
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