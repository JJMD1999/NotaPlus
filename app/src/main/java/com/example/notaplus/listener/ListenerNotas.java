package com.example.notaplus.listener;

import com.example.notaplus.tabla.Nota;

public interface ListenerNotas {
    /**
     * Listener para recibir la nota y la posición de la misma en el RecyclerView
     *
     * @param nota     Nota
     * @param posicion Posición de la nota en el RecyclerView
     */
    void onClickNota(Nota nota, int posicion);
}
