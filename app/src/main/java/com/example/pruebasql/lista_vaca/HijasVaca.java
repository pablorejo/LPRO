package com.example.pruebasql.lista_vaca;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Vaca;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HijasVaca#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HijasVaca extends Fragment {


    private LinearLayout linearLayout;
    private Usuario usuario;

    private int numeroPendiente;

    private int colorMadre = Color.GREEN;
    private int colorHijas = Color.BLUE;

    private ActivityResultLauncher<Intent> miActivityResultLauncher;
    public HijasVaca(int numeroPendiente) {
        usuario = DataManager.getInstance().getUsuario();
        this.numeroPendiente = numeroPendiente;
        miActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        añadirHijas();
                    }
                }
            }
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hijas_vaca, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutHijasVacas);
        añadirHijas();
        return view;
    }

    public void añadirHijas(){
        Vaca vaca = usuario.getVacaByNumeroPendiente(numeroPendiente);
        Vaca madre = usuario.getMadre(vaca);
        if (madre != null){
            crearCowItem("Madre:",usuario.getMadre(vaca),colorMadre);
        }

        for (Vaca hija: usuario.getHijos(vaca)){
            crearCowItem("Hija:",hija,colorHijas);
        }
    }

    private void crearCowItem(String texto,Vaca vaca,int color){
        View newLayout = LayoutInflater.from(getContext()).inflate(R.layout.list_cow, linearLayout, false);
        newLayout.setBackgroundColor(color);
        TextView textViewNumeroPendiente = newLayout.findViewById(R.id.idTextViewCowItemList);

        String numeroPendiente = String.valueOf(vaca.getNumeroPendiente());
        textViewNumeroPendiente.setText(texto + " " + numeroPendiente);

        TextView textViewNotaListCow =  newLayout.findViewById(R.id.textViewNotaListCow);
        textViewNotaListCow.setText(vaca.getNota());

        LinearLayout linearLayoutListCow = newLayout.findViewById(R.id.linearLayoutListCow);

        linearLayoutListCow.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CowItem.class);
            intent.putExtra("numero_pendiente", numeroPendiente);
            miActivityResultLauncher.launch(intent);
        });
        linearLayout.addView(newLayout);
    }
}