package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {


    private Button btnLoginUser;

    private JSONObject myResult;
    private Uri.Builder myBuilder;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        listenerBtnLoginUser();
        listenerBtnRegisterUser();
    }

    private void listenerBtnRegisterUser() {
        TextView btnRegisterUser = findViewById(R.id.registrarse);

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void listenerBtnLoginUser() {
        btnLoginUser = (Button)findViewById(R.id.login);
        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Verify Campos de New User then Get Data and Fetch
                EditText myEmail = findViewById(R.id.userEmail);
                EditText myPassword = findViewById(R.id.userPass);

                if(myEmail.getText().length() > 0  && myPassword.getText().length() > 0)
                {
                    fetchLogin();
                }
                else
                {
                    Toast.makeText(Login.this,"Debes llenar todos los datos...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /////////////////// SEGMENTO DE WEB SERVICE ///////////////////
    private void fetchLogin() {
        // Clean Builder Global
        myBuilder = new Uri.Builder();

        // Get Data and Fetch
        EditText myEmail = findViewById(R.id.userEmail);
        EditText myPassword = findViewById(R.id.userPass);

        // Make URL
        String cadenaQuery = "http://"+IP.getIP()+"/login";

        // Make Paquete POST, para Enviar Info
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", myEmail.getText().toString())
                .appendQueryParameter("password", myPassword.getText().toString());
        myBuilder = builder;

        // Fetch Query
        new Login.Fetch().execute(cadenaQuery);
    }

    /////////////////// CLASS FETCH AsyncTask ///////////////////
    private class Fetch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
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
                    Intent i = new Intent(Login.this, MainActivity.class);
                    Bundle b = new Bundle();

                    b.putString("USER", myResult.getString("user"));

                    i.putExtras(b);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(Login.this,myResult.getString("error"),Toast.LENGTH_LONG).show();
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