package com.example.notaplus.listener;

import com.example.notaplus.tabla.Nota;

/**
 * Listener para recibir la nota y la posición de la misma en el RecyclerView.
 */
public interface ListenerNotas {
    /**
     * Método <i>onClick</i> para las notas.
     *
     * @param nota     Nota.
     * @param posicion Posición de la nota en el RecyclerView.
     */
    void onClickNota(Nota nota, int posicion);
}
