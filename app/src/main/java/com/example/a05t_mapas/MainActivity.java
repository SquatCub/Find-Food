package com.example.a05t_mapas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mapa;
    private final int CODE_PERMISO = 3;
    private final int request_code = 1;
    private final int VERSION = 2;

    private long lastTouchTime = 0;
    private long currentTouchTime = 0;

    private LocationManager locManager;
    private Location loc;
    private double myLatitude;
    private double myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(this, "Lat: " + loc.getLatitude() + " | Lon: " + loc.getLongitude(), Toast.LENGTH_LONG).show();
            myLatitude = loc.getLatitude();
            myLongitude = loc.getLongitude();

            if (existUser()) {
                loadDB();
                loadData();
                loadMap();
            } else {
                gotoLogin();
            }
        } else {
            Toast.makeText(this, "No tiene Permisos para el GPS :( ", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, CODE_PERMISO);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (CODE_PERMISO == requestCode) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(this,"Lat: "+loc.getLatitude()+" | Lon: "+loc.getLongitude(),Toast.LENGTH_LONG).show();
            myLatitude = loc.getLatitude();
            myLongitude = loc.getLongitude();

            if(existUser()) {
                loadDB();
                loadData();
                loadMap();
            }
            else {
                gotoLogin();
            }
        }
        else {
            Toast.makeText(this,"No tiene Permisos para el GPS :( ",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoLogin() {
        Intent i = new Intent(MainActivity.this, Login.class);
        Bundle b = new Bundle();

        b.putInt("VERSION", VERSION);

        i.putExtras(b);
        startActivity(i);
        finish();
    }

    private boolean existUser() {
        MyBD_FindFood dbFindFood = new MyBD_FindFood(this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getReadableDatabase();
        String query = "SELECT * FROM User";
        boolean existUser = false;

        Cursor c = myDB.rawQuery(query, null);

        if (c.moveToFirst()){
            existUser = true;
        }
        c.close();
        myDB.close();

        return existUser;
    }

    private void loadMap() {
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

    private void loadDB() {
        MyBD_FindFood dbFindFood = new MyBD_FindFood(MainActivity.this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getWritableDatabase();
        myDB.close();
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
        LatLng ll = new LatLng(myLatitude, myLongitude);

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
                    lastTouchTime = currentTouchTime;
                    currentTouchTime = System.currentTimeMillis();

                    if (currentTouchTime-lastTouchTime < 1000) {
                        Log.d("Duble","Doble clic");
                        lastTouchTime = 0;
                        currentTouchTime = 0;
                        String tagIndice = (String) marker.getTag();
                        int indice = Integer.parseInt(tagIndice);

                        Intent i = new Intent(MainActivity.this, openResena_Activity.class);
                        Bundle b = new Bundle();

                        b.putInt("indice", indice);

                        i.putExtras(b);
                        startActivity(i);
                    }
                    else {
                        Log.d("Duble",Long.valueOf(currentTouchTime-lastTouchTime).toString());
                        lastTouchTime = currentTouchTime;
                    }
                    /*String tagIndice = (String) marker.getTag();
                    int indice = Integer.parseInt(tagIndice);

                    Intent i = new Intent(MainActivity.this, openResena_Activity.class);
                    Bundle b = new Bundle();

                    b.putInt("indice", indice);

                    i.putExtras(b);
                    startActivity(i);*/

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
                    finish();
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
        LatLng ubicacion = new LatLng(myLatitude, myLongitude);

        MarkerOptions options = new MarkerOptions().position(ubicacion);
        Marker marcador = mapa.addMarker(options);
        marcador.setSnippet("Tú estás aquí");
        marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps));
        marcador.setTitle("Tú estás aquí");
        for (int i = 0; i < Lista.Resenas.size(); i++)
        {
            Resena myReseña = (Resena) Lista.Resenas.get(i);
            ubicacion = new LatLng(myReseña.getLatitud(), myReseña.getLongitud());

            options = new MarkerOptions().position(ubicacion);
            marcador = mapa.addMarker(options);
            marcador.setSnippet(myReseña.getPlatillo());
            marcador.setTag(new String(""+i));
            marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
            marcador.setTitle(myReseña.getRestaurant());
        }
    }

    private void loadData()
    {
        MyBD_FindFood dbFindFood = new MyBD_FindFood(MainActivity.this, "MyBD_FindFood", null, VERSION);
        SQLiteDatabase myDB = dbFindFood.getReadableDatabase();
        String query = "SELECT Resena.idResena, latitud, longitud, restaurant, platillo, resena, fecha, nombre, edad, apellido FROM Georeferencia INNER JOIN Resena INNER JOIN User ON Georeferencia.idGeoreferencia = Resena.idGeoreferencia AND Resena.idUser = User.idUser";

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
                String apellido = c.getString(9);

                Resena newResena = new Resena();
                newResena.setNombre(nombre+" "+apellido);
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