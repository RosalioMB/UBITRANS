package com.example.ubitrans;

import android.Manifest;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class entrachofer extends AppCompatActivity {
    //creacion de variables
    private Button btnMapa;
    private Button btnenviar;
    private ListView ListaMensj;
    private EditText mensajee;
    private String User;
   int permisodelgps;
    DatabaseReference mRootReference;
    DatabaseReference ReferenceMensaje;
    DatabaseReference idchofer;
   // private FusedLocationProviderClient mFusedLocationChofer;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //le decimos que la barra de arriba sea editable para despues mandarle un icono y
        //la flecha de rretroceso
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Toast.makeText(getApplicationContext(), "El correo electronico es : " + recupereCorreo , Toast.LENGTH_SHORT).show();
        //asi se crean las referencias a la base de datos
        mRootReference = FirebaseDatabase.getInstance().getReference();
        ReferenceMensaje = FirebaseDatabase.getInstance().getReference();
        //igualando los botones con los del activity
        setContentView(R.layout.activity_entrachofer);
        btnMapa = findViewById(R.id.mapa);
        btnenviar = findViewById(R.id.buttonenviar);
        mensajee = findViewById(R.id.editMensaje);
        ListaMensj = findViewById(R.id.ListaMensajes);
        solicitarDatosFirebaseusuario();


        solicitarDatosFirebase();
        //cracion de eventos cuando se le de clik a un boton
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos un nuevo item para mostrar la otra pantalla
                Intent intent = new Intent(entrachofer.this, Mapa.class);
                intent.putExtra("quesoy","soychofer");
                String id = getIntent().getStringExtra("recuperaid");
                intent.putExtra("recuperaid", id);
                startActivity(intent);
            }
        });

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaj3 = mensajee.getText().toString();
                cargarDatosFirebase(User, mensaj3);
                mensajee.setText("");

            }
        });

        actualizarsudireccion();
        // MyAdapter myAdapter = new MyAdapter(this, R.layout.interfasmensajes, names, mensajes);
        // ListaMensj.setAdapter(myAdapter);
    }
//metodo para actualizar la direccion del chofer
    private void actualizarsudireccion(){

                obtenerdireccionchofer();
    }
//metodo para obtener su direccion
    private void obtenerdireccionchofer() {
        //antes verifica que el permiso este activo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(entrachofer.this,
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
                            String id = getIntent().getStringExtra("recuperaid");
                            Map<String,Object> latlang = new HashMap<>();
                            latlang.put("latitud","");
                            latlang.put("longitud","");
                            mRootReference.child("chofer").child(id).updateChildren(latlang);
                           // Toast.makeText(getApplication(), "El id es " + id , Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
//metodo para subir datos a la firebase
    public void cargarDatosFirebase(String nombre, String mensaje) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", User);
        datosUsuario.put("mensaje", mensaje);
        mRootReference.child("mensajes").push().setValue(datosUsuario);
        solicitarDatosFirebase();
    }
//metodo para solisitar informacion a la base de datos
    private void solicitarDatosFirebase() {
        mRootReference.child("mensajes").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> mensajes = new ArrayList<String>();
                final List<String> names = new ArrayList<String>();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){

                    mRootReference.child("mensajes").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mensaje2 user = snapshot.getValue(mensaje2.class);

                            String nombre = user.getNombre();
                            String mensaje = user.getMensaje();
                            // Toast.makeText(getApplicationContext(), "El usuario es " + nombre, Toast.LENGTH_SHORT).show();
                            mensajes.add(mensaje);
                            names.add(nombre);
                            //Log.e("NombreUsuario:",""+nombre);
                            // Log.e("ApellidoUsuario:",""+mensaje);
                            //Log.e("Datos:",""+snapshot.getValue());
                            MyAdapter myAdapter = new MyAdapter(entrachofer.this, R.layout.interfasmensajes, names, mensajes);
                            ListaMensj.setAdapter(myAdapter);
                            ListaMensj.setSelection(ListaMensj.getCount() - 1);
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


    private void quitardebasededatos(){
        String dondeentrar;
        dondeentrar = getIntent().getStringExtra("quesoy");
        String id = getIntent().getStringExtra("recuperaid");
        Map<String,Object> latlang = new HashMap<>();
        latlang.put("latitud","");
        latlang.put("longitud","");
            mRootReference.child("chofer").child(id).updateChildren(latlang);
    }



    private void solicitarDatosFirebaseusuario() {
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
                            String nombre = user.getApodo();
                            String email = user.getEmail();
                            Bundle recuperaCorreo = getIntent().getExtras();
                            String recupereCorreo = recuperaCorreo.getString("correochofer");
                            if(recupereCorreo.equals(email)){
                                User = "chofer - " + nombre + ":";
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

    @Override
    public void onStart() {
        quitardebasededatos();
        super.onStart();
    }


}