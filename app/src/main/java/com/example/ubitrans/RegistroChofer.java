package com.example.ubitrans;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class RegistroChofer extends AppCompatActivity {
Spinner opciones;
String tipoTransporte;
EditText nombre;
    EditText apodo;
    EditText numero;
    EditText email;
    private ProgressBar progressBar;
    EditText  Passwordd ;
    EditText  Passwordd1 ;
    Button registrachofer;
    private int permisodelgps = 0 ;
    private String milatitud;
    private  String milongitud;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth auth;
    DatabaseReference mRootReference;
    DatabaseReference idchofer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRootReference = FirebaseDatabase.getInstance().getReference();
        idchofer = FirebaseDatabase.getInstance().getReference("chofer");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_chofer);
        opciones = (Spinner) findViewById(R.id.marca);
        nombre = (EditText) findViewById(R.id.Nombre);
        apodo = (EditText) findViewById(R.id.apodo);
        numero = (EditText) findViewById(R.id.telchofer);
        Passwordd = (EditText) findViewById(R.id.contraseña);
        Passwordd1 = (EditText) findViewById(R.id.verificaContraseña);
        email = (EditText) findViewById(R.id.correochofer);
        registrachofer = (Button) findViewById(R.id.buttonregistrar);
        progressBar = (ProgressBar) findViewById(R.id.progressBarr);

        //asi para poder registrar


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegistroChofer.this, R.array.Opciones, android.R.layout.simple_spinner_item);
        opciones.setAdapter(adapter);

        opciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(RegistroChofer.this, "el item seleccionado es: " + opciones.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                tipoTransporte = opciones.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        registrachofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           String nombree = nombre.getText().toString();
           String apodoo = apodo.getText().toString();
           String numeroo = numero.getText().toString();
           String emaill = email.getText().toString();
           String VerificaCon = Passwordd1.getText().toString();
           String seleccionoo = tipoTransporte;
                errores(seleccionoo,nombree,apodoo,numeroo,emaill,VerificaCon);
            }
        });


    }




    public void errores(String opciones, final String nombre, final String apodo, final String numero, final String email, final String VerificaContraseña){
        int errorr = 0;
        String password1 = Passwordd.getText().toString();
        if (opciones.equals("Selecciona")){
            Toast.makeText(RegistroChofer.this, "Selecciona un tipo de transporte  ", Toast.LENGTH_LONG).show();
            errorr++;
        }
        if (password1.length()<6){
            Toast.makeText(RegistroChofer.this, "La contraseña debe de tener al menos 6 caracteres ", Toast.LENGTH_LONG).show();
            errorr++;
        }
        if (nombre.length() < 2 || apodo.length() < 2 ){
            Toast.makeText(RegistroChofer.this, "Verifica que todos los campos esten llenados correctamente  ", Toast.LENGTH_LONG).show();
            errorr++;
        }
        if (nombre.length() < 14 ){
            Toast.makeText(RegistroChofer.this, "Verifica que el nombre tenga apellidos ", Toast.LENGTH_LONG).show();
            errorr++;
        }
        if ( numero.length() != 10){
            Toast.makeText(RegistroChofer.this, "Verifica que el numero tenga 10 digitos ", Toast.LENGTH_LONG).show();
            errorr++;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Debes escribir un correo valido ", Toast.LENGTH_SHORT).show();
            errorr++;
        }


        if (!password1.equals(VerificaContraseña))
        {
            Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden ", Toast.LENGTH_SHORT).show();
            errorr++;
        }

        if (errorr ==0){


            String emailtrim = this.email.getText().toString().trim();
            String password = Passwordd.getText().toString().trim();


            progressBar.setVisibility(View.VISIBLE);

            //crear usuario en Firebase

            auth.createUserWithEmailAndPassword(emailtrim, password).addOnCompleteListener(RegistroChofer.this, new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(RegistroChofer.this, "SE ESTA CREANDO EL USUARIO",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        if (task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                            Toast.makeText(RegistroChofer.this, "El CORREO YA SE ENCUENTRA REGISTRADO" , Toast.LENGTH_SHORT).show();

                        } else if (task.getException().toString().equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                            Toast.makeText(RegistroChofer.this, "Verifica tu conexion a internet " , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistroChofer.this, "Fallo al crear el usuario, Intentalo nuevamente " , Toast.LENGTH_SHORT).show();}
                    } else {
                        Toast.makeText(RegistroChofer.this, "Usuario Creado Correctamente" ,Toast.LENGTH_SHORT).show();

                        String nombree = nombre;
                        String apodoo = apodo;
                        String numeroo = numero;
                        String emaill = email;
                        String seleccionoo = tipoTransporte;

                        cargarDatosFirebase(seleccionoo, nombree, apodoo, numeroo, emaill);
                        startActivity(new Intent(RegistroChofer.this, RegistroChofer.class));
                        finish();
                    }
                }
            });
        }

    }

    public void cargarDatosFirebase(String opciones, String nombre,String apodo,  String telefono, String email) {
        Map<String, Object> datoschoferr = new HashMap<>();
        String id = idchofer.push().getKey();
        String mil=milatitud;
        String milon=milongitud;
        datoschoferr.put("id", id);
        datoschoferr.put("tipodetransporte", opciones);
        datoschoferr.put("nombre", nombre);
        datoschoferr.put("apodo", apodo);
        datoschoferr.put("telefono", telefono);
        datoschoferr.put("email", email);
        datoschoferr.put("latitud", mil);
        datoschoferr.put("longitud", milon);
        //idchofer.child("chofer").push().setValue(datosUsuario);
        idchofer.child(id).setValue(datoschoferr);
    }


    private void obtenerdireccioregistro() {
        //antes verifica que el permiso este activo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegistroChofer.this,
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
