package com.example.notaplus.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
     * Solicitud de permiso para accedera las imágenes.
     */
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private ImageView atras, check, imagenNota;
    private EditText tituloNota, cuerpoNota;
    private TextView fechaNota, textoEnlaceWeb;
    private LinearLayout layoutURLNota;
    private String colorSeleccionado, imagenSeleccionada;
    private AlertDialog dialogoAlerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_nota);

        AsignarElementos();
        atras.setOnClickListener(v -> onBackPressed()); // Vuelve a atrás
        check.setOnClickListener(v -> GuardarNota()); // LLama a GuardarNota() cuando se presiona

        habilitarEdicion();
    }

    /**
     * Asignación de los componentes del layout al código.
     */
    private void AsignarElementos() {
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
    }

    /**
     * Guarda la nota en la base de datos.
     */
    private void GuardarNota() {
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

            // Hilo secundario, ya que no se permiten operaciones de bases de datos en el hilo principal
            class TareaGuardarNota extends AsyncTask<Void, Void, Void>{

                @Override
                protected Void doInBackground(Void... voids) {
                    BaseDeDatos.getBaseDeDatos(getApplicationContext()).dao_nota().insertarNota(nota);
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    super.onPostExecute(v);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getApplicationContext(), "Nota guardada", Toast.LENGTH_LONG).show();
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
            colorSeleccionado = "#00C4FF";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(R.drawable.ic_check);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_rojo).setOnClickListener(v -> {
            colorSeleccionado = "##FF0000";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(R.drawable.ic_check);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_naranja).setOnClickListener(v -> {
            colorSeleccionado = "#FF6F00";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(R.drawable.ic_check);
            imagenColorAmarillo.setImageResource(0);
        });

        plantilla_editar_nota.findViewById(R.id.view_amarillo).setOnClickListener(v -> {
            colorSeleccionado = "#F1F106";
            imagenColorPredeterminado.setImageResource(0);
            imagenColorAzul.setImageResource(0);
            imagenColorRojo.setImageResource(0);
            imagenColorNaranja.setImageResource(0);
            imagenColorAmarillo.setImageResource(R.drawable.ic_check);
        });

        // Añadir Imagen
        plantilla_editar_nota.findViewById(R.id.layoutAñadirImagen).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // Comprueba si tiene permiso para acceder al almacenamiento del dispositivo
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CrearNotaActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                SeleccionarImagen();
            }
        });

        // Muestra el diálogo para añadir una URL
        plantilla_editar_nota.findViewById(R.id.layoutAñadirURL).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mostrarDialogoURL();
        });
    }

    /**
     * Método para seleccionar una imagen de la galería.
     */
    private void SeleccionarImagen() {
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
     * @param requestCode Código de solicitud.
     * @param permissions Permisos solicitados.
     * @param grantResults Resultado. Ya sea <i>CONCEDIDO</i> o <i>DENEGADO</i>.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SeleccionarImagen();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método para seleccionar una imagen de la galería e insertarla en la nota.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode Código de resultado
     * @param data Intent
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
        return ruta;
    }

    private void mostrarDialogoURL() {
        if (dialogoAlerta == null) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(CrearNotaActivity.this);
            View vista = LayoutInflater.from(this).inflate(
                    R.layout.plantilla_enlace,
                    findViewById(R.id.plantilla_enlace_contenedor));
            constructor.setView(vista);

            dialogoAlerta = constructor.create();

            if (dialogoAlerta.getWindow() != null) {
                dialogoAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(0));
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
                    dialogoAlerta.dismiss();
                }
            });

            vista.findViewById(R.id.botonCancelarURL).setOnClickListener(v -> dialogoAlerta.dismiss());
        }
        dialogoAlerta.show();
    }
}