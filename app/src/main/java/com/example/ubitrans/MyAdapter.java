package com.example.ubitrans;
//esta clase funciona para crear mi propio marcado esta ves lo creo para mostrar los mensajes
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
//la extendemos que es una base de adaptador
public class MyAdapter extends BaseAdapter {
    //creamos nuestras variables
    private Context context;
    private int layout;
    //creamos dos listas
    private List<String> names;
    private List<String> menS;
//aqui igualamos niestras cosas al original
    public MyAdapter(Context context, int layout, List<String> names,List<String> mensajes ){
  this.context  = context;
  this.layout = layout;
  this.names = names;
  this.menS = mensajes;
    }

    @Override
    public int getCount() {
        return this.names.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  //este metodo es donde ira toda la logica
       View v = convertView;
       //primero inflamos el contexto
        LayoutInflater  layoutInflater = LayoutInflater.from(this.context);
        //ya inflado lo podemos igualar a un view
      v =  layoutInflater.inflate(R.layout.interfasmensajes, null);
      String namee = names.get(position);
      String MEN = menS.get(position);
        TextView textname = (TextView) v.findViewById(R.id.Nombre);
        textname.setText(namee);
        TextView textmensaje = (TextView) v.findViewById(R.id.mensaje);
        textmensaje.setText(MEN);
        return v;
    }
}
