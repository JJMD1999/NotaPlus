package com.example.notaplus.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.notaplus.R;
import com.example.notaplus.actividades.MainActivity;

/**
 * Notificación que se usa para los recordatorios de la aplicación.
 */
public class Notificacion extends BroadcastReceiver {

    public static final int ID_NOTIFICACION = 1;
    public static final String ID_CANAL = "canal1";
    public static String titulo = "Recordatorio";
    public static String mensaje = "Tienes algo pendiente...";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Al presionar en la notificación, abre la MainActivity
        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), ID_NOTIFICACION, i, PendingIntent.FLAG_IMMUTABLE);

        if (intent.getStringExtra(titulo) != null) titulo = intent.getStringExtra(titulo);
        if (intent.getStringExtra(mensaje) != null) mensaje = intent.getStringExtra(mensaje);

        Notification notificacion = new NotificationCompat.Builder(context, ID_CANAL)
                .setSmallIcon(R.mipmap.ic_icono_app_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(ID_NOTIFICACION, notificacion);
    }
}
