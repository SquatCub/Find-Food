package com.example.a05t_mapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

public class Register extends AppCompatActivity implements InterfazDialogoContinuarCancelar {
    private Button btnNewUser;
    private DialogoContinuarCancelar myDialogoNewUser = new DialogoContinuarCancelar();

    private JSONObject myResult;
    private Uri.Builder myBuilder;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        listenerBtnNewUser();
        listenerToLogin();
    }

    private void listenerToLogin() {
        TextView btnToLogin = findViewById(R.id.toLogin);

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void listenerBtnNewUser() {
        btnNewUser = (Button)findViewById(R.id.login);
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Verify Campos de New User then Get Data and Fetch
                EditText myNombre = findViewById(R.id.userName);
                EditText myApellido = findViewById(R.id.userApellido);
                EditText myEdad = findViewById(R.id.userEdad);
                EditText myCiudad = findViewById(R.id.userCiudad);

                EditText myEmail = findViewById(R.id.userEmail);
                EditText myPassword = findViewById(R.id.userPass);
                EditText myConfirmation = findViewById(R.id.userConfirm);

                if(myNombre.getText().length() > 0 && myApellido.getText().length() > 0 && myEdad.getText().length() > 0 && myCiudad.getText().length() > 0 && myEmail.getText().length() > 0  && myPassword.getText().length() > 0  && myConfirmation.getText().length() > 0)
                {
                    FragmentManager manejador = getSupportFragmentManager();
                    myDialogoNewUser.setText("Por favor verifica que tus datos sean correctos, una vez confirmada esta operacion no se podrá deshacer y asi es como apareceras en todas las Reseñas que publiques.");
                    myDialogoNewUser.show(manejador, "TAG");
                }
                else
                {
                    Toast.makeText(Register.this,"Debes llenar todos los datos...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void continuar() {
        fetchRegister();
    }

    @Override
    public void cancelar() {
        Toast.makeText(Register.this,"Por favor Verifica que tus datos sean correctos",Toast.LENGTH_LONG).show();
    }

    /////////////////// SEGMENTO DE WEB SERVICE ///////////////////
    private void fetchRegister() {
        // Clean Builder Global
        myBuilder = new Uri.Builder();

        // Get Data and Fetch
        EditText myNombre = findViewById(R.id.userName);
        EditText myApellido = findViewById(R.id.userApellido);
        EditText myEdad = findViewById(R.id.userEdad);
        EditText myCiudad = findViewById(R.id.userCiudad);

        EditText myEmail = findViewById(R.id.userEmail);
        EditText myPassword = findViewById(R.id.userPass);
        EditText myConfirmation = findViewById(R.id.userConfirm);

        // Make URL
        String cadenaQuery = "http://"+IP.getIP()+"/register";

        // Make Paquete POST, para Enviar Info
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", myEmail.getText().toString())
                .appendQueryParameter("password", myPassword.getText().toString())
                .appendQueryParameter("confirmation", myConfirmation.getText().toString())
                .appendQueryParameter("first_name", myNombre.getText().toString())
                .appendQueryParameter("last_name", myApellido.getText().toString())
                .appendQueryParameter("edad", myEdad.getText().toString())
                .appendQueryParameter("ciudad", myCiudad.getText().toString());
        myBuilder = builder;

        // Fetch Query
        new Register.Fetch().execute(cadenaQuery);
    }

    /////////////////// CLASS FETCH AsyncTask ///////////////////
    private class Fetch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Register.this);
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
                    Intent i = new Intent(Register.this, MainActivity.class);
                    Bundle b = new Bundle();

                    b.putString("USER", myResult.getString("user"));

                    i.putExtras(b);
                    startActivity(i);
                    finish();

                }
                else
                {
                    Toast.makeText(Register.this,myResult.getString("error"),Toast.LENGTH_LONG).show();

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