package com.example.ubitrans;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, View.OnClickListener {
    //creacion de variables
    private MapView mapView;
    private GoogleMap mMap;
    private boolean secreopolilinea = false;
    private Geocoder recojeInformacion;
    private Address direccion;
    private boolean queentre = true;
    private String time;
    private String milatitud = "";
    private String milongitud = "";
    private String miruta;
    private String rutadestino;
    int contadordelregreso = 0;
    private FloatingActionButton btnFlotente;
    private int gpsSeñal = 0;
    private GoogleMap gMap;
    private Polyline polylinee;
    private String aceptolatirud;
    private  String aceptolongitud;
    private ArrayList<Marker> marcadoresnuevos = new ArrayList<>();
    private ArrayList<Marker> marcadoresviejos = new ArrayList<>();
    private int jugarconlogica = 0;
    DatabaseReference mRootReference;
    JSONObject jso;
    private LocationManager locationManagerr;
    private Location locagps;

    private FusedLocationProviderClient fusedLocationClient;
    private Gson gson;
    private int permisodelgps = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        estarsubiendodatos();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRootReference = FirebaseDatabase.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);

        mapView = (MapView) findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //mmetodo que captura cuando se inicia

        String dondeentrar;
        dondeentrar = getIntent().getStringExtra("quesoy");
        mMap = googleMap;
        locationManagerr = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMap.setMinZoomPreference(5); //le decimos cuanto es el zumm minimo
        mMap.setMaxZoomPreference(15); //le decimos cuando es el zoom maximo del mapa
        LatLng sydney = new LatLng(20.3548134, -102.7804485); //mandamos cordenadas para la camara
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)); //le mandamos a la camara
        estarsubiendodatos();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cargarDialogoRecomendacion();
            return;
        }
        mMap.setMyLocationEnabled(true);
       // mMap.getUiSettings().setMyLocationButtonEnabled(false);
locationManagerr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
});
        // Add a marker in Sydney and move the camera
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(miposicion));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        crearmarcadores(dondeentrar); //aqui se crean los marcadores
        googleMap.setOnMarkerDragListener(this);
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) { //que hacer cuando le den clik a una polilinea del mapa
                Toast.makeText(Mapa.this, "el tiempo de llegada es de " + time ,Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) { //metodo cuando le dan clik a un marcador

                marker.showInfoWindow();
                String nombre = marker.getTitle();
                String datos = marker.getSnippet();
                LatLng latLng1 = marker.getPosition();
                evitartrazarrutacuandousuarionoquiere(latLng1.latitude+"",latLng1.longitude+"",nombre,datos);

                return false;
            }
        });



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            //cuando damos un clik al mapa
            @Override
            public void onMapClick(final LatLng latLng) {
               // Toast.makeText(Mapa.this, "dando clik" ,Toast.LENGTH_SHORT).show();
                verificaSeñal();

            }
        });
            // cuando damos un clik largo al mapa
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {


            }
        });
    }

    //metodo cuando empezamos a arrastrar el puntero
    @Override
    public void onMarkerDragStart(Marker marker) {

    }
   //metodo cuando estamos arrastrando el puntero
    @Override
    public void onMarkerDrag(Marker marker) {

    }
    //metodo cuando dejamos de  arrastrar el puntero
    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
    @Override
    public void onBackPressed() {
        // int contadordelregreso = 0;
        if (contadordelregreso==0){
            quehacercuandodepresionaregresar();
        }else {
             queentre = false;
            quitardebasededatos();
            finish();
            super.onBackPressed();
        }
    }
    public void verificaSeñal(){


        try {
            gpsSeñal = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }

        if (gpsSeñal==2){
            Toast.makeText(Mapa.this, "No tiene el GPS Activado" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(Mapa.this, "Deves ensender el GPS para continuar" ,Toast.LENGTH_SHORT).show();
            informacionGPS ();

        }

      //  Toast.makeText(Mapa.this, "valor de gpsSeñal = " +  gpsSeñal ,Toast.LENGTH_SHORT).show();

    }
   private void informacionGPS (){
       cargarDialogoRecomendacion();
   }


   ///metodo que recibe varios parametros
    //con los cuale hace diferentes cosas
    //dependiendo que seleccione el usuario
private void evitartrazarrutacuandousuarionoquier(final String latitud, final String longitud, final String nombreee, final String datosss){
    AlertDialog.Builder dialogo=new AlertDialog.Builder(Mapa.this);
    dialogo.setTitle("Presionastes un marcador");
    dialogo.setMessage("¿deceas realizar una petision?" +
            "Si seleccionaste un marcador diferente" +
            "se te trazara la ruta automaticamente al nuevo destino");
    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            jugarconlogica = 1;
            rutadestino = latitud+","+longitud;
            actualizarmiscordenadas();
            quehacercuandodeclikaunmarcador();
        }
    });



    dialogo.setNegativeButton("VER DATOS", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int j) {
            mostrardatosdelchofertodos( nombreee,  datosss);
        }
    });
    dialogo.show();
}



//este metodo es con el que creo los markers con base a la latitud y longitud de la base de datos
 private void crearmarcadores(String depende){

        if(depende.equals("soychofer")) { //solo es para saber si vengo del activity chofer
            //ago referencia a la base de datos del hijo usuario el listener es para
            //estar escuchando siempre a la base de datos
            mRootReference.child("usuario").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { //este metodo sirbe para escuchar cualquier actualizacion de
                    //la base de datos
                    for (Marker marker : marcadoresviejos) {  //para que no se amontonen los markers en el proyecto y se ensimen borro
                        ///todos los marcadores
                        marker.remove();
                    }

                    //realizo un recorrido por todos los hijos que tenga el hijo que espesificamos en la
                    //referencia de la base de datos LINEA 298
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            //nececitas una clase java para poder traer lo de la base de datos
                            //en mi caso se llama datos usuario
                            datosusuario du = snapshot.getValue(datosusuario.class);
                            double lati = Double.parseDouble(du.getLatitud());
                            double longi = Double.parseDouble(du.getLongitud());
                            String nombre = du.getNombre();
                            //Double.parseDouble(p)
                            //markeroption es una clase derivada de los markadores
                            //con ella podemos agregarlos
                            MarkerOptions markerOptions = new MarkerOptions();  //aqui se crean
                            markerOptions.position(new LatLng(lati, longi)); //aqui los acomodo en la latitud y longitud de la base de datos
                            String todoslosdatos = "ES UN USUARIO"; //esta variable solo es para mandarle un dato al snippet
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.markermuestrausuario2)); //sirbe para darle un icono
                            markerOptions.title(nombre).visible(true); //le doy el titulo
                            markerOptions.snippet(todoslosdatos).visible(true);//este es como para darle muchos datos despues del titulo
                            marcadoresnuevos.add(mMap.addMarker(markerOptions));
                        } //aqui los agrego a una lista de markadores

                        catch (Exception ra) {

                        }
                    }
                    marcadoresviejos.clear(); //elimino la vieja
                    marcadoresviejos.addAll(marcadoresnuevos); //actualizo la vieja con los nuevos
                    //esto evita llenar el mapa de marcadores
                }

                @Override  //basicamente si no lo pones marca errores :C
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        } else if (depende.equals("soyusuario")) {

            mRootReference.child("chofer").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (Marker marker : marcadoresviejos) {
                        marker.remove();
                    }


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        datoschofer du = snapshot.getValue(datoschofer.class);
                        boolean  convertir=false;
                        try{
                            double lati = Double.parseDouble(du.getLatitud());
                            double longi = Double.parseDouble(du.getLongitud());
                            String nombre = du.getNombre();
                            //Double.parseDouble(p)
                            String todoslosdatos ="Apodo: "+ du.getApodo()+ "\n"+ "Telefono: " + du.getTelefono()+ "\n"+ "Soy chofer de: " + du.getTipodetransporte() ;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(lati, longi));
                            markerOptions.snippet(todoslosdatos).visible(true);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.markermuestrachofer));
                            markerOptions.title(nombre).visible(true);

                          //  markerOptions.showInfoWindow();



                            marcadoresnuevos.add(mMap.addMarker(markerOptions));
                        }catch (Exception r){

                        }


                    }
                    marcadoresviejos.clear();
                    marcadoresviejos.addAll(marcadoresnuevos);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(Mapa.this, "Ocurrio un error vuelve a entrar" ,Toast.LENGTH_SHORT).show();
        }


 }
//metodo con el que cargo un dialogo de recomendacion
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(Mapa.this);
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


//metodo con el que desiframos el joson para trazar y sacar el tiempo
    private void antesdeTrazarRuta(String origen, String destino){
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+origen+"&destination="+destino+"&key=AIzaSyB2B9EwrKBhLjh7pCcDLPzHTwkZPptuvIA";
        RequestQueue queue = Volley.newRequestQueue(Mapa.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    jso = new JSONObject(response);
                    trazarRuta(jso);
                    Log.i("jsonRuta: ",""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

    }










//metodo en el que barrimos el joson que creamos para ver sus diferentes datos
    private void trazarRuta(JSONObject jso) {

        mMap.clear();
        String dondeentrar;
        dondeentrar = getIntent().getStringExtra("quesoy");
        crearmarcadores(dondeentrar);
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        //JSONArray tiempo ;= jso.getJSONArray("tiempo");
        //JSONObject c = tiempo.getJSONObject(0);

        try {
            jRoutes = jso.getJSONArray("routes");

            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                time = ""+((JSONObject)((JSONObject)jLegs.get(i)).get("duration")).get("text");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");


                    for (int k = 0; k<jSteps.length();k++){

                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                       // Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        //aqui se crean los marcadores
                      mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.GREEN).width(5).clickable(true));

                    }



                }



            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
//metodo que manda un cuadro de texto dependiendo que ponga el usuario
    private void quehacercuandodeclikaunmarcador(){
        AlertDialog.Builder dialogo=new AlertDialog.Builder(Mapa.this);
        dialogo.setTitle("¿Que petision decea realizar?");
        dialogo.setMessage("Debe seleccionar alguna");
        dialogo.setPositiveButton("Trazar ruta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                actualizarmiscordenadas();
            }
        });
        dialogo.setNegativeButton("Tiempo de llegada ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actualizarmiscordenadas();
                if(time==null){
                Toast.makeText(Mapa.this, "Primero debes trazar una ruta " +
                        " No te preocupes la trazaresmos automaticamente " +
                        " al destino que seleccionastes " ,Toast.LENGTH_LONG).show();}
                else {
                    Toast.makeText(Mapa.this, "El tiempo de llegada es de:" + time ,Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogo.show();

    }

///metodo para obtener las cordenadas del usuario
    private void obtenermiscordenadas() {
        //antes verifica que el permiso este activo
        if (queentre ==true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Mapa.this,
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
                                Map<String, Object> latlang = new HashMap<>();
                                latlang.put("latitud", location.getLatitude() + "");
                                latlang.put("longitud", location.getLongitude() + "");
                                milatitud = location.getLatitude() + "";
                                milongitud = location.getLongitude() + "";
                                String dondeentrar;
                                dondeentrar = getIntent().getStringExtra("quesoy");
                                if (dondeentrar.equals("soychofer")) {
                                    mRootReference.child("chofer").child(id).updateChildren(latlang);
                                } else if (dondeentrar.equals("soyusuario")) {
                                    mRootReference.child("usuario").child(id).updateChildren(latlang);
                                }

                                // Toast.makeText(getApplication(), "El id es " + id , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



    public void estarsubiendodatos(){

        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(queentre==true){
                obtenercordenadasdeusuarioysubir();}
            }

            public void onFinish() {
                estarsubiendodatos();
            }
        }.start();

    }
    public void cargarDatosdelUsuarioQueEntro(String latitud, String longitud) {
        String dondeentrar;
        dondeentrar = getIntent().getStringExtra("quesoy");
        String id;
        id= getIntent().getStringExtra("recuperaid");
        Map<String, Object> datoschoferr = new HashMap<>();
        if(dondeentrar.equals("soyusuario")){
        datoschoferr.put("latitud", latitud);
        datoschoferr.put("longitud", longitud);
        //idchofer.child("chofer").push().setValue(datosUsuario);
            mRootReference.child("usuario").child(id).updateChildren(datoschoferr);;
        } else {

            datoschoferr.put("latitud", latitud);
            datoschoferr.put("longitud", longitud);
            //idchofer.child("chofer").push().setValue(datosUsuario);
           mRootReference.child("chofer").child(id).updateChildren(datoschoferr);
        }


    }
    private void obtenercordenadasdeusuarioysubir() {
        //antes verifica que el permiso este activo
      if(queentre==true) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(Mapa.this,
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
                              String latitud = "";
                              String longi = "";
                              latitud = location.getLatitude() + "";
                              longi = location.getLongitude() + "";
                              cargarDatosdelUsuarioQueEntro(latitud, longi);
                              // Toast.makeText(getApplication(), "El id es " + id , Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
      }

    }

    private void actualizarmiscordenadas() {
        if (queentre ==true) {
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                obtenermiscordenadas();
                miruta = milatitud + "," + milongitud;
                antesdeTrazarRuta(miruta, rutadestino);
            }

            public void onFinish() {

            }
        }.start();
    }
    }
    @Override
    public void onClick(View v) {
        verificaSeñal();
    }
    private void quitardebasededatos(){
        String dondeentrar;
        dondeentrar = getIntent().getStringExtra("quesoy");
        String id = getIntent().getStringExtra("recuperaid");
        Map<String,Object> latlang = new HashMap<>();
        latlang.put("latitud","");
        latlang.put("longitud","");
        if(dondeentrar.equals("soychofer")) {
            mRootReference.child("chofer").child(id).updateChildren(latlang);
        } else if (dondeentrar.equals("soyusuario")){
            mRootReference.child("usuario").child(id).updateChildren(latlang);
        }
    }
    private void quehacercuandodepresionaregresar(){
        AlertDialog.Builder dialogo=new AlertDialog.Builder(Mapa.this);
        dialogo.setTitle("VEO QUE QUIERES SALIR DEL MAPA");
        dialogo.setMessage("SI SALES DEL MAPA LOS USUARIOS YA NO PODRAN VERTE");
        dialogo.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quitardebasededatos();
                contadordelregreso = 3;
                onBackPressed();
            }
        });
        dialogo.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Mapa.this, "LA ACCION FUE CANCELADA" ,Toast.LENGTH_SHORT).show();
            }
        });
        dialogo.show();

    }
//metodo para mostrar los datos de un marcador
    private void mostrardatosdelchofertodo(String nombre, String datos){
        AlertDialog.Builder dialogo=new AlertDialog.Builder(Mapa.this);
        dialogo.setTitle(nombre);
        dialogo.setMessage(datos);
        dialogo.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialogo.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogo.show();
    }

}
