package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private int VERSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();
        VERSION = bundle.getInt("VERSION");
    }
    public void login(View v) {

        EditText myNombre = findViewById(R.id.userName);
        EditText myApellido = findViewById(R.id.userApellido);
        EditText myEdad = findViewById(R.id.userEdad);
        EditText myCiudad = findViewById(R.id.userCiudad);

        if(myNombre.getText().length() > 0 && myApellido.getText().length() > 0 && myEdad.getText().length() > 0 && myCiudad.getText().length() > 0)
        {
            MyBD_FindFood dbFindFood = new MyBD_FindFood(Login.this, "MyBD_FindFood", null, VERSION);
            SQLiteDatabase myDB = dbFindFood.getWritableDatabase();

            String queryAddUser =
                    " INSERT INTO User " +
                            "(nombre, apellido, edad, ciudad) VALUES " +
                            "('"+myNombre.getText().toString()+"', '"+myApellido.getText().toString()+"', "+myEdad.getText().toString()+", '"+myCiudad.getText().toString()+"')";

            myDB.execSQL(queryAddUser);
            myDB.close();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this,"Debes llenar todos los datos...", Toast.LENGTH_LONG).show();
        }

    }
}