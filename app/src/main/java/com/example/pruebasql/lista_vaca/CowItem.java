package com.example.pruebasql.lista_vaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.calendario.Calendario;

import org.threeten.bp.LocalDate;

public class CowItem extends BarraSuperior {

    EditText editTextNumeroPendiente, editTextFechaNacimiento, editTextNota, textViewNumeroPendienteMadre;
    Button btnEdit;

    Boolean editando = false;

    TextView txtCowLendar,txtCowFinder;

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
        if (numeroPendienteString.equals("0")){
            editando = true;
        }
        vaca = usuario.getVacaByNumeroPendiente(Integer.parseInt(numeroPendienteString));

        btnEdit = findViewById(R.id.buttonEditCowItem);

        editTextNumeroPendiente = findViewById(R.id.numeroPendiente);
        editTextNumeroPendiente.setText(String.valueOf(vaca.getNumeroPendiente()));
        editTextNumeroPendiente.setEnabled(false);

        editTextFechaNacimiento = findViewById(R.id.textViewFechaDeNacimientoEdit);
        editTextFechaNacimiento.setEnabled(false);
        if(editando){
            editTextFechaNacimiento.setOnClickListener(v -> {
                openDialog(editTextFechaNacimiento);
            });
        }else{editTextFechaNacimiento.setEnabled(false);}

        editTextFechaNacimiento.setText(vaca.getFechaNacimiento().toString());

        editTextNota = findViewById(R.id.editTextTextNotaVaca);
        if (!editando) {editTextNota.setEnabled(false);}
        editTextNota.setText(vaca.getNota());

        textViewNumeroPendienteMadre = findViewById(R.id.textViewNumeroPendienteMadre);
        if (!editando) {textViewNumeroPendienteMadre.setEnabled(false);}

        textViewNumeroPendienteMadre.setText(String.valueOf(vaca.getIdNumeroPendienteMadre()));
        textViewNumeroPendienteMadre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textViewNumeroPendienteMadre.setText(""); // Esto borrará el texto cuando el EditText gane el foco
                }
            }
        });


        btnEdit.setOnClickListener(v -> {
            if (editando){
                guardarVaca();
            }else{
                editarVaca();
            }
            editando = !editando;
        });
        if (editando){
            btnEdit.setText("Guardar");
        }

        txtCowLendar = findViewById(R.id.CowLendar);


        if (!editando){
            txtCowLendar.setOnClickListener(v -> {
                Intent intentCalendario = new Intent(getApplicationContext(), Calendario.class);
                intentCalendario.putExtra("numero_pendiente", numeroPendienteString);
                startActivity(intentCalendario);
            });
        }

        txtCowFinder = findViewById(R.id.CowFinder);
    }

    private void editarVaca(){
        editTextFechaNacimiento.setEnabled(true);
        editTextFechaNacimiento.setOnClickListener(v -> {
            openDialog(editTextFechaNacimiento);
        });
        textViewNumeroPendienteMadre.setEnabled(true);


        editTextNota.setEnabled(true);

        txtCowLendar.setOnClickListener(v -> {});
        txtCowFinder.setOnClickListener(v -> {});

        btnEdit.setText("Guardar");
    }

    private void guardarVaca(){
        Server server = new Server(this,usuario);
        editTextFechaNacimiento.setEnabled(false);
        editTextFechaNacimiento.setOnClickListener(v -> {});
        textViewNumeroPendienteMadre.setEnabled(false);
        editTextNota.setEnabled(false);

        txtCowLendar.setOnClickListener(v -> {
            Intent intentCalendario = new Intent(getApplicationContext(), Calendario.class);
            intentCalendario.putExtra("numero_pendiente", vaca.getNumeroPendiente());
            startActivity(intentCalendario);
        });

        vaca.setFechaNacimiento(LocalDate.parse(editTextFechaNacimiento.getText().toString(),formatter));

        vaca.setNota(editTextNota.getText().toString());

        String strNumeroPendienteMadre = textViewNumeroPendienteMadre.getText().toString();
        if (strNumeroPendienteMadre != null && strNumeroPendienteMadre!= ""){
            vaca.setIdNumeroPendienteMadre(Integer.parseInt(strNumeroPendienteMadre));
        }

        server.updateVaca(vaca);

        btnEdit.setText("Editar");
    }
}