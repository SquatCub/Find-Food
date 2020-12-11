package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class openResena_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_resena_);

        Bundle bundle = getIntent().getExtras();
        int indice = bundle.getInt("indice");

        TextView myTexto = findViewById(R.id.idTexto);
        myTexto.setText("Ubicacion: "+indice);
    }
}