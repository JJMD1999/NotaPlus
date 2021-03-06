package com.example.notaplus.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notaplus.receiver.Notificacion;
import com.example.notaplus.R;
import com.example.notaplus.adaptadores.AdaptadorNotas;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.listener.ListenerNotas;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Activity principal. Desde aqu?? se visualizan las notas guardadas.
 *
 * @author Julio Jos?? Meijueiro Dacosta
 * @version 6.0
 */
public class MainActivity extends AppCompatActivity implements ListenerNotas {

    /**
     * C??digo de solicitud para cada acci??n.
     */
    private static final int A??ADIR_NOTA = 1, ACTUALIZAR_NOTA = 2, MOSTRAR_NOTAS = 3, MOVER_NOTA = 4;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 5, SELECCIONAR_IMAGEN = 6;
    private static final int REQUEST_CODE_RECORD_AUDIO = 7, VOZ_A_TEXTO = 8;
    // Atributos
    @SuppressLint("StaticFieldLeak")
    public static Context contexto;
    public static Map<String, Typeface> fuentes;
    public AdaptadorNotas adaptadorNotas;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Nota> listaNotas = new ArrayList<>();
    private RecyclerView recyclerViewNotas;
    private SearchView barraDeBusqueda;
    private SharedPreferences.OnSharedPreferenceChangeListener listenerPrefs;
    private FloatingActionButton a??adirNota;
    private LinearLayout barraDeAcciones;
    private AlertDialog dialogoA??adirEnlace, dialogoRecordatorio;
    private Calendar calendario;
    private int posicion;
    private TextView titulo;

    /**
     * Devuelve el tiempo actual en un formato espec??fico
     *
     * @return El tiempo formateado
     */
    public static String tiempoActual(boolean fecha_hora) {
        if (fecha_hora) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(Calendar.getInstance().getTime());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Se ejecuta cuando se inicia la actividad.
     *
     * @param savedInstanceState Estado de la instancia guardada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recogerFuentes();
        cargarPreferencias();
        crearCanalDeNotificacion();

        // Asignar variables iniciales
        asignarVariables();

        // Men?? lateral (Navigation Drawer)
        menuDrawer();

        // Abrir "CrearNotaActivity"
        abrirCrearNota();

        // Carga las notas en pantalla
        cargarRecycler();
        getNotas(MOSTRAR_NOTAS, false, 0);

        // Funciones de los accesos r??pidos
        accesosDirectos();

        // B??squeda de notas
        busquedaNotas();

    }

    /**
     * Recoge en un Map (lista clave-valor) los tipos de fuente disponibles.
     */
    private void recogerFuentes() {
        fuentes = new HashMap<>();
        fuentes.put("roboto_bold", getResources().getFont(R.font.roboto_bold));
        fuentes.put("roboto_medium", getResources().getFont(R.font.roboto_medium));
        fuentes.put("roboto_regular", getResources().getFont(R.font.roboto_regular));
        fuentes.put("alegreya_sans_bold", getResources().getFont(R.font.alegreya_sans_bold));
        fuentes.put("alegreya_sans_medium", getResources().getFont(R.font.alegreya_sans_medium));
        fuentes.put("alegreya_sans_regular", getResources().getFont(R.font.alegreya_sans_regular));
        fuentes.put("prompt_bold", getResources().getFont(R.font.prompt_bold));
        fuentes.put("prompt_medium", getResources().getFont(R.font.prompt_medium));
        fuentes.put("prompt_regular", getResources().getFont(R.font.prompt_regular));
    }

    /**
     * Asignar variables iniciales
     */
    private void asignarVariables() {
        contexto = getApplicationContext();
        titulo = findViewById(R.id.tituloAplicacion);
        barraDeBusqueda = findViewById(R.id.barraDeBusqueda);
        a??adirNota = findViewById(R.id.a??adirNota);
        barraDeAcciones = findViewById(R.id.barraDeAcciones);
        calendario = Calendar.getInstance();
    }

    /**
     * Permite filtrar y mostrar las notas seg??n el t??tulo, contenido o etiqueta.
     */
    private void busquedaNotas() {
        SearchView barraBusqueda = findViewById(R.id.barraDeBusqueda);
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (listaNotas.size() != 0) {
                    adaptadorNotas.buscarNotas(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                if (listaNotas.size() != 0) {
                    adaptadorNotas.buscarNotas(texto);
                }
                return false;
            }
        });

    }

    /**
     * Abre la Activity <i>CrearNotaActivity</i> para a??adir una nueva nota.
     */
    private void abrirCrearNota() {
        FloatingActionButton anadirNota = findViewById(R.id.a??adirNota);

        anadirNota.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CrearNotaActivity.class), A??ADIR_NOTA));
    }

    /**
     * Asigna el <i>RecyclerView</i>, la disposici??n de las notas y el adaptador.
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
     * Devuelve las notas de la base de datos y se gestiona la lista y eladaptador de las notas seg??n acci??n.
     *
     * @param requestCode  C??digo de solicitud.
     * @param nota_borrada Booleano de comprobaci??n para eliminar o actualizar nota.
     * @param accion       Realiza una consulta a la base de datos seg??n el Integer.
     */
    private void getNotas(int requestCode, boolean nota_borrada, int accion) {
        // Hilo secundario, ya que no se permiten operaciones de bases de datos en el hilo principal
        @SuppressLint("StaticFieldLeak")
        class TareaGetNotas extends AsyncTask<Void, Void, List<Nota>> {

            @Override
            protected List<Nota> doInBackground(Void... voids) {
                switch (accion) {
                    case 0:
                        return BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().getNotas();
                    case 1:
                        return BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().getArchivadas();
                    case 2:
                        return BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().getPapelera();
                    default:
                        return null;
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Nota> notas) {
                super.onPostExecute(notas);

                switch (requestCode) {
                    case MOSTRAR_NOTAS:
                        listaNotas.clear();
                        // Para cambiar el color predeterminado al cambiar el tema de la aplicaci??n
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            for (Nota nota : notas) {
                                if (nota.getColor().endsWith("D8D8D8")) nota.setColor("#373737");
                            }
                        } else {
                            for (Nota nota : notas) {
                                if (nota.getColor().endsWith("373737")) nota.setColor("#D8D8D8");
                            }
                        }
                        listaNotas.addAll(notas);
                        adaptadorNotas.notifyDataSetChanged();
                        break;

                    case A??ADIR_NOTA:
                        listaNotas.add(0, notas.get(0));
                        adaptadorNotas.notifyItemInserted(0);
                        recyclerViewNotas.smoothScrollToPosition(0);
                        Toast.makeText(getApplicationContext(), R.string.toast_nota_guardada, Toast.LENGTH_LONG).show();
                        break;

                    case ACTUALIZAR_NOTA:
                        listaNotas.remove(posicion);

                        // Comprobar si es para eliminar o actualizar
                        if (nota_borrada) {
                            adaptadorNotas.notifyItemRemoved(posicion);
                            Toast.makeText(getApplicationContext(), R.string.toast_nota_borrada, Toast.LENGTH_LONG).show();
                        } else {
                            listaNotas.add(posicion, notas.get(posicion));
                            adaptadorNotas.notifyItemChanged(posicion);
                            Toast.makeText(getApplicationContext(), R.string.toast_nota_actualizada, Toast.LENGTH_LONG).show();
                        }
                        break;

                    case MOVER_NOTA:
                        listaNotas.remove(posicion);
                        adaptadorNotas.notifyItemRemoved(posicion);
                }
            }
        }
        new TareaGetNotas().execute();
    }

    /**
     * Acciones del men?? lateral (<i>DrawerLayout</i> y <i>NavigationView</i>).
     */
    @SuppressLint("NonConstantResourceId")
    private void menuDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigationView);
        // Anchura del NavigationView
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels / 1.5);
        navigationView.setLayoutParams(params);

        navigationView.getMenu().getItem(0).setChecked(true);

        // Abrir el men?? lateral al pulsar en la imagen
        findViewById(R.id.imagenMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Esto se ampliar?? para que seg??n se clique, mostrar notas, archivo o papelera
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_notas:
                    getNotas(MOSTRAR_NOTAS, false, 0);
                    titulo.setText(R.string.mis_notas);
                    item.setChecked(true);
                    mostrarElementos(true);
                    return true;

                case R.id.menu_archivo:
                    getNotas(MOSTRAR_NOTAS, false, 1);
                    titulo.setText(R.string.archivo);
                    item.setChecked(true);
                    mostrarElementos(false);
                    return true;

                case R.id.menu_papelera:
                    getNotas(MOSTRAR_NOTAS, false, 2);
                    titulo.setText(R.string.papelera);
                    item.setChecked(true);
                    mostrarElementos(false);
                    return true;

                case R.id.menu_opciones:
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;

                case R.id.menu_codigo:
                    Intent github = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/JJMD1999/NotaPlus"));
                    startActivity(github);
                    return true;

                case R.id.menu_licencia:
                    Intent licencia = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/JJMD1999/NotaPlus/blob/master/LICENSE"));
                    startActivity(licencia);
                    return true;
            }
            return true;
        });
    }

    /**
     * Muestra las barras de b??squeda y acciones s??lo cuando se muestren todas las notas.
     *
     * @param mostrar Booleano.
     */
    private void mostrarElementos(boolean mostrar) {
        if (mostrar) {
            barraDeBusqueda.setVisibility(View.VISIBLE);
            a??adirNota.setVisibility(View.VISIBLE);
            barraDeAcciones.setVisibility(View.VISIBLE);
        } else {
            barraDeBusqueda.setVisibility(View.GONE);
            a??adirNota.setVisibility(View.GONE);
            barraDeAcciones.setVisibility(View.GONE);
        }
    }

    /**
     * M??todo que se encarga de cargar las preferencias al inicio y mientras se ejecuta la aplicaci??n.
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
        listenerPrefs = (prefs, key) -> {
            switch (key) {
                case SettingsActivity.KEY_PREF_TEMA:
                    if (prefs.getBoolean(key, false)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    break;
                case SettingsActivity.KEY_PREF_DISPOSICION:
                    if (prefs.getBoolean(key, false)) {
                        recyclerViewNotas.setLayoutManager(
                                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    } else {
                        recyclerViewNotas.setLayoutManager(
                                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    }
                    recyclerViewNotas.setAdapter(adaptadorNotas);
                    break;
                case SettingsActivity.KEY_PREF_FUENTE:
                    recyclerViewNotas.setAdapter(adaptadorNotas);
                    recyclerViewNotas.smoothScrollToPosition(0);
                    navigationView.getMenu().getItem(0).setChecked(true);
                    break;
            }
        };
        preferencias.registerOnSharedPreferenceChangeListener(listenerPrefs);
    }

    /**
     * A partir de la versi??n 8.0 (Oreo) es necesario crear un canal para las notificaciones.
     */
    @SuppressLint("ObsoleteSdkInt")
    private void crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.canal_nombre);
            String descripcion = getString(R.string.canal_descripcion);

            NotificationChannel canal = new NotificationChannel(Notificacion.ID_CANAL, nombre, NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(canal);
        }
    }

    /**
     * Funciones de los botones de acceso r??pido.
     */
    private void accesosDirectos() {
        findViewById(R.id.a??adirRecordatorioInicio).setOnClickListener(v -> mostrarDialogoRecordatorio());

        findViewById(R.id.a??adirImagen).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                seleccionarImagen();
            }
        });

        findViewById(R.id.a??adirURL).setOnClickListener(v -> mostrarDialogoURL());
        findViewById(R.id.vozATexto).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_RECORD_AUDIO);
            } else {
                vozATexto();
            }
        });
    }

    /**
     * M??todo para seleccionar una imagen de la galer??a.
     */
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, SELECCIONAR_IMAGEN);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Devuelve la ruta de una imagen en formato <i>String</i>.
     *
     * @param uri Uri de la imagen.
     * @return La ruta de la imagen.
     */
    private String getRutaImagen(Uri uri) {
        String ruta;
        Cursor cursor = getContentResolver().query(uri, null, null, null);

        if (cursor == null) {
            ruta = uri.getPath();
        } else {
            cursor.moveToFirst();
            int indice = cursor.getColumnIndex("_data");
            ruta = cursor.getString(indice);
        }
        assert cursor != null;
        cursor.close();

        return ruta;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarImagen();
            } else {
                Toast.makeText(this, R.string.toast_denegado, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_RECORD_AUDIO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                vozATexto();
            } else {
                Toast.makeText(this, R.string.toast_denegado, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Muestra un di??logo <i>pop-up</i> para introducir una direcci??n web a la nota.
     */
    private void mostrarDialogoURL() {
        if (dialogoA??adirEnlace == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(MainActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_enlace,
                    findViewById(R.id.plantilla_enlace));
            constructor.setView(vista);

            dialogoA??adirEnlace = constructor.create();

            if (dialogoA??adirEnlace.getWindow() != null) {
                dialogoA??adirEnlace.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText textoURL = vista.findViewById(R.id.textoURL);
            textoURL.requestFocus();

            vista.findViewById(R.id.botonA??adirURL).setOnClickListener(v -> {
                if (textoURL.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_inserta_enlace, Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(textoURL.getText().toString()).matches()) {
                    Toast.makeText(MainActivity.this, R.string.toast_inserta_enlace_valido, Toast.LENGTH_SHORT).show();
                } else {
                    dialogoA??adirEnlace.dismiss();
                    Intent intent = new Intent(getApplicationContext(), CrearNotaActivity.class);
                    intent.putExtra("accesoDirecto", true);
                    intent.putExtra("accion", "url");
                    intent.putExtra("url", textoURL.getText().toString());
                    startActivityForResult(intent, A??ADIR_NOTA);
                }
            });

            vista.findViewById(R.id.botonCancelarURL).setOnClickListener(v -> dialogoA??adirEnlace.dismiss());
        }
        dialogoA??adirEnlace.show();
    }

    /**
     * Muestra un di??logo <i>pop-up</i> para introducir una direcci??n web a la nota.
     */
    private void mostrarDialogoRecordatorio() {
        if (dialogoRecordatorio == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(MainActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_recordatorio,
                    findViewById(R.id.plantilla_recordatorio));
            constructor.setView(vista);

            dialogoRecordatorio = constructor.create();

            if (dialogoRecordatorio.getWindow() != null) {
                dialogoRecordatorio.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText fechaRecordatorio = vista.findViewById(R.id.fechaRecordatorio);
            fechaRecordatorio.setText(tiempoActual(true));

            // Di??logo para escoger fecha
            fechaRecordatorio.setOnClickListener(v -> {
                DatePickerDialog.OnDateSetListener dateSetListener = (view, a??o, mes, diaMes) -> {
                    calendario.set(Calendar.YEAR, a??o);
                    calendario.set(Calendar.MONTH, mes);
                    calendario.set(Calendar.DAY_OF_MONTH, diaMes);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    fechaRecordatorio.setText(dateFormat.format(calendario.getTime()));
                };
                new DatePickerDialog(MainActivity.this, dateSetListener,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            });

            EditText horaRecordatorio = vista.findViewById(R.id.horaRecordatorio);
            horaRecordatorio.setText(tiempoActual(false));

            // Di??logo para escoger hora
            vista.findViewById(R.id.horaRecordatorio).setOnClickListener(v -> {
                TimePickerDialog.OnTimeSetListener timeSetListener = (view, horaDia, minuto) -> {
                    calendario.set(Calendar.HOUR_OF_DAY, horaDia);
                    calendario.set(Calendar.MINUTE, minuto);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    horaRecordatorio.setText(dateFormat.format(calendario.getTime()));
                };
                new TimePickerDialog(MainActivity.this, timeSetListener,
                        calendario.get(Calendar.HOUR_OF_DAY),
                        calendario.get(Calendar.MINUTE), true).show();
            });

            vista.findViewById(R.id.botonA??adirRecordatorio).setOnClickListener(v -> {
                if (fechaRecordatorio.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_inserta_fecha, Toast.LENGTH_SHORT).show();
                } else if (horaRecordatorio.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_inserta_hora, Toast.LENGTH_SHORT).show();
                } else {
                    crearNotificacion();
                    dialogoRecordatorio.dismiss();
                }
            });

            vista.findViewById(R.id.botonCancelarRecordatorio).setOnClickListener(v -> dialogoRecordatorio.dismiss());
        }
        dialogoRecordatorio.show();
    }

    /**
     * Crea una notificaci??n con los par??metros indicados.
     */
    private void crearNotificacion() {
        Intent intent = new Intent(getApplicationContext(), Notificacion.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                Notificacion.ID_NOTIFICACION,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Cuando notifica
        AlarmManager alarma = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long tiempo = calendario.getTimeInMillis();

        alarma.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tiempo,
                pendingIntent
        );
        Toast.makeText(this, R.string.toast_recordatorio_creado, Toast.LENGTH_SHORT).show();
    }

    /**
     * Permite convertir la voz a texto.
     */
    private void vozATexto() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");
        startActivityForResult(intent, VOZ_A_TEXTO);
    }

    /**
     * Para cerrar el panel lateral al darle hacia atr??s en vez de cerrar la aplicaci??n.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Una vez se termine de crear una nota, se carga en el <i>MainActivity</i> al volver.
     *
     * @param requestCode C??digo de solicitud.
     * @param resultCode  C??digo de resultado.
     * @param data        Intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == A??ADIR_NOTA && resultCode == RESULT_OK) {
            getNotas(A??ADIR_NOTA, false, 0);
        } else if (requestCode == ACTUALIZAR_NOTA && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getBooleanExtra("moverNota", false)) {
                    getNotas(MOVER_NOTA, false, 0);
                } else {
                    getNotas(ACTUALIZAR_NOTA, data.getBooleanExtra("nota_borrada", false), 0);
                }
            }
        } else if (requestCode == SELECCIONAR_IMAGEN && resultCode == RESULT_OK) {
            Uri imagenUri = data.getData();
            if (imagenUri != null) {
                String ruta = getRutaImagen(imagenUri);
                Intent intent = new Intent(getApplicationContext(), CrearNotaActivity.class);
                intent.putExtra("accesoDirecto", true);
                intent.putExtra("accion", "imagen");
                intent.putExtra("ruta", ruta);
                startActivityForResult(intent, A??ADIR_NOTA);
            }
        } else if (requestCode == VOZ_A_TEXTO && resultCode == RESULT_OK) {
            String texto = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            Intent intent = new Intent(getApplicationContext(), CrearNotaActivity.class);
            intent.putExtra("accesoDirecto", true);
            intent.putExtra("accion", "voz");
            intent.putExtra("texto", texto);
            startActivityForResult(intent, A??ADIR_NOTA);
        }
    }
}