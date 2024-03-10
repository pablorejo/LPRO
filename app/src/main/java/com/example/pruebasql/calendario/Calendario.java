package com.example.pruebasql.calendario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.Usuario;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.calendario.AddEnfermedad;
import com.example.pruebasql.calendario.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.time.ZoneId;
import java.util.Date;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class Calendario extends BarraSuperior {

    Button btnAddEnfermedad, btnAddParto;
    MaterialCalendarView calendarView;

    LocalDate localDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        configureToolbar();
        Usuario usuario = getIntent().getParcelableExtra("usuario");

        calendarView = findViewById(R.id.calendarView);

        btnAddEnfermedad = findViewById(R.id.btnAddEnfermedad);
        btnAddParto = findViewById(R.id.btnAddParto);

        btnAddParto.setOnClickListener(v -> {

            //iniciarActividad(AddEnfermedad.class,usuario);
        });

        btnAddEnfermedad.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddEnfermedad.class);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String fechaComoString = localDate.format(formatter);

            intent.putExtra("fecha", fechaComoString);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            startActivity(intent);
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                // Convertir CalendarDay a LocalDate
                localDate = date.getDate();
            }
        });

    }

    private void añadirEnfermedad(Enfermedad enfermedad){

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        HashSet<CalendarDay> dates = new HashSet<>();
        dates.add(CalendarDay.from(enfermedad.getFechaInicio().getYear(),enfermedad.getFechaInicio().getMonth(),enfermedad.getFechaInicio().getDay()));
        dates.add(CalendarDay.from(enfermedad.getFechaFin().getYear(),enfermedad.getFechaFin().getMonth(),enfermedad.getFechaFin().getDay()));


        int color = getResources().getColor(com.google.android.material.R.color.design_default_color_primary); // Usa un color de tu elección
        calendarView.addDecorator(new EventDecorator(color, dates));
    }

}