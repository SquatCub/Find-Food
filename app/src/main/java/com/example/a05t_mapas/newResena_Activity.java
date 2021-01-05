package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class newResena_Activity extends AppCompatActivity {

    private EditText local;
    private EditText alimento;
    private EditText resena;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_resena_);
        local = (EditText)findViewById(R.id.localName);
        alimento = (EditText)findViewById(R.id.comidaName);
        resena = (EditText)findViewById(R.id.resenaName);
        //finish();
    }

    public void create(View view) {
        Resena newResena = new Resena();
        newResena.setNombre("Diego Zamora");
        newResena.setEdad(22);
        newResena.setRestaurant(local.getText().toString());
        newResena.setPlatillo(alimento.getText().toString());
        newResena.setReseña(resena.getText().toString());

        Bundle bundle = getIntent().getExtras();
        newResena.setLatitud(bundle.getDouble("lat"));
        newResena.setLongitud(bundle.getDouble("lon"));
        Lista.Resenas.add(newResena);
        Intent data = new Intent();
        Toast.makeText(this,"Reseña creada con éxito", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, data);


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}