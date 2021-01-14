package com.example.a05t_mapas.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.a05t_mapas.R;
import com.example.a05t_mapas.interfaces.InterfazDialogoContinuarCancelar;

public class DialogoContinuarCancelar extends DialogFragment {
    private InterfazDialogoContinuarCancelar myDialogoNewResena;
    private String text = "¿Desea Continuar?";

    public void setText(String text){
        this.text = text;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof Activity)
        {
            myDialogoNewResena = (InterfazDialogoContinuarCancelar) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoPersonalizado();
    }

    private AlertDialog crearDialogoPersonalizado() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflador = getActivity().getLayoutInflater();
        View v = inflador.inflate(R.layout.dialogo_continuar_cancelar, null);
        builder.setView(v);

        Button myContinuar = v.findViewById(R.id.btnContinuar);
        Button myCancelar = v.findViewById(R.id.btnCancelar);
        TextView myText = v.findViewById(R.id.myText);
        myText.setText(this.text);


        myContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDialogoNewResena.continuar();

            }
        });

        myCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                myDialogoNewResena.cancelar();
            }
        });

        return builder.create();
    }
}
