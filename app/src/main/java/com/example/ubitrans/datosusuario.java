package com.example.ubitrans;
//esta clase solo se creo para escuchar a la base de datos para poder agarrar su informacion o bien mandarle informacion
public class datosusuario {
    //declaracion de variables
    public String email;
    public String nombre;
    public String telefono;
    public String latitud;
    public String longitud;
    public String id;
    //reacion de geter and seters
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public datosusuario(){

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


}
