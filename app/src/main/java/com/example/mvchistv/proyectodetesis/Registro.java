package com.example.mvchistv.proyectodetesis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class Registro extends AppCompatActivity implements View.OnClickListener {
    EditText txtEmail,txtPassword,txtNicknme;
    Button btnRegistrar,btnVolver,btnRecuperar;
    static String email,password,nickname;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgress;
    Login login= new Login();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regristo);
        firebaseAuth = FirebaseAuth.getInstance();
        txtEmail =  findViewById(R.id.txtEmail);
        txtNicknme =  findViewById(R.id.txtNickname);
        txtPassword = findViewById(R.id.txtPassword);
        mProgress = new ProgressDialog(this);
        btnRecuperar=findViewById(R.id.rgtRecuperar);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registro.this,RecuperarEmail.class);
                startActivity(intent);
                finish();
            }
        });
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this);
        btnVolver=findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registro.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            email = txtEmail.getText().toString().trim();
            password = txtPassword.getText().toString().trim();
            nickname = txtNicknme.getText().toString().trim();

            //Verificamos que los textview no esten vacíos
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Se debe ingresar un Email", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Falta ingresar la Contraseña", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, "Falta ingresar Nickname", Toast.LENGTH_LONG).show();
                return;
            }


            mProgress.setMessage("Registrando, Un Momento...");
            mProgress.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {
                                Login.USUARIOWEB = email;
                                Login.consultarxUsuario();

                                Toast.makeText(Registro.this,
                                        "Registro de Cuenta Satisfactorio", Toast.LENGTH_SHORT).show();

                                finish();


                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(Registro.this, "Ese Usuario ya Existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registro.this, "No se pudo Registrar el Usuario ", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        }else {
            Toast.makeText(Registro.this, "No Estas conectado a Internet", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("REGISTRO", "REGISTRO ONDESTROY");


    }
}