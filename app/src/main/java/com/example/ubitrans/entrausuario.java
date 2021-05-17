package com.example.ubitrans;

import android.Manifest;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class entrausuario extends AppCompatActivity {
    //cracion de bariables
    private static final String TAG = "entrausuario";
    private Button btnMapa;
    private Button btnenviar;
    private ListView ListaMensj;
    private EditText mensajee;
    private String User;
    DatabaseReference mRootReference;
    DatabaseReference ReferenceMensaje;
    int permisodelgps;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  //le decimos que la barra de arriba sea editable para despues mandarle un icono y
        //        //la flecha de rretroceso
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        //variable con la que guardamos nuestra localizacion
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //esta linea de codigo funciona para que no se muevan los componentes
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);

        obtenerdireccionusuario();

        //Toast.makeText(getApplicationContext(), "El correo electronico es : " + recupereCorreo , Toast.LENGTH_SHORT).show();
        mRootReference = FirebaseDatabase.getInstance().getReference();
        ReferenceMensaje = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_entrausuario);
        btnMapa = findViewById(R.id.mapa);
        ListaMensj = findViewById(R.id.ListaMensajes);
        solicitarDatosFirebaseusuario();


        solicitarDatosFirebase();

        //evento de cuando precionamos un boton
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos un nuevo item para mostrar la otra pantalla
                Intent intent = new Intent(entrausuario.this, Mapa.class);
                intent.putExtra("quesoy","soyusuario");
                String id = getIntent().getStringExtra("recuperaidusuario");
                intent.putExtra("recuperaid",id);
                startActivity(intent);
            }
        });
    }


    public void cargarDatosFirebase(String nombre, String mensaje) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", User);
        datosUsuario.put("mensaje", mensaje);
        mRootReference.child("mensajes").push().setValue(datosUsuario);
        solicitarDatosFirebase();
    }

    //este metod sirbe para recojer los mensajes que an mandado los choferes
    //y que los usuarios puedan visualizarlos
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
                            MyAdapter myAdapter = new MyAdapter(entrausuario.this, R.layout.interfasmensajes, names, mensajes);
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

    private void obtenerdireccionusuario() {
        //antes verifica que el permiso este activo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(entrausuario.this,
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
                            String id = getIntent().getStringExtra("recuperaidusuario");
                            Map<String,Object> latlang = new HashMap<>();
                            latlang.put("latitud","");
                            latlang.put("longitud","");
                            mRootReference.child("usuario").child(id).updateChildren(latlang);
                            // Toast.makeText(getApplication(), "El id es " + id , Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }




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

                            String nombre = user.getNombre();
                            String email = user.getEmail();
                            Bundle recuperaCorreo = getIntent().getExtras();
                            String recupereCorreo = recuperaCorreo.getString("correo");
                            if(recupereCorreo.equals(email)){
                                User = nombre;
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



}