package com.example.ubitrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class olvidocontrasena extends AppCompatActivity {
//creacion de variables
    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvidocontrasena);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Escriba el correo electrónico que tiene registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(olvidocontrasena.this, "Te hemos enviado instrucciones para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                                } else {

                                    errorAlConectar(task.getException().toString());
                                   // Toast.makeText(olvidocontrasena.this, "Error al enviar correo electrónico para reestablecer contraseña", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void errorAlConectar(String error){
        boolean ExisteCorreo = false ;
        if (error.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
            ExisteCorreo = true;
            // Toast.makeText(iniciar.this, "El CORREO YA SE ENCUENTRA REGISTRADO" , Toast.LENGTH_SHORT).show();
        }
        if (error.equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
            Toast.makeText(olvidocontrasena.this, "Verifica tu conexion a internet " , Toast.LENGTH_SHORT).show();
        } else if (ExisteCorreo == false){
            Toast.makeText(olvidocontrasena.this, "El correo que ingreso no existe" , Toast.LENGTH_SHORT).show();
        }
    }


}
