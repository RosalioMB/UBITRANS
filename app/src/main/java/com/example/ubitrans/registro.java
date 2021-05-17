package com.example.ubitrans;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.util.Patterns;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class registro extends AppCompatActivity {
    private Button btnAgregar;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private EditText Email, Password, Nombre, verificaContraseña, Tel;
    private int permisodelgps = 0 ;
    private String milatitud;
    private  String milongitud;
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference mRootReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRootReference = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        auth = FirebaseAuth.getInstance();
        btnAgregar = findViewById(R.id.registrar);
        Email = (EditText) findViewById(R.id.correo);
        Password = (EditText) findViewById(R.id.Contraseña);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Nombre = (EditText) findViewById(R.id.NombreCompleto);
        verificaContraseña = (EditText) findViewById(R.id.verificaContraseña);
        Tel = (EditText) findViewById(R.id.NumeroDeTelefono);





        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String con1 = Password.getText().toString();
                String nombre = Nombre.getText().toString();
                String telefono = Tel.getText().toString();
                String VeriCont = verificaContraseña.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Correo electronico no es correcto ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(telefono)) {
                    Toast.makeText(getApplicationContext(), "El numero de telefono no esta correcto", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "La contraseña no es correcta ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() <= 7) {
                    Toast.makeText(getApplicationContext(), "La contraseña  no puede ser demasiado corto, minimo 8 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Debes escribir un correo valido ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.PHONE.matcher(telefono).matches()) {
                    Toast.makeText(getApplicationContext(), "Debes escribir un telefono celular correcto ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (telefono.length()!=10) {
                    Toast.makeText(getApplicationContext(), "Debes escribir un telefono celular con 10 digitos", Toast.LENGTH_SHORT).show();
                    return;
                }







                if (!con1.equals(VeriCont)) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinsiden ", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //crear usuario en Firebase

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(registro.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(registro.this, "SE ESTA CREANDO EL USUARIO",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            if (task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                Toast.makeText(registro.this, "El CORREO YA SE ENCUENTRA REGISTRADO" , Toast.LENGTH_SHORT).show();
                            } else if (task.getException().toString().equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                Toast.makeText(registro.this, "Verifica tu conexion a internet " , Toast.LENGTH_SHORT).show();
                            } else {
                            Toast.makeText(registro.this, "Fallo al crear el usuario, Intentalo nuevamente " , Toast.LENGTH_SHORT).show();}
                        } else {
                            Toast.makeText(registro.this, "Usuario Creado Correctamente" ,Toast.LENGTH_SHORT).show();
                            String nombre = Nombre.getText().toString();
                            String email = Email.getText().toString();
                            String telefono = Tel.getText().toString();
                            cargarDatosFirebase(nombre, email, telefono);
                            startActivity(new Intent(registro.this, registro.class));
                            finish();
                        }
                    }
                });
                //hasta aqui bien
            }






        });

    }

    ///metodo para subir datos a la base de datos
    public void cargarDatosFirebase(String nombre, String email, String telefono) {
        obtenerdireccioregistro();
        Map<String, Object> datosUsuario = new HashMap<>();
        String id = mRootReference.push().getKey();
        datosUsuario.put("email", email);
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("latitud", milatitud);
        datosUsuario.put("longitud", milongitud);
        datosUsuario.put("id", id);
       // mRootReference.child("usuario").push().setValue(datosUsuario);
        mRootReference.child("usuario").child(id).setValue(datosUsuario);
    }










    private void obtenerdireccioregistro() {
        //antes verifica que el permiso este activo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(registro.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    permisodelgps);
            return;
        }
        //toma la localizacion del chofer
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            milatitud = location.getLatitude()+"";
                            milongitud = location.getLongitude()+"";
                            // Toast.makeText(getApplication(), "El id es " + id , Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}
