package com.example.a05t_mapas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WS_FindFood {
    private final static String IP = "127.0.0.1:8000";

    public static String getSaludo(){
        String result = "http://192.168.0.106:8000/";
        return result;
    }

    private static String formato(String json){
        String codificado = "";
        try {
            codificado = URLEncoder.encode(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return codificado;
    }
}
