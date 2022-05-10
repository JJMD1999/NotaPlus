package com.example.notaplus.actividades;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;

import com.example.notaplus.R;
import com.example.notaplus.adaptadores.AdaptadorNotas;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.listener.ListenerNotas;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity principal. Desde aquí se visualizan las notas guardadas
 *
 * @author Julio José Meijueiro Dacosta
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements ListenerNotas {

    private static final int AÑADIR_NOTA = 1, ACTUALIZAR_NOTA = 2, MOSTRAR_NOTAS = 3;
    @SuppressLint("StaticFieldLeak")
    public static Context contexto;
    private DrawerLayout drawerLayout;
    private List<Nota> listaNotas;
    private RecyclerView recyclerViewNotas;
    private AdaptadorNotas adaptadorNotas;
    private int posicion;

    /**
     * Se ejecuta cuando se inicia la actividad.
     *
     * @param savedInstanceState Estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarPreferencias();

        // Asignar variables iniciales
        contexto = getApplicationContext();

        // Menú lateral (Navigation Drawer)
        menuDrawer();

        // Abrir "CrearNotaActivity"
        abrirCrearNota();

        // Carga las notas en pantalla
        cargarRecycler();
        getNotas(MOSTRAR_NOTAS);

    }

    /**
     * Abre la Activity <i>CrearNotaActivity</i> para añadir una nueva nota.
     */
    private void abrirCrearNota() {
        FloatingActionButton anadirNota = findViewById(R.id.añadirNota);

        anadirNota.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CrearNotaActivity.class), AÑADIR_NOTA));
    }

    /**
     * Asigna el <i>RecyclerView</i>, la disposición de las notas y el adaptador
     */
    private void cargarRecycler() {
        recyclerViewNotas = findViewById(R.id.recyclerViewNotas);
        recyclerViewNotas.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        listaNotas = new ArrayList<>();
        adaptadorNotas = new AdaptadorNotas(listaNotas, this);
        recyclerViewNotas.setAdapter(adaptadorNotas);
    }

    @Override
    public void onClickNota(Nota nota, int posicion) {
        this.posicion = posicion;
        Intent intent = new Intent(this, CrearNotaActivity.class);
        intent.putExtra("existe_nota", true);
        intent.putExtra("nota", nota);
        startActivityForResult(intent, ACTUALIZAR_NOTA);
    }

    /**
     * Devuelve las notas de la base de datos para mostrarlas en pantalla.
     * <p>
     * VERSION 3.0: Ahora se usa un código de solicitud para saber que orden realizar (AÑADIR, ACTUALIZAR, MOSTRAR).
     *
     * @param requestCode Código de solicitud
     */
    private void getNotas(int requestCode) {
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
                if (requestCode == MOSTRAR_NOTAS) {
                    listaNotas.addAll(notas);
                    adaptadorNotas.notifyDataSetChanged();

                } else if (requestCode == AÑADIR_NOTA) {
                    listaNotas.add(0, notas.get(0));
                    adaptadorNotas.notifyItemInserted(0);
                    recyclerViewNotas.smoothScrollToPosition(0);

                } else if (requestCode == ACTUALIZAR_NOTA) {
                    listaNotas.remove(posicion);
                    listaNotas.add(posicion, notas.get(posicion));
                    adaptadorNotas.notifyItemChanged(posicion);
                }
            }
        }
        new TareaGetNotas().execute();
    }

    private void menuDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.getMenu().getItem(0).setChecked(true);

        // Abrir el menú lateral al pulsar en la imagen
        findViewById(R.id.imagenMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        // Esto se ampliará para que según se clique, mostrar notas, archivo o papelera
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_notas:
                case R.id.menu_archivo:
                case R.id.menu_papelera:
                    item.setChecked(true);
                    break;
                case R.id.opciones:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
            }
            return true;
        });
    }

    /**
     * Método que se encarga de cargar las preferencias al inicio y mientras se ejecuta la aplicación
     */
    private void cargarPreferencias() {
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferencias.getBoolean(SettingsActivity.KEY_PREF_TEMA, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Listener que se encarga de manejar los cambios en las preferencias
        SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
            if (key.equals(SettingsActivity.KEY_PREF_TEMA)) {
                if (prefs.getBoolean(key, true)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        };
        preferencias.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Para cerrar el panel lateral al darle hacia atrás en vez de cerrar la aplicación.
     */
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    /**
     * Una vez se termine de crear una nota, se carga en el <i>MainActivity</i> al volver
     *
     * @param requestCode Código de solicitud
     * @param resultCode  Código de resultado
     * @param data        Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AÑADIR_NOTA && resultCode == RESULT_OK) {
            getNotas(AÑADIR_NOTA);
        } else if (requestCode == ACTUALIZAR_NOTA && resultCode == RESULT_OK) {
            if (data != null) {
                getNotas(ACTUALIZAR_NOTA);
            }
        }
    }
}