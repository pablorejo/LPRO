package com.example.pruebasql.lista_vaca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
import com.example.pruebasql.mapa.Mapa;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class CowItem extends BarraSuperior {

    EditText editTextNumeroPendiente, editTextFechaNacimiento, editTextNota;
    TextView textViewVelocidadMediaDia,textViewDistanciaRecorridaDía;

    AutoCompleteTextView textViewNumeroPendienteMadre;
    Button btnEdit, btnEliminarCowItem;

    Boolean editando = false;

    TextView txtCowLendar,txtCowFinder;

    Usuario usuario;
    Vaca vaca;
    Server server;

    HijasVaca hijasVaca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_item);
        configureToolbar();
        usuario = DataManager.getInstance().getUsuario();
        server = new Server(this,usuario);

        Intent intent = getIntent();
        String numeroPendienteString = intent.getStringExtra("numero_pendiente");
        if (numeroPendienteString.equals("0")){
            editando = true;
        }
        int numero_pendiente = Integer.parseInt(numeroPendienteString);

        hijasVaca =  new HijasVaca(numero_pendiente);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewHijasVaca, hijasVaca)
                .commit();

        vaca = usuario.getVacaByNumeroPendiente(numero_pendiente);

        btnEliminarCowItem = findViewById(R.id.btnEliminarCowItem);
        btnEliminarCowItem.setVisibility(View.GONE);
        btnEliminarCowItem.setOnClickListener(v -> {
            server.deleteVaca(vaca.getNumeroPendiente());
            usuario.getVacas().remove(vaca);
            finish();
        });

        btnEdit = findViewById(R.id.buttonEditCowItem);
        btnEdit.setOnClickListener(v -> {
            if (editando){
                btnEliminarCowItem.setVisibility(View.GONE);
                guardarVaca();
            }else{
                btnEliminarCowItem.setVisibility(View.VISIBLE);
                editarVaca();
            }
            editando = !editando;
        });
        if (editando){
            btnEdit.setText("Guardar");
        }

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
        // Obtener referencia al EditText desde el layout
        AutoCompleteTextView textViewNumeroPendienteMadre = findViewById(R.id.textViewNumeroPendienteMadre);
        // Establecer el adaptador para el EditText
        textViewNumeroPendienteMadre.setAdapter(usuario.getAdapterVacas(this));

        txtCowLendar = findViewById(R.id.CowLendar);

        if (!editando){
            txtCowLendar.setOnClickListener(v -> {
                Intent intentCalendario = new Intent(getApplicationContext(), Calendario.class);
                intentCalendario.putExtra("numero_pendiente", numeroPendienteString);
                startActivity(intentCalendario);
            });
        }

        txtCowFinder = findViewById(R.id.CowFinder);

        if (!editando){
            txtCowFinder.setOnClickListener(v -> {
                Intent intentCalendario = new Intent(getApplicationContext(), Mapa.class);
                intentCalendario.putExtra("numero_pendiente", numeroPendienteString);
                startActivity(intentCalendario);
            });
        }


        ArrayList<String> sugerencias = new ArrayList<>();
        for (Vaca vaca : usuario.getVacas()) {
            sugerencias.add(String.valueOf(vaca.getNumeroPendiente()));
        }


        textViewVelocidadMediaDia = findViewById(R.id.textViewVelocidadMediaDia);
        textViewDistanciaRecorridaDía = findViewById(R.id.textViewDistanciaRecorridaDía);
        if (vaca != null && vaca.velocidadMediaDia!= null){
            textViewVelocidadMediaDia.setText(getString(R.string.velocidadMedia) + " " +  vaca.velocidadMediaDia.toString());
        }else{
            textViewVelocidadMediaDia.setText(getString(R.string.velocidadMedia) + " " +  getString(R.string.noData));
        }
        if (vaca != null && vaca.distanciaRecorridaDía!= null){

            textViewDistanciaRecorridaDía.setText(getString(R.string.distaciaMedia) + " " +  vaca.distanciaRecorridaDía.toString());
        }else{
            textViewDistanciaRecorridaDía.setText(getString(R.string.distaciaMedia) + " " +  getString(R.string.noData));
        }
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
        if (strNumeroPendienteMadre != null && !strNumeroPendienteMadre.equals("")){
            vaca.setIdNumeroPendienteMadre(Integer.parseInt(strNumeroPendienteMadre));
        }else{
            vaca.setIdNumeroPendienteMadre(0);
        }

        server.updateVaca(vaca);

        btnEdit.setText("Editar");
    }
}