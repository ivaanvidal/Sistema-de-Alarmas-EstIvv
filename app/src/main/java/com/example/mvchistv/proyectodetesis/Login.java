package com.example.mvchistv.proyectodetesis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    static Login instance=null;
    Button btnIngresar, btnSalir,btnRegistro;
    EditText txtUsu, txtPass;
    public static String USUARIOWEB="";
    public static String CONTRASENAWEB ="";
    public static String NICKNAME="";
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;






    @Override
    protected void onStart() {
        super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        instance = this;
        txtUsu= findViewById(R.id.txtusuario);
        txtPass= findViewById(R.id.txtpassword);
        mAuth = FirebaseAuth.getInstance();
        btnIngresar=findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(this);
        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            //********************* Boton para Salir de la App  ******************/
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null){
                    mAuth.signOut();
                    mAuth.removeAuthStateListener(mAuthListener);}
                    finish();
            }
        });

        progressDialog=new ProgressDialog(this);

        cargarUsuario();

        txtUsu.setText(USUARIOWEB);
        txtPass.setText(CONTRASENAWEB);
        btnRegistro=findViewById(R.id.btnRegistrar);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Registro.class);
                startActivity(intent);
                finish();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    //IDUSARIOWEB=firebaseAuth.getCurrentUser().getUid();
                    Intent intent = new Intent(Login.this, SegundaVentana.class);
                    startActivity(intent);
                    finish();

                }
            }
        };

    }

    //*********************** Fin OnCreate  ******************/



    @Override
    public void onClick(View v) {
        //****** Boton Ingresar  *********/

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            String email = txtUsu.getText().toString();
            String password = txtPass.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.setMessage("Iniciando Sesión...");
                                progressDialog.show();
                                guardarUsuario();
                                consultarxUsuario();

                            } else {
                                Toast.makeText(Login.this, "Usuario o Contraseña Incorrectos", Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();
                        }
                    });

        }else {
            Toast.makeText(Login.this, "No Estas conectado a Internet", Toast.LENGTH_SHORT).show();
        }
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

    public static void consultarxUsuario(){
        Log.i("MYSQL", "CONSULTANDO USUARIO");
        RequestQueue requestQueue = Volley.newRequestQueue(Login.getAppContext());
        String consultarusuario = "https://ivanvidalsepulveda.000webhostapp.com/consultarUsuario.php?email="+USUARIOWEB;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consultarusuario, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray ja = new JSONArray(response);
                    String resultadoUsuario=CargarValorEstado(ja);
                    //** carga el valor del estado que será "0"
                   if (resultadoUsuario.equals(USUARIOWEB)){
                       Log.i("MYSQL", "USUARIO ENCONTRADO");
                       actualizarDatosMysql();
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("MYSQL", "USUARIO NO ENCONTRADO EN LA BD");
                    crearDatosMysql();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                consultarxUsuario(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);


    }



    public static void crearDatosMysql(){
        RequestQueue requestQueue = Volley.newRequestQueue(Login.getAppContext());
        String creardatos = "https://ivanvidalsepulveda.000webhostapp.com/insertarDatos.php?email="+USUARIOWEB+"&nickname="+Registro.nickname+"&estado=0&token="+FirebaseInstanceId.getInstance().getToken();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, creardatos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("MYSQL", "DATOS CREADOS EN BD MYSQL");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                crearDatosMysql(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);

    }

    public static void actualizarDatosMysql(){
        RequestQueue requestQueue = Volley.newRequestQueue(Login.getAppContext());
        String actualizardatos = "https://ivanvidalsepulveda.000webhostapp.com/actualizarDatos.php?email="+USUARIOWEB+"&estado=0&token="+FirebaseInstanceId.getInstance().getToken();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, actualizardatos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("MYSQL", "DATOS ACTUALIZADOS EN BD MYSQL");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               actualizarDatosMysql(); //** si hay error del volley o no hay internet se autoejecuta otra vez
            }
        });
        requestQueue.add(stringRequest);

    }





    private void guardarUsuario(){
        //********* Guarda el Usuario y Contraseña Ingresados  ********/

        SharedPreferences preferences=getSharedPreferences("credenciales",Context.MODE_PRIVATE);
        SharedPreferences preferences2=getSharedPreferences("contraseña",Context.MODE_PRIVATE);
        SharedPreferences preferences3=getSharedPreferences("broadcast",Context.MODE_PRIVATE);

        String usuarioweb=txtUsu.getText().toString();
        String password=txtPass.getText().toString();
        String nickname=NICKNAME;

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("user",usuarioweb);
        SharedPreferences.Editor editor1=preferences2.edit();
        editor1.putString("password",password);
        SharedPreferences.Editor editor2=preferences3.edit();
        editor2.putString("nickname",nickname);

        USUARIOWEB=usuarioweb;
        CONTRASENAWEB = password;
        NICKNAME=nickname;
        editor.apply();
        editor1.apply();
        editor2.apply();



    }


    private void cargarUsuario() {
        //********* Carga el Usuario y Contraseña Ingresados Anteriormente y  *******/

        SharedPreferences preferences;
        SharedPreferences preferences2;
        SharedPreferences preferences3;
        preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        preferences2 = getSharedPreferences("contraseña", Context.MODE_PRIVATE);
        preferences3 = getSharedPreferences("broadcast", Context.MODE_PRIVATE);

        USUARIOWEB= preferences.getString("user","");
        CONTRASENAWEB = preferences2.getString("password","");
        NICKNAME=preferences3.getString("nickname","");


    }

    public static Login getsInstance(){
        return instance;
    }
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
    }
    public static Context getAppContext(){
        return getsInstance().getApplicationContext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("ACTIVIDAD LOGIN", "onDestroy!");

    }
}
