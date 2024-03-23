package com.example.pruebasql.lista_vaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Vaca;

import org.threeten.bp.LocalDate;

public class CowItem extends BarraSuperior {

    EditText editTextNumeroPendiente, editTextFechaNacimiento, editTextNota;
    Button btnEdit;

    Boolean editando = false;

    Usuario usuario;
    Vaca vaca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_item);
        configureToolbar();
        usuario = DataManager.getInstance().getUsuario();

        Intent intent = getIntent();
        String numeroPendienteString = intent.getStringExtra("numero_pendiente");

        vaca = usuario.getVacaByNumeroPendiente(Integer.parseInt(numeroPendienteString));
        btnEdit = findViewById(R.id.buttonEditCowItem);

        editTextNumeroPendiente = findViewById(R.id.numeroPendiente);
        editTextNumeroPendiente.setEnabled(false);
        editTextNumeroPendiente.setText(String.valueOf(vaca.getNumeroPendiente()));


        editTextFechaNacimiento = findViewById(R.id.textViewFechaDeNacimientoEdit);
        editTextFechaNacimiento.setEnabled(false);
        editTextFechaNacimiento.setText(vaca.getFechaNacimiento().toString());

        editTextNota = findViewById(R.id.editTextTextNotaVaca);
        editTextNota.setEnabled(false);
        editTextNota.setText(vaca.getNota());

        btnEdit.setOnClickListener(v -> {
            if (editando){
                guardarVaca();
            }else{
                editarVaca();
            }
            editando = !editando;
        });
    }

    private void editarVaca(){
        editTextFechaNacimiento.setEnabled(true);
        editTextNota.setEnabled(true);
        editTextNota.setEnabled(true);
        btnEdit.setText("Guardar");
    }

    private void guardarVaca(){
        Server server = new Server(this,usuario);
        editTextFechaNacimiento.setEnabled(false);
        editTextNumeroPendiente.setEnabled(false);
        editTextNota.setEnabled(false);

        vaca.setNumeroPendiente(Integer.parseInt(editTextNumeroPendiente.getText().toString()));
        vaca.setFechaNacimiento(LocalDate.parse(editTextFechaNacimiento.getText().toString(),formatter));
        vaca.setNota(editTextNota.getText().toString());
        server.updateVaca(vaca);

        btnEdit.setText("Editar");
    }
}