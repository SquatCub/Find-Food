package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.a05t_mapas.dialogos.DialogoContinuarCancelar;
import com.example.a05t_mapas.interfaces.InterfazDialogoContinuarCancelar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class newResena_Activity extends AppCompatActivity implements InterfazDialogoContinuarCancelar {

    private EditText local;
    private EditText alimento;
    private EditText resena;
    private RatingBar rating;
    private Button btnNewResena;
    private ProgressDialog pd;
    private String idUser;
    private double myLatitude;
    private double myLongitude;

    private JSONObject myResult;
    private Uri.Builder myBuilder;
    private DialogoContinuarCancelar myDialogoNewResena = new DialogoContinuarCancelar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_resena_);
        local = (EditText)findViewById(R.id.localName);
        alimento = (EditText)findViewById(R.id.comidaName);
        resena = (EditText)findViewById(R.id.resenaName);
        rating = (RatingBar)findViewById(R.id.ratingName);
        btnNewResena = (Button)findViewById(R.id.confirmar);

        Bundle bundle = getIntent().getExtras();
        idUser = bundle.getString("USER");
        myLongitude = bundle.getDouble("lon");
        myLatitude = bundle.getDouble("lat");
        listenerBtnNewResena();
        //finish();
    }

    private void listenerBtnNewResena() {
        btnNewResena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manejador = getSupportFragmentManager();
                myDialogoNewResena.setText("Deseas publicar un Nueva Reseña para el platillo '"+alimento.getText().toString()+"' del Restaurant '"+local.getText().toString()+"'");
                myDialogoNewResena.show(manejador, "TAG");
            }
        });
    }

    public void create() {

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
                            "(restaurant, platillo, resena, rating, fecha, idUser, idGeoreferencia) VALUES " +
                            "('"+local.getText().toString()+"', '"+alimento.getText().toString()+"', '"+resena.getText().toString()+"', '"+(double) rating.getRating()+"', '"+"', "+idUser+", "+idGeoreferencia+") ";
            myDB.execSQL(queryAddResena);

            myDB.close();

            //Toast.makeText(this, queryAddResena, Toast.LENGTH_LONG).show();

            Intent data = new Intent();
            Toast.makeText(this,"Reseña creada con éxito", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK, data);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(this,String.valueOf(rating.getRating()), Toast.LENGTH_LONG).show()

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

    @Override
    public void continuar() {
        try {
            if (local.getText().length() > 0 && alimento.getText().length() > 0 && resena.getText().length() > 0) {
                fetchSaludo();
            } else {
                Toast.makeText(newResena_Activity.this, "Debes llenar todos los datos", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(newResena_Activity.this,e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelar() {
        Toast.makeText(newResena_Activity.this,"Se ha cancelado la Publicacion de la Reseña",Toast.LENGTH_LONG).show();
    }

    /////////////////// SEGMENTO DE WEB SERVICE ///////////////////
    private void fetchSaludo() {
        // Clean Builder Global
        myBuilder = new Uri.Builder();

        // Make URL
        String cadenaQuery = "http://192.168.1.73:8000/new_resena";

        // Make Paquete POST, para Enviar Info
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("idUser", idUser)
                .appendQueryParameter("local", local.getText().toString())
                .appendQueryParameter("platillo", alimento.getText().toString())
                .appendQueryParameter("resena", resena.getText().toString())
                .appendQueryParameter("latitud", Double.toString(myLatitude))
                .appendQueryParameter("longitud", Double.toString(myLongitude))
                .appendQueryParameter("rating", Float.toString(rating.getRating()));
        myBuilder = builder;

        // Fetch Query
        //Toast.makeText(this,cadenaQuery.toString(), Toast.LENGTH_LONG).show();
        new newResena_Activity.Fetch().execute(cadenaQuery);
    }


    /////////////////// CLASS FETCH AsyncTask ///////////////////
    private class Fetch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(newResena_Activity.this);
            pd.setMessage("Espere por favor.");
            pd.setTitle("Realizando registro...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream flujoEntrada = null;

            try {
                // Preparar Conexion
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String query = myBuilder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                // Starts the query

                conn.connect();
                int codigoRespuestaHTTP = conn.getResponseCode();

                Log.d("Respuesta", "La respuesta es: " + codigoRespuestaHTTP);
                flujoEntrada = conn.getInputStream();


                System.out.println("Conn: "+conn.getContent());
                System.out.println("Codigo Respuesta: "+codigoRespuestaHTTP);
                System.out.println("Flujo Entrada: "+flujoEntrada.toString());

                // Convert the InputStream into a string
                String contentAsString = convertStreamToString(flujoEntrada);
                System.out.println("STRFlujo: "+contentAsString);


                conn.disconnect();
                return contentAsString;
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
            finally {
                if (flujoEntrada != null) {
                    flujoEntrada.close();
                }
            }
        }

        private String convertStreamToString(InputStream flujoEntrada) {
            Scanner s = new Scanner(flujoEntrada, "UTF-8").useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                System.out.println("Params: "+params[0]);
                return downloadUrl(params[0]);
            } catch (IOException e) {
                System.out.println("Error: "+e.toString());
                return "088888";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                myResult = new JSONObject(result);

                if(myResult.getString("data").equals("OK"))
                {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    Intent intent = new Intent(newResena_Activity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("USER", myResult.getString("user"));
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(newResena_Activity.this,myResult.getString("error"),Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (pd.isShowing()) {
                pd.dismiss();
            }
        }

    }
}