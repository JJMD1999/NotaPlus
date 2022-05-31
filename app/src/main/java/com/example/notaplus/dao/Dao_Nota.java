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
@SuppressWarnings("AndroidUnresolvedRoomSqlReference")
public interface Dao_Nota {

    /**
     * Consulta que devuelve todas las entradas(notas) de la base de datos
     *
     * @return Los elementos de la base de datos
     */
    @Query("SELECT * FROM notas ORDER BY id DESC")
    List<Nota> getAll();

    @Query("SELECT * FROM notas WHERE etiqueta IS NULL OR etiqueta NOT IN ('_archivada', '_papelera') ORDER BY id DESC")
    List<Nota> getNotas();

    @Query("SELECT * FROM notas WHERE etiqueta = '_archivada' ORDER BY id DESC")
    List<Nota> getArchivadas();

    @Query("SELECT * FROM notas WHERE etiqueta = '_papelera' ORDER BY id DESC")
    List<Nota> getPapelera();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarNota(Nota nota);

    @Delete
    void eliminarNota(Nota nota);

}
