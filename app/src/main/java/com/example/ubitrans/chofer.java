package com.example.ubitrans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class chofer extends AppCompatActivity {
    //creacion de variables
private Button chofer;
private Button Ingresar;
private Button olvideContraseña;
private ProgressBar progressBar;
private EditText Email;
private FirebaseAuth auth;
private EditText Password;
private int gpsSeñal = 2;
DatabaseReference mRootReference;
String recuperaid;
private boolean choferExiste = false;
    SharedPreferences prefe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true); //con esta parte le decimos qje la parte de arriva sea editable
        getSupportActionBar().setIcon(R.mipmap.ic_myicon); //con esta ponemos el icono en la parte de arriba
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //con esta parte le damos que aparesca la flechita en la parte de arriba
        super.onCreate(savedInstanceState);
        //igualacion de njestras variables con las del activity
        setContentView(R.layout.activity_chofer);
        chofer = findViewById(R.id.rchofer);
        Ingresar = findViewById(R.id.entrar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Email = (EditText) findViewById(R.id.correo);
        olvideContraseña = (Button) findViewById(R.id.buttonolvidecontraseña);
        Password = (EditText) findViewById(R.id.contraseña);
        auth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBarrr);
        mRootReference = FirebaseDatabase.getInstance().getReference();
        prefe = getSharedPreferences("DatosShare", Context.MODE_PRIVATE);
        Email.setText(prefe.getString("mail", ""));
        Password.setText(prefe.getString("pwd",""));
        //evento de dar clik al boton chofer
        chofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chofer.this, RegistroChofer.class);
                startActivity(intent);
            }
        });
          //evento de dar clik al boton olvidecontraseña
        olvideContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(chofer.this, olvidocontrasena.class));
            }
        });
        //evento de dar clik al boton ingresar
        Ingresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //finish();
                solicitarDatosFirebasechofer();
                solicitarDatosFirebasechofer();

                String email = Email.getText().toString();
                final String password = Password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    //asi envia,os un mensaje de advertencia al usuario dependiendo del error
                    Toast.makeText(getApplicationContext(), "El Correo no existe", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "La contraseña no es correcta", Toast.LENGTH_SHORT).show();
                    return;
                }
               //mostramos un grafico donde semuestra que esta cargando
                progressBar.setVisibility(View.VISIBLE);

                //autenticar usuario
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(chofer.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    errorAlConectar(task.getException().toString());
                                    // Toast.makeText(iniciar.this, "Falló la autenticación, revise su correo electrónico y contraseña o regístrese", Toast.LENGTH_LONG).show();
                                }
                                else{
                                  //  Toast.makeText(chofer.this, "Usuario correcto ", Toast.LENGTH_LONG).show();
                                    if(choferExiste==true){

                                        //asi mandamos a abrir un nueva ventana
                                        prefe = getSharedPreferences("DatosShare", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefe.edit();
                                        editor.putString("mail",Email.getText().toString());
                                        editor.putString("pwd",Password.getText().toString());
                                        editor.commit();
                                    Intent intent = new Intent(chofer.this, entrachofer.class);
                                    intent.putExtra("recuperaid", recuperaid );
                                    intent.putExtra("correochofer",Email.getText().toString());
                                    startActivity(intent);

                                    finish();} else if (choferExiste==false) {
                                        Toast.makeText(getApplicationContext(), "EL CHOFER NO EXISTE", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });
    }
  ///metodo para detectar que error ocurrio antes de entrar a la base de datos
    private void errorAlConectar(String error){
        boolean ExisteCorreo = false ;
        if (error.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
            ExisteCorreo = true;
            // Toast.makeText(iniciar.this, "El CORREO YA SE ENCUENTRA REGISTRADO" , Toast.LENGTH_SHORT).show();
        }

        if (error.equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
            Toast.makeText(chofer.this, "Verifica tu conexion a internet " , Toast.LENGTH_SHORT).show();
        } else if (ExisteCorreo = false){
            Toast.makeText(chofer.this, "El correo que ingreso no existe" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(chofer.this, "la contraseña es incorrecta" , Toast.LENGTH_SHORT).show();}
    }
    //metodo par pedirle valores a la base de datos

    private void solicitarDatosFirebasechofer() {
        mRootReference.child("chofer").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> mensajes = new ArrayList<String>();
                final List<String> names = new ArrayList<String>();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){

                    mRootReference.child("chofer").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            datoschofer user = snapshot.getValue(datoschofer.class);

                           // String nombre = user.getNombre();
                            String email = user.getEmail();
                            String recupereCorreo =  Email.getText().toString();

                            if(recupereCorreo.equals(email)){
                                choferExiste=true;
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


    //metodo para verificar señal del gps
    public void verificaSeñal(){


        try {
            gpsSeñal = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }

        if (gpsSeñal==2){
            Toast.makeText(chofer.this, "No tiene el GPS Activado" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(chofer.this, "Deves ensender el GPS para continuar" ,Toast.LENGTH_SHORT).show();
            informacionGPS ();

        } else if (gpsSeñal==0)
        {
            Toast.makeText(chofer.this, "No tiene el GPS Activado" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(chofer.this, "Deves ensender el GPS para continuar" ,Toast.LENGTH_SHORT).show();
            informacionGPS ();
        }

        //  Toast.makeText(Mapa.this, "valor de gpsSeñal = " +  gpsSeñal ,Toast.LENGTH_SHORT).show();

    }
    //metodo para lanzar informacion
    private void informacionGPS (){
        cargarDialogoRecomendacion();
    }

    //metodo para crear un cuadro de dialogo de recomendaciones al chofer
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(chofer.this);
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
    protected void onResume() {
        //lo que hacems cuando esta en resumen la aplicacion
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onStart() {
        //lo primero que hacemos al entrar a la aplicacion
        verificaSeñal();
        super.onStart();
    }
}
