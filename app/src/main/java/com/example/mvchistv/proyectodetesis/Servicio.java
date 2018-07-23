package com.example.mvchistv.proyectodetesis;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.mvchistv.proyectodetesis.Login.USUARIOWEB;

public class Servicio extends Service {
    static boolean vibrar = false;
    static MediaPlayer mp;
    static int contadorMp = 0;
    static Handler handler = new Handler();
    static Runnable myRunnable;
    static Vibrator vibrator;
    static Servicio instance=null;
    static NotificationCompat.Builder mbuilder;
    static String resultadoFinal;
    static boolean salirDelServicio=true;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        instance = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Log.d("TOKEN ACTUAL",FirebaseInstanceId.getInstance().getToken());


        Intent notificationIntent = new Intent(Servicio.this, SegundaVentana.class);
        notificationIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        notificationIntent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(Servicio.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        String channelId = "my_channel_1";
        NotificationManager mNotificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        mbuilder= new NotificationCompat.Builder(this,null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //**** si la version de Android es mayor o igual a 8
            //**** se crean estos parametros para la Notificación
            CharSequence name = getString(R.string.app_name);
            String description = " Toca para Volver a la Aplicación";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId,name,importance);
            mChannel.setDescription(description);

            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);

            mbuilder= new NotificationCompat.Builder(this,channelId);
            Log.i("NOTIFICACION", "PASO POR NOTIFICACION PARA ANDROID 8");

        }
        //**** parametros normales para la Notificación en cualquier android
        mbuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(" Toca para Volver a la Aplicación")
                .setContentIntent(pendingIntent);
        Log.i("NOTIFICACION", "PASO POR NOTIFICACION PARA ANDROID CUALQUIER ANDROID");


        //**** Poner Notifcacion en primer plano
        startForeground(1,mbuilder.build());


        Log.i("SERVICIO", "INICIO DEL SERVICIO");
        return START_REDELIVER_INTENT; //***startREDELIVER reinicia el servicio aunque quede poca memoria

    }
    public static Servicio getsInstance(){
        return instance;
    }
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
    }
    public static Context getAppContext(){
        return getsInstance().getApplicationContext();
    }

    public String CargarValorEstado(JSONArray ja) {

        ArrayList<String> lista = new ArrayList<>();
        for (int i = 0; i < ja.length(); i += 1) {
            try {
                //** carga el valor del estado en lista y lo retorna
                lista.add(ja.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lista.get(0);
    }

    public void Consulta() {
        //** esta funcion activa la alarma pero sigue consultando por el valor del
        //** Estado en la Base de datos, por si el arduino lo cambia antes de presionar el boton Desactivar
        RequestQueue requestQueue = Volley.newRequestQueue(Servicio.getAppContext());
        String consulta = "https://ivanvidalsepulveda.000webhostapp.com/consultarEstado.php?email="+USUARIOWEB;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);

                        resultadoFinal = CargarValorEstado(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                    if (resultadoFinal.equals("D")) {
                        //** si el arduino cambia el valor del estado
                        //** aqui se detecta, se desactiva la alarma
                        //** y se vuelve a iniciar la funcion Consultar
                        Log.i("CONSULTA", "VALOR=DESACTIVADA");
                        DesactivarAlarma();

                    }else {
                        if (resultadoFinal.equals("A")){
                            Log.i("CONSULTA", "VALOR=ACTIVADA");
                        //** carga la imagen de Peligro en la Actividad Segunda Ventana
                        SegundaVentana.texto.setImageResource(R.drawable.peligro);
                        //** activa el boton desactivar para poder ser presionado
                        SegundaVentana.btnDesactivar.setEnabled(true);
                        vibrar = true;
                        contadorMp = 1;
                        myRunnable = new Runnable() {
                            public void run() {
                                if (vibrar) {
                                    vibrator.vibrate(500);
                                    if (contadorMp == 1) {
                                        iniciarSonido();
                                    }
                                }
                               Consulta();
                            }

                        };

                        handler.postDelayed(myRunnable,1500);
                        //** ejecuta la tarea de activar alarmas cada 1 segundo

                    }
                }}


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //** si la consulta volley posee error o se desconecta el internet
                //** se sigue ejecutando IniciarAlarma hasta que detecte algo
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Consulta();
                    }
                }, 800);
            }
        });

        requestQueue.add(stringRequest);
    }


    public void DesactivarAlarma() {


        handler.removeCallbacksAndMessages(myRunnable);
        handler.removeCallbacks(myRunnable);
        SegundaVentana.btnDesactivar.setEnabled(false);
        SegundaVentana.texto.setImageResource(R.drawable.zonasegura);
        vibrar = false;
        detenerSonido();
        contadorMp = 0;

        Log.i("DESACTIVAR-ALARMA", "DESACTIVANDO");
        //** detiene la tarea de ejecutar activar alarmas
        RequestQueue requestQueue = Volley.newRequestQueue(Servicio.getAppContext());
        String desactivarphp = "https://ivanvidalsepulveda.000webhostapp.com/desactivar.php?email="+USUARIOWEB;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, desactivarphp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DesactivarAlarma(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);



    }

    public void eliminarToken() {

        RequestQueue requestQueue = Volley.newRequestQueue(Servicio.getAppContext());
        String eliminartoken = "https://ivanvidalsepulveda.000webhostapp.com/eliminarToken.php?email="+USUARIOWEB;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, eliminartoken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                eliminarToken(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);



    }

    public static void destruirSonido() {
        if (mp != null) {
            mp.release();
        }
    }

    public static void iniciarSonido() {
        destruirSonido();
        mp = MediaPlayer.create(Servicio.getAppContext(), R.raw.alarma2);
        mp.start();
    }

    public static void detenerSonido() {
        if (mp != null) {
            mp.stop();

        }
    }




    public void detenerRunnable(){
        //** detiene la ejecucion de alarma activada
        Log.d("RUNNABLE","DETENIENDO RUNNABLE SONIDO + VIBRACION");
        handler.removeCallbacksAndMessages(myRunnable);
        handler.removeCallbacks(myRunnable);

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        if (salirDelServicio){
            Log.i("BROADCAST", "LLAMANDO A BROADCAST");
            Intent broadcastIntent = new Intent(getApplicationContext(),BroadcastReceiverReanudarServicio.class);
            sendBroadcast(broadcastIntent);
        }
        Log.i("SERVICIO", "SALIENDO DEL SERVICIO");

    }
}
