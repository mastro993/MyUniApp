package progettomobdev.it.myuni;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ModificaEsame extends AppCompatActivity {

    Spinner selezione_voto, selezione_materia;
    EditText data_esame;
    FloatingActionButton salva;
    MateriaRepo matRepo;
    EsameRepo esRepo;
    ArrayList<String> materie;
    Esame esame;
    String votoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_esame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent myIntent = getIntent();
        Integer idEsame = Integer.parseInt(myIntent.getStringExtra("esame"));

        esRepo = new EsameRepo(this);
        matRepo = new MateriaRepo(ModificaEsame.this);

        esame = esRepo.getEsameByID(idEsame);

        // Crea il selettore per la materia con la materia corrente selezionata
        selezione_materia = (Spinner) findViewById(R.id.nome_materia_esame);

        // Materia
        creaSpinnerMaterie(matRepo.getMateriaByID(esame.getId()).getNome());

        // Voto
        selezione_voto = (Spinner) findViewById(R.id.voto_nuovo_esame);
        ArrayAdapter<CharSequence> adapter_voti = ArrayAdapter.createFromResource(this,
                R.array.array_voti, android.R.layout.simple_spinner_item);
        adapter_voti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selezione_voto.setAdapter(adapter_voti);

        if(esame.getVoto() == 0){
            votoString = "Idoneità";
        } else if (esame.getVoto() == Libretto.getValoreLode()){
            votoString = "30 lode";
        } else {
            votoString = esame.getVoto().toString();
        }

        Integer id = adapter_voti.getPosition(votoString);

        selezione_voto.setSelection(id);

        // Data
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                data_esame.setText(sdf.format(myCalendar.getTime()));
            }

        };

        data_esame = (EditText) this.findViewById(R.id.data_nuovo_esame);
        data_esame.setText(new SimpleDateFormat("dd/MM/yyyy").format(esame.getData()));

        data_esame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ModificaEsame.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data_esame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(ModificaEsame.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        salva = (FloatingActionButton) findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Materia m = new Materia();
                m = matRepo.getMateriaByNome(selezione_materia.getSelectedItem().toString());
                Integer id = m.getID();

                String voto_string = selezione_voto.getSelectedItem().toString();
                if (Objects.equals(voto_string, "30 lode")) {
                    voto_string = Libretto.getValoreLode().toString();
                } else if (Objects.equals(voto_string, "Idoneità")) {
                    voto_string = "0";
                }

                Integer voto = Integer.parseInt(voto_string);
                Integer crediti = m.getCrediti();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsedDate = null;
                try {
                    parsedDate = dateFormat.parse(data_esame.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Timestamp data = new Timestamp(parsedDate.getTime());

                Esame e = new Esame();
                e.setData(data);
                e.setIdMateria(id);
                e.setVoto(voto);
                e.setID(esRepo.getEsameByMateria(id).getId());

                esRepo.update(e);

                Toast.makeText(ModificaEsame.this, (String) "Esame modificato con successo!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void creaSpinnerMaterie(String materia) {
        materie = new ArrayList<String>();
        materie.add("Seleziona un corso");

        materie.add(materia);

        for (Materia mat : matRepo.getListaMaterieNoEsame()) {
            materie.add(mat.getNome());
        }
        materie.add("+ Aggiungi nuovo corso");

        ArrayAdapter<String> adapter_materie = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, materie);
        adapter_materie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selezione_materia.setAdapter(adapter_materie);
        selezione_materia.setSelection(1);
    }

}
