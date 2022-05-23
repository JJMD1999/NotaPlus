package com.example.notaplus.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notaplus.R;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.tabla.Nota;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity que permite crear una nueva nota con el contenido introducido por el usuario.
 */
public class CrearNotaActivity extends AppCompatActivity {

    /**
     * Solicitud de permiso para acceder alalmacenamiento.
     */
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    /**
     * Solicitud de permiso para acceder a las imágenes.
     */
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private ImageView atras, check, etiqueta, recordatorio, archivo, papelera;
    private ShapeableImageView imagenNota;
    private EditText tituloNota, cuerpoNota;
    private TextView fechaNota, textoEnlaceWeb, etiquetaNota;
    private LinearLayout layoutURLNota;
    private String colorSeleccionado, imagenSeleccionada, string_etiqueta;
    private AlertDialog dialogoAñadirEnlace, dialogoAgregarEtiqueta, dialogoBorrarNota;
    private Nota notaExistente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_nota);

        asignarElementos();

        atras.setOnClickListener(v -> {
            onBackPressed();
            Toast.makeText(this, "Cambios descartados", Toast.LENGTH_SHORT).show();
        }); // Vuelve a atrás

        check.setOnClickListener(v -> guardarNota()); // LLama a guardarNota() cuando se presiona

        // Para borrar imagen de la nota
        findViewById(R.id.borrarImagenNota).setOnClickListener(v -> {
            imagenNota.setImageBitmap(null);
            imagenNota.setVisibility(View.GONE);
            findViewById(R.id.borrarImagenNota).setVisibility(View.GONE);
            imagenSeleccionada = "";
        });

        // Para borrar enlace web de la nota
        findViewById(R.id.borrarEnlaceWebNota).setOnClickListener(v -> {
            textoEnlaceWeb.setText(null);
            layoutURLNota.setVisibility(View.GONE);
        });

        existeNota();

        // Funcionalidad de los iconos superiores
        habilitarIconos();

        // Layout para la edición de la nota
        habilitarEdicion();
    }

    /**
     * Asignación de los componentes del layout al código.
     */
    private void asignarElementos() {
        atras = findViewById(R.id.atras);
        check = findViewById(R.id.check);
        tituloNota = findViewById(R.id.tituloNota);
        cuerpoNota = findViewById(R.id.cuerpoNota);
        imagenNota = findViewById(R.id.imagenNota);
        fechaNota = findViewById(R.id.fechaNota);
        // Establece la hora actual a la fecha. (20/03/2022 17:02 PM)
        fechaNota.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.getDefault()).format(new Date()));
        layoutURLNota = findViewById(R.id.layoutURLNota);
        textoEnlaceWeb = findViewById(R.id.textoEnlaceWebNota);
        etiquetaNota = findViewById(R.id.etiquetaNota);
        // Asignar color e imagen predeterminados (Si no puede generar comportamientos inesperados)
        colorSeleccionado = getResources().getString(0 + R.color.fondo_nota);
        imagenSeleccionada = "";
        // Iconos superiores
        etiqueta = findViewById(R.id.añadirEtiqueta);
        recordatorio = findViewById(R.id.añadirRecordatorio);
        archivo = findViewById(R.id.añadirArchivo);
        papelera = findViewById(R.id.añadirPapelera);

    }

    /**
     * Comprueba que la nota existe para cargar los datos de la misma
     */
    private void existeNota() {
        if (getIntent().getBooleanExtra("existe_nota", false)) {
            notaExistente = (Nota) getIntent().getSerializableExtra("nota");

            tituloNota.setText(notaExistente.getTitulo());
            cuerpoNota.setText(notaExistente.getTexto());
            fechaNota.setText(notaExistente.getFecha());
            colorSeleccionado = notaExistente.getColor();

            if (notaExistente.getImagen() != null && !notaExistente.getImagen().isEmpty()) {
                imagenNota.setImageBitmap(BitmapFactory.decodeFile(notaExistente.getImagen()));
                imagenNota.setVisibility(View.VISIBLE);
                findViewById(R.id.borrarImagenNota).setVisibility(View.VISIBLE);
                imagenSeleccionada = notaExistente.getImagen();
            }

            if (notaExistente.getUrl() != null) {
                textoEnlaceWeb.setText(notaExistente.getUrl());
                layoutURLNota.setVisibility(View.VISIBLE);
            }

            if (notaExistente.getEtiqueta() != null) {
                etiquetaNota.setText(notaExistente.getEtiqueta());
                etiquetaNota.setVisibility(View.VISIBLE);

                if (etiquetaNota.getText().toString().equals("_archivada")) {
                    etiqueta.setVisibility(View.GONE);
                    etiquetaNota.setVisibility(View.GONE);
                    archivo.setImageResource(R.drawable.ic_desarchivar);
                }
                if (etiquetaNota.getText().toString().equals("_papelera")) {
                    etiqueta.setVisibility(View.GONE);
                    etiquetaNota.setVisibility(View.GONE);
                    papelera.setImageResource(R.drawable.ic_sacar_de_papelera);
                }
            }
        } else {
            archivo.setVisibility(View.GONE);
            papelera.setVisibility(View.GONE);
        }
    }

    /**
     * Guarda la nota en la base de datos.
     */
    private void guardarNota() {
        if (tituloNota.getText().toString().isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_LONG).show();
        } else if (cuerpoNota.getText().toString().isEmpty()) {
            Toast.makeText(this, "La nota no puede estar vacía", Toast.LENGTH_LONG).show();
        } else {
            Nota nota = new Nota();
            nota.setTitulo(tituloNota.getText().toString());
            nota.setTexto(cuerpoNota.getText().toString());
            nota.setFecha(fechaNota.getText().toString());
            nota.setColor(colorSeleccionado);
            nota.setImagen(imagenSeleccionada);

            if (layoutURLNota.getVisibility() == View.VISIBLE) {
                nota.setUrl(textoEnlaceWeb.getText().toString());
            }

            if (etiquetaNota.getVisibility() == View.VISIBLE) {
                nota.setEtiqueta(etiquetaNota.getText().toString());
            } else {
                if (etiquetaNota.getText().toString().equals("_archivada")
                        || etiquetaNota.getText().toString().equals("_papelera")) {
                    nota.setEtiqueta(etiquetaNota.getText().toString());
                }
            }

            // Si hemos seleccionado una nota existente, se le asigna el mismo id, ya que esta reemplazará a la existente
            if (notaExistente != null) {
                nota.setId(notaExistente.getId());
            }

            // Hilo secundario, ya que no se permiten operaciones de bases de datos en el hilo principal
            class TareaGuardarNota extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().insertarNota(nota);
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    super.onPostExecute(v);
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
            new TareaGuardarNota().execute();
        }
    }

    /**
     * Permite editar las características de las notas.
     */
    private void habilitarEdicion() {
        LinearLayout plantilla_editar_nota = findViewById(R.id.plantilla_editar_nota);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(plantilla_editar_nota);
        plantilla_editar_nota.findViewById(R.id.edicion).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        ImageView imagenColorPredeterminado = plantilla_editar_nota.findViewById(R.id.imagenColorPredeterminado);
        ImageView imagenColorAzul = plantilla_editar_nota.findViewById(R.id.imagenColorAzul);
        ImageView imagenColorRojo = plantilla_editar_nota.findViewById(R.id.imagenColorRojo);
        ImageView imagenColorNaranja = plantilla_editar_nota.findViewById(R.id.imagenColorNaranja);
        ImageView imagenColorAmarillo = plantilla_editar_nota.findViewById(R.id.imagenColorAmarillo);

        // Al clicar en un color (En este caso el predeterminado)
        plantilla_editar_nota.findViewById(R.id.view_color_predeterminado).setOnClickListener(v -> {
            colorSeleccionado = getResources().getString(0 + R.color.fondo_nota); // Escoger el color predeterminado
            imagenColorPredeterminado.setImageResource(R.drawable.ic_check);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_azul).setOnClickListener(v -> {
            colorSeleccionado = "#0BB1E3";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(R.drawable.ic_check);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_rojo).setOnClickListener(v -> {
            colorSeleccionado = "#F10808";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(R.drawable.ic_check);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_naranja).setOnClickListener(v -> {
            colorSeleccionado = "#F36C05";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(R.drawable.ic_check);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_amarillo).setOnClickListener(v -> {
            colorSeleccionado = "#E1E10D";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(R.drawable.ic_check);
        });

        // En caso de mostrar una nota existente
        if (notaExistente != null && notaExistente.getColor() != null) {
            switch (notaExistente.getColor()) {
                case "#0BB1E3":
                    plantilla_editar_nota.findViewById(R.id.view_azul).performClick();
                    break;
                case "#F10808":
                    plantilla_editar_nota.findViewById(R.id.view_rojo).performClick();
                    break;
                case "#F36C05":
                    plantilla_editar_nota.findViewById(R.id.view_naranja).performClick();
                    break;
                case "#E1E10D":
                    plantilla_editar_nota.findViewById(R.id.view_amarillo).performClick();
                    break;
                default:
                    plantilla_editar_nota.findViewById(R.id.view_color_predeterminado).performClick();
            }
        }

        // Añadir Imagen
        plantilla_editar_nota.findViewById(R.id.layoutAñadirImagen).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // Comprueba si tiene permiso para acceder al almacenamiento del dispositivo
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CrearNotaActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                seleccionarImagen();
            }
        });

        // Muestra el diálogo para añadir una URL
        plantilla_editar_nota.findViewById(R.id.layoutAñadirURL).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mostrarDialogoURL();
        });

        // Borrar nota
        if (notaExistente != null) {
            plantilla_editar_nota.findViewById(R.id.layoutEliminarNota).setVisibility(View.VISIBLE);
            plantilla_editar_nota.findViewById(R.id.layoutEliminarNota).setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mostrarDialogoBorrarNota();
            });
        }
    }

    /**
     * Métodos y funciones de los iconos superiores.
     */
    @SuppressLint("SetTextI18n")
    private void habilitarIconos() {
        etiqueta.setOnClickListener(v -> {
            mostrarDialogoEtiqueta();
        });

        etiquetaNota.setOnClickListener(v -> {
            if (!etiquetaNota.getText().toString().isEmpty()) {
                string_etiqueta = "";
                etiquetaNota.setText("");
                etiquetaNota.setVisibility(View.GONE);
                Toast.makeText(this, "Etiqueta eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        recordatorio.setOnClickListener(v -> {
            // TODO: Notificaciones
        });

        archivo.setOnClickListener(v -> {
            if (etiquetaNota.getText().toString().equals("_papelera")) {
                papelera.setImageResource(R.drawable.ic_papelera);
            }
            if (!etiquetaNota.getText().toString().equals("_archivada")) {
                etiqueta.setVisibility(View.GONE);
                etiquetaNota.setVisibility(View.GONE);
                etiquetaNota.setText("_archivada");
                archivo.setImageResource(R.drawable.ic_desarchivar);
                Toast.makeText(this, "La nota se archivará", Toast.LENGTH_SHORT).show();
            } else {
                etiqueta.setVisibility(View.VISIBLE);
                etiquetaNota.setVisibility(string_etiqueta == null || string_etiqueta.isEmpty() ? View.GONE : View.VISIBLE);
                etiquetaNota.setText(string_etiqueta);
                archivo.setImageResource(R.drawable.ic_archivo);
                Toast.makeText(this, "La nota se ya no se archivará", Toast.LENGTH_SHORT).show();
            }
        });

        papelera.setOnClickListener(v -> {
            if (etiquetaNota.getText().toString().equals("_archivada")) {
                archivo.setImageResource(R.drawable.ic_archivo);
            }
            if (!etiquetaNota.getText().toString().equals("_papelera")) {
                etiqueta.setVisibility(View.GONE);
                etiquetaNota.setVisibility(View.GONE);
                etiquetaNota.setText("_papelera");
                papelera.setImageResource(R.drawable.ic_sacar_de_papelera);
                Toast.makeText(this, "La nota se moverá a la papelera", Toast.LENGTH_SHORT).show();
            } else {
                etiqueta.setVisibility(View.VISIBLE);
                etiquetaNota.setVisibility(string_etiqueta == null || string_etiqueta.isEmpty() ? View.GONE : View.VISIBLE);
                etiquetaNota.setText(string_etiqueta);
                papelera.setImageResource(R.drawable.ic_papelera);
                Toast.makeText(this, "La nota ya no se moverá a la papelera", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para seleccionar una imagen de la galería.
     */
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método que maneja las solicitudes de permiso de la aplicación.
     *
     * @param requestCode  Código de solicitud.
     * @param permissions  Permisos solicitados.
     * @param grantResults Resultado. Ya sea <i>CONCEDIDO</i> o <i>DENEGADO</i>.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarImagen();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método para seleccionar una imagen de la galería e insertarla en la nota.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode  Código de resultado
     * @param data        Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri imagenUri = data.getData();
            if (imagenUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imagenUri);
                    Bitmap imagen = BitmapFactory.decodeStream(inputStream);

                    imagenNota.setImageBitmap(imagen);
                    imagenNota.setVisibility(View.VISIBLE);
                    findViewById(R.id.borrarImagenNota).setVisibility(View.VISIBLE);

                    imagenSeleccionada = getRutaImagen(imagenUri);

                } catch (FileNotFoundException ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Devuelve la ruta de una imagen en formato <i>String</i>.
     *
     * @param uri Uri de la imagen
     * @return La ruta de la imagen
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

    /**
     * Muestra un diálogo <i>pop-up</i> para introducir una dirección web a la nota
     */
    private void mostrarDialogoURL() {
        if (dialogoAñadirEnlace == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(CrearNotaActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_enlace,
                    findViewById(R.id.plantilla_enlace));
            constructor.setView(vista);

            dialogoAñadirEnlace = constructor.create();

            if (dialogoAñadirEnlace.getWindow() != null) {
                dialogoAñadirEnlace.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText textoURL = vista.findViewById(R.id.textoURL);
            textoURL.requestFocus();

            vista.findViewById(R.id.botonAñadirURL).setOnClickListener(v -> {
                if (textoURL.getText().toString().isEmpty()) {
                    Toast.makeText(CrearNotaActivity.this, "Inserta un enlace web", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(textoURL.getText().toString()).matches()) {
                    Toast.makeText(CrearNotaActivity.this, "Inserta un enlace web válido", Toast.LENGTH_SHORT).show();
                } else {
                    textoEnlaceWeb.setText(textoURL.getText().toString());
                    layoutURLNota.setVisibility(View.VISIBLE);
                    dialogoAñadirEnlace.dismiss();
                }
            });

            vista.findViewById(R.id.botonCancelarURL).setOnClickListener(v -> dialogoAñadirEnlace.dismiss());
        }
        dialogoAñadirEnlace.show();
    }

    /**
     * Muestra un diálogo <i>pop-up</i> para introducir una dirección web a la nota
     */
    private void mostrarDialogoEtiqueta() {
        if (dialogoAgregarEtiqueta == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(CrearNotaActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_etiqueta,
                    findViewById(R.id.plantilla_etiqueta));
            constructor.setView(vista);

            dialogoAgregarEtiqueta = constructor.create();

            if (dialogoAgregarEtiqueta.getWindow() != null) {
                dialogoAgregarEtiqueta.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText etiqueta = vista.findViewById(R.id.textoEtiqueta);
            etiqueta.requestFocus();

            vista.findViewById(R.id.botonAñadirEtiqueta).setOnClickListener(v -> {
                if (etiqueta.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CrearNotaActivity.this, "Inserta una etiqueta", Toast.LENGTH_SHORT).show();
                } else {
                    etiquetaNota.setText(etiqueta.getText().toString());
                    string_etiqueta = etiqueta.getText().toString(); // variable comodín
                    etiquetaNota.setVisibility(View.VISIBLE);
                    dialogoAgregarEtiqueta.dismiss();
                }
            });

            vista.findViewById(R.id.botonCancelarEtiqueta).setOnClickListener(v -> dialogoAgregarEtiqueta.dismiss());
        }
        dialogoAgregarEtiqueta.show();
    }

    /**
     * Muestra un <i>AlertDialog</i> para confirmar el borrado de la nota y la borra.
     */
    private void mostrarDialogoBorrarNota() {
        if (dialogoBorrarNota == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(CrearNotaActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_borrar_nota,
                    findViewById(R.id.plantilla_borrar_nota));
            constructor.setView(vista);

            dialogoBorrarNota = constructor.create();

            if (dialogoBorrarNota.getWindow() != null) {
                dialogoBorrarNota.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            vista.findViewById(R.id.botonBorrarNota).setOnClickListener(v -> {
                class TareaEliminarNota extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().eliminarNota(notaExistente);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void vacio) {
                        super.onPostExecute(vacio);
                        Intent intent = new Intent();
                        intent.putExtra("nota_borrada", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                new TareaEliminarNota().execute();

            });
            // Para cancelar...
            vista.findViewById(R.id.botonCancelarBorrarNota).setOnClickListener(v -> dialogoBorrarNota.dismiss());
        }
        dialogoBorrarNota.show();
    }
}