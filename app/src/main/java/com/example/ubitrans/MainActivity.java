package com.example.ubitrans;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //creando bariable de booton

    private Button btnUsuario;
    private Button btnChofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_myicon);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //igualamos la variable que creamos en la linea 9 a un boton

        btnUsuario = findViewById(R.id.usuario);
        btnChofer = findViewById(R.id.chofer);
//eventos cuando le damos clik a un boton
        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, iniciar.class);
                startActivity(intent);
            }
        });
        btnChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, chofer.class);
                startActivity(intent);
            }
        });


    }


    
}
