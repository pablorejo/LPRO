package com.example.pruebasql.calendario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;

public class AddEnfermedad extends BarraSuperior {

    private Button btnInicio, btnFin;
    private TextView textHoraInicio,textHoraFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enfermedad);
        configureToolbar();


        btnInicio = findViewById(R.id.btnFechaInicio);
        btnFin = findViewById(R.id.btnFechaFin);
        textHoraInicio = findViewById(R.id.textFechaInicio);
        textHoraFin = findViewById(R.id.textFechaFin);

        btnInicio.setOnClickListener(v -> {
            openDialog(textHoraInicio);
        });

        btnFin.setOnClickListener(v -> {
            openDialog(textHoraFin);
        });
    }

    private  void openDialog(TextView text){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                text.setText(String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(dayOfMonth));
            }
        },24,3,8);

        dialog.show();
    }
}