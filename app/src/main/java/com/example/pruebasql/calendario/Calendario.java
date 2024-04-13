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
import androidx.core.content.ContextCompat;
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


    private LinearLayout layout;
    private Usuario usuario;
    private int numeroPendiente;
    private DatosDia datosDia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        configureToolbar();
        usuario = DataManager.getInstance().getUsuario();



        // Inciamos el fragmento de datos día
        datosDia =  new DatosDia();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewDatosDia, datosDia)
                .commit();
        datosDia.setMiActivityResultLauncher(miActivityResultLauncher);

        Intent intent2 = getIntent();
        String numeroPendienteString = intent2.getStringExtra("numero_pendiente");

        layout  = findViewById(R.id.linearLayoutCalendario);
        if (numeroPendienteString == null){
            numeroPendiente = 0;
        }else{
            numeroPendiente = Integer.valueOf(numeroPendienteString);
        }

        // Inicializa el ActivityResultLauncher
        calendarView = findViewById(R.id.calendarView);

        btnAddEnfermedad = findViewById(R.id.btnAddEnfermedad);
        btnAddParto = findViewById(R.id.btnAddParto);

        btnAddParto.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddParto.class);
            intent.putExtra("fecha",localDate.toString());
            miActivityResultLauncher.launch(intent);
        });

        btnAddEnfermedad.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddEnfermedad.class);
            intent.putExtra("fecha",localDate.toString());
            miActivityResultLauncher.launch(intent);
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                // Convertir CalendarDay a LocalDate
                if (localDate != null && localDate.equals(date.getDate())){
                    localDate = date.getDate();
                    hideEventDetailsFragment();
                }else{
                    localDate = date.getDate();
                    showEventDetailsFragment(localDate);
                }
            }
        });

        updateCalendar();
    }

    @Override
    protected void actualizar(Intent data) {
        updateCalendar();
        showEventDetailsFragment(localDate);
    }

    private void updateCalendar(){
        calendarView.removeDecorators();
        for (Vaca vaca : usuario.getVacas()){
            if (numeroPendiente == 0 || vaca.getNumeroPendiente() == numeroPendiente){
                añadirEnfermedades(vaca.getEnfermedades());
                añadirPartos(vaca.getPartos());
            }
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

    private void añadirTomarMedicina(LocalDate fecha_tomar){
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        HashSet<CalendarDay> dates = new HashSet<>();
        addDate(dates,fecha_tomar);
        calendarView.addDecorator(new EventDecorator(colorMedicina, dates));
    }


    private void añadirEnfermedades(ArrayList<Enfermedad> enfermedades){
        for (Enfermedad enfermedad: enfermedades){
            añadirEnfermedad(enfermedad);

            // Para añadir las fechas en las que tiene que tomar la medicina.
            for (LocalDate fechaTomarMedicina: enfermedad.getFechasTomarMedicina()){
                añadirTomarMedicina(fechaTomarMedicina);
            }
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
        if (datosDia == null) {
            datosDia = new DatosDia();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerViewDatosDia, datosDia)
                    .commit();
        }else{
            datosDia.setEventDetails(date,numeroPendiente);
        }
    }

    // Opcional: Método para ocultar el fragmento si es necesario
    private void hideEventDetailsFragment() {
        if (datosDia != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(datosDia)
                    .commit();
        }else{
            datosDia.clearDatos();
        }
    }
}