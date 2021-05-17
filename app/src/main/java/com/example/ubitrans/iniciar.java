package com.example.ubitrans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class iniciar extends AppCompatActivity {
    //creacion de variables
    private Button btnIngresar;
    private  Button btnOlvideCon;
    private Button btnRegistro;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private EditText Email, Password;
    boolean existeUsuario = false;
    private String recuperaid;
    private int gpsSeñal = 2;
    SharedPreferences prefe;
    DatabaseReference mRootReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //le decimos que la barra de arriba sea editable para despues mandarle un icono y
        //        //la flecha de rretroceso
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar);

        btnIngresar = (Button) findViewById(R.id.entrar);
        btnOlvideCon = (Button) findViewById(R.id.olvideContraseña);
        auth = FirebaseAuth.getInstance();
        Email = (EditText) findViewById(R.id.correo);
        Password = (EditText) findViewById(R.id.Contraseña);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnRegistro = findViewById(R.id.registrooo);
        mRootReference = FirebaseDatabase.getInstance().getReference();
        prefe = getSharedPreferences("DatosShared", Context.MODE_PRIVATE);
        Email.setText(prefe.getString("mail", ""));
        Password.setText(prefe.getString("pwd",""));
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                solicitarDatosFirebaseusuario();
                solicitarDatosFirebaseusuario();
                String email = Email.getText().toString();
                final String password = Password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "El Correo no existe", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "La contraseña no es correcta", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //autenticar usuario
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(iniciar.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    errorAlConectar(task.getException().toString());
                                       // Toast.makeText(iniciar.this, "Falló la autenticación, revise su correo electrónico y contraseña o regístrese", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    if(existeUsuario==true){

                                        prefe = getSharedPreferences("DatosShared", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefe.edit();
                                        editor.putString("mail",Email.getText().toString());
                                        editor.putString("pwd",Password.getText().toString());
                                        editor.commit();

                                    Toast.makeText(iniciar.this, "Usuario correcto ", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(iniciar.this, entrausuario.class);
                                    verificaSeñal();
                                    intent.putExtra("correo",Email.getText().toString());
                                    intent.putExtra("recuperaidusuario", recuperaid);
                                    startActivity(intent);



                                    finish();} else {
                                        Toast.makeText(iniciar.this, "Este correo no se encuentra registrado como usuario ", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });

            }
        });

///evento de botones cuando dan clik
        btnOlvideCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iniciar.this, olvidocontrasena.class));
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos un nuevo item para mostrar la otra pantalla
                Intent intent = new Intent(iniciar.this, registro.class);
                startActivity(intent);

            }
        });


    }


    //metodo para recojer errores al entrar y salir de este activity

    private void errorAlConectar(String error){
boolean ExisteCorreo = false ;
        if (error.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
            ExisteCorreo = true;
            // Toast.makeText(iniciar.this, "El CORREO YA SE ENCUENTRA REGISTRADO" , Toast.LENGTH_SHORT).show();
        }

        if (error.equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
            Toast.makeText(iniciar.this, "Verifica tu conexion a internet " , Toast.LENGTH_SHORT).show();
        } else if (ExisteCorreo = false){
            Toast.makeText(iniciar.this, "El correo que ingreso no existe" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(iniciar.this, "la contraseña es incorrecta" , Toast.LENGTH_SHORT).show();}
    }


    //consultar datos en la firebase

    private void solicitarDatosFirebaseusuario() {
        mRootReference.child("usuario").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> mensajes = new ArrayList<String>();
                final List<String> names = new ArrayList<String>();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){

                    mRootReference.child("usuario").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            datosusuario user = snapshot.getValue(datosusuario.class);
                            // String nombre = user.getNombre();
                            String email = user.getEmail();
                            String recupereCorreo =  Email.getText().toString();
                            if(recupereCorreo.equals(email)){
                                existeUsuario=true;
                                recuperaid = user.getId();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }









//verificar que este prendodo el gps
    public void verificaSeñal(){


        try {
            gpsSeñal = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }

        if (gpsSeñal==2){
            Toast.makeText(iniciar.this, "No tiene el GPS Activado" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(iniciar.this, "Deves ensender el GPS para continuar" ,Toast.LENGTH_SHORT).show();
            informacionGPS ();
        } else if (gpsSeñal==0)
        {
            Toast.makeText(iniciar.this, "No tiene el GPS Activado" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(iniciar.this, "Deves ensender el GPS para continuar" ,Toast.LENGTH_SHORT).show();
            informacionGPS ();
        }
       // Toast.makeText(iniciar.this, "el gps es igual a " +  gpsSeñal ,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(Mapa.this, "valor de gpsSeñal = " +  gpsSeñal ,Toast.LENGTH_SHORT).show();

    }
    //metodo para llamar a metodo
    private void informacionGPS (){
        cargarDialogoRecomendacion();
    }
    //metodo con el cual creamos un cuadro de texto
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(iniciar.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe encender  el GPS para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialogo.setNegativeButton("CANCELAR",null);
        dialogo.show();
    }

    @Override
    public void onStart() {
        verificaSeñal();

        super.onStart();
    }


}
