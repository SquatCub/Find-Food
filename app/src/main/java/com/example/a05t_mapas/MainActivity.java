package com.example.a05t_mapas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mapa;
    private final int request_code = 1;
    private final int VERSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        MyBD_FindFood dbFindFood = new MyBD_FindFood(MainActivity.this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getWritableDatabase();
        myDB.close();

        loadData();

        if (googleServiciosDisponible())
        {
            //Toast.makeText(this, "Servicios disponibles", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            iniciaMapa();
        }
        else
        {
            Toast.makeText(this, "Servicio NO disponible :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciaMapa() {
        MapFragment mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.fragMapEC);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServiciosDisponible() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int estaDisponible = api.isGooglePlayServicesAvailable(this);
        if (estaDisponible == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(estaDisponible)) {
            Dialog dialog = api.getErrorDialog(this, estaDisponible, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "No fue posible establecer conexión con Google Services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapa = googleMap;
        LatLng ll = new LatLng(19.707611075950084, -101.1888419188767);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15);

        mapa.moveCamera(update);

        if(mapa != null)
        {
            renderMap();

            mapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    String tagIndice = (String) marker.getTag();
                    int indice = Integer.parseInt(tagIndice);

                    Intent i = new Intent(MainActivity.this, openResena_Activity.class);
                    Bundle b = new Bundle();

                    b.putInt("indice", indice);

                    i.putExtras(b);
                    startActivity(i);

                    return null;
                }
            });

            mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Intent i = new Intent(MainActivity.this, newResena_Activity.class);
                    Bundle b = new Bundle();

                    b.putDouble("lat", latLng.latitude);
                    b.putDouble("lon", latLng.longitude);
                    b.putInt("VERSION", VERSION);
                    i.putExtras(b);

                    startActivityForResult(i, request_code);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                renderMap();
            }
        }
    }

    private void renderMap()
    {
        mapa.clear();

        //Toast.makeText(getApplicationContext(), "Numero de Registros: "+Lista.Resenas.size(),Toast.LENGTH_SHORT).show();

        for (int i = 0; i < Lista.Resenas.size(); i++)
        {
            Resena myReseña = (Resena) Lista.Resenas.get(i);
            LatLng ubicacion = new LatLng(myReseña.getLatitud(), myReseña.getLongitud());

            MarkerOptions options = new MarkerOptions().position(ubicacion);
            Marker marcador = mapa.addMarker(options);
            marcador.setTag(new String(""+i));
        }
    }

    private void loadData()
    {
        MyBD_FindFood dbFindFood = new MyBD_FindFood(MainActivity.this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getReadableDatabase();
        String query = "SELECT Resena.idResena, latitud, longitud, restaurant, platillo, resena, fecha, nombre, edad FROM Georeferencia INNER JOIN Resena INNER JOIN User ON Georeferencia.idGeoreferencia = Resena.idGeoreferencia AND Resena.idUser = User.idUser";

        Cursor c = myDB.rawQuery(query, null);
        if (c.moveToFirst()){
            do {
                int idResena = c.getInt(0);
                double latitud = c.getDouble(1);
                double longitud = c.getDouble(2);
                String restaurant = c.getString(3);
                String platillo = c.getString(4);
                String resena = c.getString(5);
                String fecha = c.getString(6);
                String nombre = c.getString(7);
                int edad = c.getInt(8);

                Resena newResena = new Resena();
                newResena.setNombre(nombre);
                newResena.setEdad(edad);
                newResena.setRestaurant(restaurant);
                newResena.setPlatillo(platillo);
                newResena.setReseña(resena);
                newResena.setLatitud(latitud);
                newResena.setLongitud(longitud);
                Lista.Resenas.add(newResena);

            } while(c.moveToNext());
        }
        c.close();
        myDB.close();
    }
}