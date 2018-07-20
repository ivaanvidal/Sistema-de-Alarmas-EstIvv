package com.example.mvchistv.proyectodetesis;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarEmail extends AppCompatActivity {
    Button salirApp,volver,recuperar;
    EditText recuperarEmail;
    private FirebaseAuth firebaseAuth;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_email);
        recuperarEmail=findViewById(R.id.txtRecuperarEmail);
        salirApp=findViewById(R.id.rcpSalir);
        volver=findViewById(R.id.rcpVolver);
        recuperar=findViewById(R.id.rcpRecuperar);
        firebaseAuth = FirebaseAuth.getInstance();

        salirApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecuperarEmail.this,Registro.class);
                startActivity(intent);
                finish();
            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                assert connMgr != null;
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    email = recuperarEmail.getText().toString().trim();
                    //Verificamos que los textview no esten vacíos
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(RecuperarEmail.this, "Se debe ingresar un Email", Toast.LENGTH_LONG).show();
                        return;
                    }

                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RecuperarEmail.this,
                                                "Correo para Restablecer Contraseña Enviado ", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RecuperarEmail.this, Registro.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RecuperarEmail.this,
                                                "Este Correo no Existe ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(RecuperarEmail.this, "No Estas conectado a Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });






    }
}
