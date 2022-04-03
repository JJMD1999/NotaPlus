package com.example.notaplus.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notaplus.R;
import com.example.notaplus.bbdd.BaseDeDatos;
import com.example.notaplus.tabla.Nota;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity que permite crear una nueva nota con el contenido introducido por el usuario
 */
public class CrearNotaActivity extends AppCompatActivity {

    private ImageView atras, check;
    private EditText tituloNota, cuerpoNota;
    private TextView fechaNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_nota);

        AsignarElementos();
        atras.setOnClickListener(v -> onBackPressed()); // Vuelve a atrás
        check.setOnClickListener(v -> GuardarNota()); // LLama a GuardarNota() cuando se presiona
    }

    /**
     * Asignación de los componentes del layout al código
     */
    private void AsignarElementos() {
        atras = findViewById(R.id.atras);
        check = findViewById(R.id.check);
        tituloNota = findViewById(R.id.tituloNota);
        cuerpoNota = findViewById(R.id.cuerpoNota);
        fechaNota = findViewById(R.id.fechaNota);
        // Establece la hora actual a la fecha. (20/03/2022 17:02 PM)
        fechaNota.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.getDefault()).format(new Date()));
    }

    /**
     * Guarda la nota en la base de datos
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
}