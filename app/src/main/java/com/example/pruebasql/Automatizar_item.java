package com.example.pruebasql;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Text;

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
        textHoraFin = findViewById(R.id.textHoraFin);

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