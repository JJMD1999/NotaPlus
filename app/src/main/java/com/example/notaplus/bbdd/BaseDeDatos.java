package com.example.notaplus.bbdd;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notaplus.dao.Dao_Nota;
import com.example.notaplus.tabla.Nota;

/**
 * Base de datos "notas_db".
 */
@Database(entities = Nota.class, version = 1, exportSchema = false)
public abstract class BaseDeDatos extends RoomDatabase {

    private static BaseDeDatos baseDeDatos;
    private static final String NOMBRE = "notas_db";

    /**
     * Crea la base de datos usando la librería "Room" en caso de no existir y la devuelve.
     *
     * @param contexto Contexto de la aplicación.
     * @return La base de datos.
     */
    public static synchronized BaseDeDatos getBaseDeDatos(Context contexto) {
        if (baseDeDatos == null) {
            baseDeDatos = Room.databaseBuilder(contexto, BaseDeDatos.class, NOMBRE).build();
        }
        return baseDeDatos;
    }

    public abstract Dao_Nota dao_nota();
}
