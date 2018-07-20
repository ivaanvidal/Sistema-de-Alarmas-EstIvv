package com.example.mvchistv.proyectodetesis;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class SegundaVentana extends AppCompatActivity  {
    @SuppressLint("StaticFieldLeak")
    static
     Button btnDesactivar;
    @SuppressLint("StaticFieldLeak")
    Button btnCerrarSesion;
    @SuppressLint("StaticFieldLeak")
    public static ImageView texto;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtUsuario;
    Intent servicioIntent;

    Servicio servicio= new Servicio();



    @Override

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //instance = this;
        setContentView(R.layout.activity_segunda_ventana);
        //**** crea intent para iniciar Servicio **//
        servicioIntent = new Intent(SegundaVentana.this, servicio.getClass());

        texto= findViewById(R.id.txtEstadoBd);
        texto.setImageResource(R.drawable.zonasegura);   //***** carga imagen de zona segura en pantalla ***//

        txtUsuario = findViewById(R.id.txtUsuarioWeb);
        btnDesactivar = findViewById(R.id.btnDesactivar);
        btnDesactivar.setOnClickListener(new View.OnClickListener() {
            //***** Boton que Desactiva la Alarma cuando esta encendida ***//
            @Override
            public void onClick(View v) {
               servicio.DesactivarAlarma();
            }
        });

        btnCerrarSesion = findViewById(R.id.btncerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //***** Boton para cerrar la sesion ***//
                //***** esta es una de las 2 maneras de cerrar sesion completamente ***//
                //***** y detener el servicio por completo, para luego pasar a Login //
                Servicio.salirDelServicio =false;
                servicio.eliminarToken();
                servicio.DesactivarAlarma();
                servicio.detenerRunnable();
                stopService(servicioIntent);
                Login.mAuth.signOut();
                Login.mAuth.removeAuthStateListener(Login.mAuthListener);
                Intent i = new Intent(SegundaVentana.this, Login.class);
                startActivity(i);
                finish();
            }
        });
        consultaNickname();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String pkg=getPackageName();
            PowerManager pm;
            pm = getSystemService(PowerManager.class);
            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                @SuppressLint("BatteryLife") Intent i =
                        new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                .setData(Uri.parse("package:" + pkg));
                Toast.makeText(this, "Agregando Aplicacion a Lista Blanca", Toast.LENGTH_SHORT).show();

                startActivity(i);
            }

        }




        if (!isMyServiceRunning(Servicio.class)){
            //**** si no hay servicio corriendo (FALSE) creará un nuevo Servicio **//

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                //**** si la version de android es 8 (Oreo) o mayor se ejecutara de esta manera el servicio **//

                startForegroundService(servicioIntent);
                Log.i("INICIAR SERVICIO", "PASO POR START FOREGROUND SERVICE PARA ANDROID >= 8");
            }else{
                //**** si la version de android es menor se ejecutara de esta manera el servicio **//

                startService(servicioIntent);
                Log.i("INICIAR SERVICIO", "PASO POR START SERVICE NORMAL");
            }


        }






    }
    //************************* FINAL ONCREATE ************************/

    public void consultaNickname() {
        String consultaNickname="https://ivanvidalsepulveda.000webhostapp.com/consultarNickname.php?email="+Login.USUARIOWEB;
        RequestQueue requestQueue = Volley.newRequestQueue(Login.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consultaNickname, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);

                       String nickname = CargarValorEstado(ja);
                       txtUsuario.setText(nickname);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }
    public static String CargarValorEstado(JSONArray ja) {

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




    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                //**** si hay un servicio ejecutando devuelve true para no crear mas servicios **//
                Log.i("ISMYSERVICERUNNING?", "SERVICIO ENCONTRADO");
                return true;
            }
        }
        //**** si no hay servicio devuelve false, para que se cree uno nuevo **//
        Log.i("ISMYSERVICERUNNING?", "SERVICIO NO ENCONTRADO");
        return false;
    }


    @Override
    protected void onDestroy() {
        Log.i("SEGUNDA VENTANA", "onDestroy!");
        super.onDestroy();


    }

}









