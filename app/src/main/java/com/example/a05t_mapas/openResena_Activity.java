package com.example.a05t_mapas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class openResena_Activity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mapa;
    private Resena myReseña;
    private int indice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_resena_);

        Bundle bundle = getIntent().getExtras();
        indice = bundle.getInt("indice");

        myReseña = (Resena) Lista.Resenas.get(indice);


        TextView myTexto = findViewById(R.id.idTexto);
        TextView persona = findViewById(R.id.nombrePersona);
        TextView local = findViewById(R.id.nombreLocal);
        TextView alimento = findViewById(R.id.nombreAlimento);
        TextView resena = findViewById(R.id.nombreResena);
        myTexto.setText("Ubicacion: "+indice);
        persona.setText("Usuario: "+myReseña.getNombre());
        local.setText("Restaurant/Local: "+myReseña.getRestaurant());
        alimento.setText("Alimento: "+myReseña.getPlatillo());
        resena.setText("Reseña: "+myReseña.getReseña());

        iniciaMapa();
    }
    private void iniciaMapa() {
        MapFragment mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.fragMapEC2);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        LatLng ll = new LatLng(myReseña.getLatitud(), myReseña.getLongitud());

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

                    Intent i = new Intent(getApplicationContext(), openResena_Activity.class);
                    Bundle b = new Bundle();

                    b.putInt("indice", indice);

                    i.putExtras(b);
                    startActivity(i);

                    return null;
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
    public void renderMap()
    {
        mapa.clear();
        LatLng ubicacion = new LatLng(myReseña.getLatitud(), myReseña.getLongitud());

        MarkerOptions options = new MarkerOptions().position(ubicacion);
        Marker marcador = mapa.addMarker(options);
        marcador.setTag(new String(""+indice));
    }
}