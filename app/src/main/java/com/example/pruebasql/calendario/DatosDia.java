package com.example.pruebasql.calendario;

import android.app.Activity;
import android.app.sdksandbox.LoadSdkException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pruebasql.DataManager;
import com.example.pruebasql.PrincipalActivity;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.lista_vaca.CowItem;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.w3c.dom.Text;


public class DatosDia extends Fragment {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LinearLayout linearLayout; // Asegúrate de inicializar esto correctamente en onCreateView o onViewCreated
    private Usuario usuario;

    private int colorEnfermedad = Color.RED;
    private int colorParto = Color.GREEN;
    private int colorMedicina = Color.BLUE;

    private ActivityResultLauncher<Intent> miActivityResultLauncher;
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

    public void setMiActivityResultLauncher(ActivityResultLauncher<Intent> launcher) {
        this.miActivityResultLauncher = launcher;
    }

    public void setEventDetails(LocalDate date, int numeroPendiente) {

        linearLayout.removeAllViews();

        for (Vaca vaca: usuario.getVacas()){
            if (numeroPendiente == 0 || vaca.getNumeroPendiente() == numeroPendiente){
                for (Enfermedad enfermedad: vaca.getEnfermedades()){
                    if (enfermedad.getFechaInicio().equals(date)){
                        addText(String.valueOf(enfermedad.getNumero_pendiente()) +" Inicio" + enfermedad.getEnfermedad(),
                                colorEnfermedad,
                                AddEnfermedad.class,
                                enfermedad.getId_enfermedad_vaca(),
                                enfermedad.getNota());
                    }
                    else if (enfermedad.getFechaFin().equals(date)){
                        addText(String.valueOf(enfermedad.getNumero_pendiente()) +" Fin" + enfermedad.getEnfermedad(),
                                colorEnfermedad,
                                AddEnfermedad.class,
                                enfermedad.getId_enfermedad_vaca(),
                                enfermedad.getNota());
                    }

                    for (LocalDate fechaTomarMedicina: enfermedad.getFechasTomarMedicina()){
                        if (fechaTomarMedicina.equals(date)){
                            addText(String.valueOf(enfermedad.getNumero_pendiente()) + " Tomar: " + enfermedad.getMedicamento() ,
                                    colorMedicina,
                                    AddEnfermedad.class,
                                    enfermedad.getId_enfermedad_vaca(),
                                    enfermedad.getNota());
                        }
                    }
                }
                for (Parto parto: vaca.getPartos()){
                    if (parto.getFechaParto().equals(date)){
                        addText(String.valueOf(parto.getNumeroPendiente()),
                                colorParto,AddParto.class, parto.getId_vaca_parto(),
                                parto.getNota());
                    }
                }
            }
        }
    }

    private void addText(String texto, int color, Class<?> clase, int id, String nota){
        View newLayout = LayoutInflater.from(getContext()).inflate(R.layout.list_date, linearLayout, false);
        LinearLayout linearLayout1 = newLayout.findViewById(R.id.linearLayoutListDate);
        linearLayout1.setBackgroundColor(color);

        TextView textViewNumeroPendiente = newLayout.findViewById(R.id.idTextViewDateListItem);
        textViewNumeroPendiente.setText(texto);

        TextView textViewNota = newLayout.findViewById(R.id.textViewNotaListDate);
        textViewNota.setText(nota);

        linearLayout.addView(newLayout);

        linearLayout1.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), clase);
            intent.putExtra("id",String.valueOf(id));
            // Importante NOOO poner esta bandera sino no funciona el miActivityResultLauncher.
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            miActivityResultLauncher.launch(intent);
        });
    }

    public void clearDatos(){
        linearLayout.removeAllViews();
    }
}
