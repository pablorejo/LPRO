package com.example.pruebasql.calendario;

import android.app.Activity;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;

import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Date;
import java.util.Locale;

import org.threeten.bp.LocalDate;

public class Calendario extends BarraSuperior {


    Button btnAddEnfermedad, btnAddParto;
    MaterialCalendarView calendarView;

    LocalDate localDate;

    View view;
    private int colorEnfermedad = Color.RED;
    private int colorParto = Color.GREEN;

    private LinearLayout layout;

    private Usuario usuario;

    // Define un ActivityResultLauncher como una variable de instancia
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        configureToolbar();
        usuario = DataManager.getInstance().getUsuario();

        layout  = findViewById(R.id.linearLayoutCalendario);


        // Inicializa el ActivityResultLauncher
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            limpiarCalendario();
                            for (Vaca vaca : usuario.getVacas()){
                                añadirEnfermedades(vaca.getEnfermedades());
                                añadirPartos(vaca.getPartos());
                            }
                        }
                    }
                });
        calendarView = findViewById(R.id.calendarView);

        btnAddEnfermedad = findViewById(R.id.btnAddEnfermedad);
        btnAddParto = findViewById(R.id.btnAddParto);

        btnAddParto.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddParto.class);
            intent.putExtra("fecha",localDate.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            someActivityResultLauncher.launch(intent);
        });

        btnAddEnfermedad.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddEnfermedad.class);
            intent.putExtra("fecha",localDate.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            someActivityResultLauncher.launch(intent);
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                // Convertir CalendarDay a LocalDate
                try {
                    if (localDate.equals(date.getDate())){
                        hideEventDetailsFragment();
                    }else{
                        localDate = date.getDate();
                        showEventDetailsFragment(localDate);
                    }
                }catch (Exception e){
                    localDate = date.getDate();
                }
            }
        });

        for (Vaca vaca : usuario.getVacas()){
            añadirEnfermedades(vaca.getEnfermedades());
            añadirPartos(vaca.getPartos());
        }
    }

    private void addDate(HashSet<CalendarDay> dates, LocalDate fecha){
        dates.add(CalendarDay.from(fecha.getYear(),
                        fecha.getMonthValue(),
                fecha.getDayOfMonth()));
    }
    private void añadirEnfermedad(Enfermedad enfermedad){
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        HashSet<CalendarDay> dates = new HashSet<>();
        addDate(dates,enfermedad.getFechaInicio());
        addDate(dates,enfermedad.getFechaFin());
        calendarView.addDecorator(new EventDecorator(colorEnfermedad, dates));
    }


    private void añadirEnfermedades(ArrayList<Enfermedad> enfermedades){
        for (Enfermedad enfermedad: enfermedades){
            añadirEnfermedad(enfermedad);
        }
    }

    private void añadirParto(Parto parto){
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        HashSet<CalendarDay> dates = new HashSet<>();
        addDate(dates,parto.getFechaParto());
        calendarView.addDecorator(new EventDecorator(colorParto, dates));
    }

    private void añadirPartos(ArrayList<Parto> partos){
        for (Parto parto : partos){
            añadirParto(parto);
        }
    }

    private void limpiarCalendario() {
        if (calendarView != null) {
            calendarView.removeDecorators(); // Elimina todos los decoradores
        }
    }

    private void showEventDetailsFragment(LocalDate date) {
        DatosDia fragment = (DatosDia) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerViewDatosDia);
        if (fragment == null) {
            fragment = new DatosDia();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerViewDatosDia, fragment)
                    .commit();
        } else {
            // Si el fragmento ya está adjunto, actualiza los detalles directamente
            fragment.setEventDetails(date);
        }
    }

    // Opcional: Método para ocultar el fragmento si es necesario
    private void hideEventDetailsFragment() {
        DatosDia fragment = (DatosDia) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerViewDatosDia);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }
}