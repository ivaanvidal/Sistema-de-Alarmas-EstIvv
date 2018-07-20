package com.example.mvchistv.proyectodetesis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BroadcastReceiverReanudarServicio extends BroadcastReceiver {

    final Login login= new Login();
    Intent servicioIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BROADCAST", "INICIO DEL BROADCAST");

        servicioIntent = new Intent(context, login.getClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //** si la version de android es mayor o igual a 8 se ejecutara el servicio de esta manera
            context.startActivity(servicioIntent);

            Log.i("BROADCAST", "PASO POR START FOREGROUND SERVICE Para android >= 8");
        }else{
            //** si la version de android es menor a 8 se ejecutara el servicio de esta manera
            context.startActivity(servicioIntent);
            Log.i("BROADCAST", "PASO POR START SERVICE");
        }

    }
}
