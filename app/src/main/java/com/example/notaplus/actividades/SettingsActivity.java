package com.example.notaplus.actividades;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.notaplus.R;

/**
 * Actividad para guardar las opciones y preferencias del usuario.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Tema claro/oscuro
     */
    public static final String KEY_PREF_TEMA = "tema";
    /**
     * Disposición de las notas en columnas o filas
     */
    public static final String KEY_PREF_DISPOSICION = "columnas";
    /**
     * Tipo de fuente de las notas
     */
    public static final String KEY_PREF_FUENTE = "fuente";
    /**
     * Modelo del dispositivo
     */
    public static final String KEY_PREF_MODELO = "modelo";
    /**
     * Versión del dispositivo
     */
    public static final String KEY_PREF_VERSION = "version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.botonAtras).setOnClickListener(v -> onBackPressed());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferencias, rootKey);
            Preference modelo = findPreference(KEY_PREF_MODELO);
            modelo.setSummary(getModelo());

            Preference version = findPreference(KEY_PREF_VERSION);
            version.setSummary(Build.VERSION.RELEASE);
        }
    }

    /**
     * Devuelve el fabircante y el modelo del dispositivo en un formato específico.
     *
     * @return La cadena de texto con el fabricante y el modelo.
     */
    public static String getModelo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        if (modelo.toLowerCase().startsWith(fabricante.toLowerCase())) {
            return capitalizar(modelo);
        } else {
            return capitalizar(fabricante) + " " + modelo;
        }
    }

    /**
     * Capitaliza un String
     *
     * @param cadena Una cadena de texto.
     * @return La cadena de texto con el primer caracter en mayúscula.
     */
    private static String capitalizar(String cadena) {
        if (cadena == null || cadena.length() == 0) {
            return "";
        }
        char primerChar = cadena.charAt(0);
        if (Character.isUpperCase(primerChar)) {
            return cadena;
        } else {
            return Character.toUpperCase(primerChar) + cadena.substring(1);
        }
    }
}




