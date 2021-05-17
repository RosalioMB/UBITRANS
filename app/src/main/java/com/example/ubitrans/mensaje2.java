package com.example.ubitrans;
//esta clase solo se creo para escuchar a la base de datos para poder agarrar su informacion o bien mandarle informacion
public class mensaje2 {
    //declaracion de variables
    public String   mensaje;
    public String nombre;
    //creacion de geter y seters
    public mensaje2(){

    }
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
