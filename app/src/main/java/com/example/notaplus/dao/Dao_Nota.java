package com.example.notaplus.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notaplus.tabla.Nota;

import java.util.List;

/**
 * Dao_Nota de la base de datos.
 */
@Dao
public interface Dao_Nota {

    /**
     * Consulta que devuelve todas las entradas(notas) de la base de datos
     *
     * @return Los elementos de la base de datos
     */
    @SuppressWarnings("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT * FROM notas ORDER BY id DESC") // Funciona bien
    List<Nota> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarNota(Nota nota);

    @Delete
    void eliminarNota(Nota nota);

}
