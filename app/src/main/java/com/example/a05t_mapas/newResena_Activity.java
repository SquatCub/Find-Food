package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class newResena_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_resena_);

        Resena newResena = new Resena();
        newResena.setNombre("Diego Zamora");
        newResena.setEdad(22);
        newResena.setRestaurant("Carl's JR");
        newResena.setPlatillo("Clasica");
        newResena.setReseña("Esta bien pejja");
        Bundle bundle = getIntent().getExtras();
        newResena.setLatitud(bundle.getDouble("lat"));
        newResena.setLongitud(bundle.getDouble("lon"));
        Lista.Resenas.add(newResena);
        Intent data = new Intent();
        Toast.makeText(this,"Redacta tu Reseña...", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, data);
        //finish();
    }
}