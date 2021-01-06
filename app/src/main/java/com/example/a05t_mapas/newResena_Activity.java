package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
        if (local.getText().length() > 0 && alimento.getText().length() > 0 && resena.getText().length() > 0)
        {
            Bundle bundle = getIntent().getExtras();
            MyBD_FindFood dbFindFood = new MyBD_FindFood(newResena_Activity.this, "MyBD_FindFood", null, bundle.getInt("VERSION"));
            SQLiteDatabase myDB = dbFindFood.getWritableDatabase();

            String queryAddGeoreferencia =
                    " INSERT INTO Georeferencia " +
                            "(latitud, longitud) VALUES " +
                            "("+bundle.getDouble("lat")+", "+bundle.getDouble("lon")+")";

            myDB.execSQL(queryAddGeoreferencia);

            int idUser = 1;
            int idGeoreferencia = getIdGeoreferencia(bundle.getDouble("lat"), bundle.getDouble("lon"), bundle.getInt("VERSION"));


            String queryAddResena =
                    " INSERT INTO Resena " +
                            "(restaurant, platillo, resena, fecha, idUser, idGeoreferencia) VALUES " +
                            "('"+local.getText().toString()+"', '"+alimento.getText().toString()+"', '"+resena.getText().toString()+"', '"+getDateTime()+"', "+idUser+", "+idGeoreferencia+") ";
            myDB.execSQL(queryAddResena);

            myDB.close();

            //Toast.makeText(this, queryAddResena, Toast.LENGTH_LONG).show();

            Intent data = new Intent();
            Toast.makeText(this,"Reseña creada con éxito", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK, data);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(newResena_Activity.this, "Debes llenar todos los datos", Toast.LENGTH_LONG).show();
        }
    }

    private int getIdGeoreferencia(double lat, double lon, int VERSION)
    {
        MyBD_FindFood dbFindFood = new MyBD_FindFood(newResena_Activity.this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getReadableDatabase();
        String query = "SELECT * FROM Georeferencia WHERE latitud = "+lat+" AND longitud = "+lon+"";
        int idGeoreferencia = 1;

        Cursor c = myDB.rawQuery(query, null);
        if (c.moveToFirst()){
            idGeoreferencia = c.getInt(0);
        }
        c.close();
        myDB.close();

        return idGeoreferencia;
    }

    private static String getDateTime() {
        String DATE_FORMAT = "E, dd MMM yyyy HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}