package com.example.pruebasql;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Automatizar_item extends BarraSuperior {

    private Button btnInicio, btnFin;
    private TextView textHoraInicio,textHoraFin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatizar_item);
        configureToolbar();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre_automatizacion");
        TextView nombreAutomatizacion = findViewById(R.id.textViewAutomatizacionItem);
        nombreAutomatizacion.setText(nombre);

        btnInicio = findViewById(R.id.btnInicio);
        btnFin = findViewById(R.id.btnFin);
        textHoraInicio = findViewById(R.id.textHoraInicio);
        textHoraFin = findViewById(R.id.textFechaFin);

        btnInicio.setOnClickListener(v -> {
            openDialog(textHoraInicio);
        });

        btnFin.setOnClickListener(v -> {
            openDialog(textHoraFin);
        });
    }

    private  void openDialog(TextView text){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                text.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
            }
        },13,00,true);

        dialog.show();
    }
}