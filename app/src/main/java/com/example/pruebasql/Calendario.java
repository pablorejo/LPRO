package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.pruebasql.bbdd.Usuario;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pruebasql.calendario.AddEnfermedad;
import com.example.pruebasql.calendario.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashSet;
public class Calendario extends BarraSuperior {

    Button btnAddEnfermedad, btnAddParto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        configureToolbar();
        Usuario usuario = getIntent().getParcelableExtra("usuario");



        btnAddEnfermedad = findViewById(R.id.btnAddEnfermedad);
        btnAddParto = findViewById(R.id.btnAddParto);

        btnAddParto.setOnClickListener(v -> {

            //iniciarActividad(AddEnfermedad.class,usuario);
        });

        btnAddEnfermedad.setOnClickListener(v -> {
            iniciarActividad(AddEnfermedad.class,usuario);
        });
    }

    private void añadirEnfermedad(){
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        HashSet<CalendarDay> dates = new HashSet<>();
        dates.add(CalendarDay.from(2024, 3, 8)); // Añade aquí tus fechas. Nota: el mes está basado en cero, así que marzo es 2.
        dates.add(CalendarDay.from(2024, 3, 9));

        int color = getResources().getColor(com.google.android.material.R.color.design_default_color_primary); // Usa un color de tu elección
        calendarView.addDecorator(new EventDecorator(color, dates));
    }
}