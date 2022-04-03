package com.example.notaplus.tabla;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Entidad "Nota". Representa la tabla donde estarán alojadas las notas.
 */
@Entity(tableName = "notas")
public class Nota implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "texto")
    private String texto;

    @ColumnInfo(name = "imagen")
    private String imagen;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "color")
    private String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @NonNull
    @Override
    public String toString() {
        return titulo + " : " + fecha;
    }
}
