package com.example.a05t_mapas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyBD_FindFood extends SQLiteOpenHelper {

    private Context myContext;

    private final String queryUserTable =
            "CREATE TABLE User " +
                    "( idUser INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "  nombre TEXT, " +
                    "  apellido TEXT, " +
                    "  edad INTEGER, " +
                    "  ciudad TEXT " +
                    ")";

    private final String queryGeoreferenciaTable =
            "CREATE TABLE Georeferencia " +
                    "( idGeoreferencia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "  latitud REAL, " +
                    "  longitud REAL " +
                    ")";

    private final String queryResenaTable =
            "CREATE TABLE Resena " +
                    "( idResena INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "  restaurant TEXT , " +
                    "  platillo   TEXT , " +
                    "  resena     TEXT , " +
                    "  fecha      TEXT , " +
                    "  idUser               INTEGER , " +
                    "  idGeoreferencia      INTEGER , " +
                    "  FOREIGN KEY (idGeoreferencia) REFERENCES Georeferencia(idGeoreferencia) ," +
                    "  FOREIGN KEY (idUser) REFERENCES User(idUser)" +
                    ")";

    public MyBD_FindFood(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(queryUserTable);
        db.execSQL(queryGeoreferenciaTable);
        db.execSQL(queryResenaTable);

        String queryAddUser =
                " INSERT INTO User " +
                        "(nombre, apellido, edad, ciudad) VALUES " +
                        "('Diego', 'Zamora', 22, 'Huajumbaro')";

        db.execSQL(queryAddUser);

        Log.d("MyBD_FindFood", "Creando Base de Datos");
        Toast.makeText(this.myContext, "Creando Base de Datos... :)", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Georeferencia");
        db.execSQL("DROP TABLE IF EXISTS Resena");
        db.execSQL(queryGeoreferenciaTable);
        db.execSQL(queryResenaTable);
        Log.d("MyBD_FindFood", "Actualizando Base de Datos");
        Toast.makeText(this.myContext, "Actualizando Base de Datos Version "+newVersion+" ... :)", Toast.LENGTH_LONG).show();
    }
}
