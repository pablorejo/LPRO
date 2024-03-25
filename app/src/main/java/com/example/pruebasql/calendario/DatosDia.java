package com.example.pruebasql.calendario;

import android.app.sdksandbox.LoadSdkException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.lista_vaca.CowItem;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;


public class DatosDia extends Fragment {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LinearLayout linearLayout; // Asegúrate de inicializar esto correctamente en onCreateView o onViewCreated
    private Usuario usuario;

    private int colorEnfermedad = Color.RED;
    private int colorParto = Color.GREEN;
    private int colorMedicina = Color.BLUE;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = DataManager.getInstance().getUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_datos_dia, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutDatosDia); // Asegúrate de reemplazar tuLinearLayoutId con el ID real de tu LinearLayout en el layout del fragmento

        return view;
    }

    public void setEventDetails(LocalDate date, int numeroPendiente) {

        linearLayout.removeAllViews();

        for (Vaca vaca: usuario.getVacas()){
            if (numeroPendiente == 0 || vaca.getNumeroPendiente() == numeroPendiente){
                for (Enfermedad enfermedad: vaca.getEnfermedades()){
                    if (enfermedad.getFechaInicio().equals(date)){
                        addText(String.valueOf(enfermedad.getNumero_pendiente()) +" Inicio" + enfermedad.getEnfermedad(),colorEnfermedad);
                    }
                    else if (enfermedad.getFechaFin().equals(date)){
                        addText(String.valueOf(enfermedad.getNumero_pendiente()) +" Fin" + enfermedad.getEnfermedad(),colorEnfermedad);
                    }

                    for (LocalDate fechaTomarMedicina: enfermedad.getFechasTomarMedicina()){
                        if (fechaTomarMedicina.equals(date)){
                            addText(String.valueOf(enfermedad.getNumero_pendiente()) + " Tomar: " + enfermedad.getMedicamento() ,colorMedicina);
                        }
                    }
                }
                for (Parto parto: vaca.getPartos()){
                    if (parto.getFechaParto().equals(date)){
                        addText(String.valueOf(parto.getNumeroPendiente()),colorParto);
                    }
                }
            }
        }
    }

    private void addText(String texto, int color){
        View newLayout = LayoutInflater.from(getContext()).inflate(R.layout.list_cow, linearLayout, false);
        TextView textViewNumeroPendiente = newLayout.findViewById(R.id.idTextViewCowItemList);
        textViewNumeroPendiente.setText(texto);
        textViewNumeroPendiente.setBackgroundColor(color);
        linearLayout.addView(newLayout);
    }

    public void clearDatos(){
        linearLayout.removeAllViews();
    }
}
