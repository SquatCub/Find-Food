package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a05t_mapas.dialogos.DialogoContinuarCancelar;
import com.example.a05t_mapas.interfaces.InterfazDialogoContinuarCancelar;

public class Login extends AppCompatActivity implements InterfazDialogoContinuarCancelar {
    private int VERSION;
    private Button btnNewUser;
    private DialogoContinuarCancelar myDialogoNewUser = new DialogoContinuarCancelar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();
        VERSION = bundle.getInt("VERSION");

        btnNewUser = (Button)findViewById(R.id.login);
        listenerBtnNewUser();
    }

    private void listenerBtnNewUser() {
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manejador = getSupportFragmentManager();
                myDialogoNewUser.setText("Por favor verifica que tus datos sean correctos, una vez confirmada esta operacion no se podrá deshacer y asi es como apareceras en todas las Reseñas que publiques.");
                myDialogoNewUser.show(manejador, "TAG");
            }
        });
    }

    public void login() {

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

    @Override
    public void continuar() {
        login();
    }

    @Override
    public void cancelar() {
        Toast.makeText(Login.this,"Por favor Verifica que tus datos sean correctos",Toast.LENGTH_LONG).show();
    }
}