package com.example.mvchistv.proyectodetesis;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import static com.android.volley.VolleyLog.TAG;
import static com.example.mvchistv.proyectodetesis.Login.USUARIOWEB;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    RequestQueue requestQueue;
    String refreshedToken;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());

    }
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        if (refreshedToken!=null){
            if (!USUARIOWEB.equals("") && USUARIOWEB!=null) {

               cambiarToken();
               sendRegistrationToServer(refreshedToken);


            }
        }


    }
    public void cambiarToken(){

        String desactivarphp = "https://ivanvidalsepulveda.000webhostapp.com/insertarToken.php?email="+USUARIOWEB+"token="+refreshedToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, desactivarphp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length() > 0) {
                    try {

                        //** carga el nuevo token
                        Log.d("TOKEN","ACTUALIZANDO EL NUEVO TOKEN A LA BASE DE DATOS");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cambiarToken(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);


    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
