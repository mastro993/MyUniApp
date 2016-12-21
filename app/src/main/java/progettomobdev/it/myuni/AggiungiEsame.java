package progettomobdev.it.myuni;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AggiungiEsame extends AppCompatActivity {

    Spinner selezione_voto;
    Spinner selezione_materia;
    EditText data_esame;
    MateriaRepo matRepo;
    ArrayList<String> materie;
    FloatingActionButton salva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_esame);
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

        selezione_materia = (Spinner) findViewById(R.id.nome_materia_esame);

        matRepo = new MateriaRepo(AggiungiEsame.this);
        final EsameRepo esameRepo = new EsameRepo(AggiungiEsame.this);

        // Materia
        creaSpinnerMaterie();

        // Voto
        selezione_voto = (Spinner) findViewById(R.id.voto_nuovo_esame);
        ArrayAdapter<CharSequence> adapter_voti = ArrayAdapter.createFromResource(this,
                R.array.array_voti, android.R.layout.simple_spinner_item);
        adapter_voti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selezione_voto.setAdapter(adapter_voti);

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

        data_esame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AggiungiEsame.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data_esame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(AggiungiEsame.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        salva = (FloatingActionButton) findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(selezione_materia.getSelectedItem().toString(), "Seleziona un corso")){
                    Toast.makeText(AggiungiEsame.this, (String) "Inserisci un corso!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(selezione_voto.getSelectedItem().toString(), "Seleziona un voto")){
                    Toast.makeText(AggiungiEsame.this, (String) "Inserisci il voto!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(data_esame.getText().toString(), "")){
                    Toast.makeText(AggiungiEsame.this, (String) "Inserisci una data!",
                            Toast.LENGTH_LONG).show();
                } else {
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


                    Esame e = new Esame(id, voto, data);
                    esameRepo.insert(e);

                    Toast.makeText(AggiungiEsame.this, (String) "Esame aggiunto con successo!",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }

    private void creaSpinnerMaterie() {

        materie = new ArrayList<String>();
        materie.add("Seleziona un corso");

        for (Materia mat : matRepo.getListaMaterieNoEsame()) {
            materie.add(mat.getNome());
        }
        materie.add("+ Aggiungi nuovo corso");

        ArrayAdapter<String> adapter_materie = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, materie);
        adapter_materie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selezione_materia.setAdapter(adapter_materie);
    }

    @Override
    protected void onStart() {
        super.onStart();

        selezione_materia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String test = selezione_materia.getItemAtPosition(position).toString();
                if (Objects.equals(test, "+ Aggiungi nuovo corso")) {
                    nuovoCorso(AggiungiEsame.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NULLA
            }
        });
    }

    // Dialog per l'aggiunta di un nuovo corso
    public void nuovoCorso(Context context) {
        final Dialog dialog = new Dialog(context);
        final MateriaRepo matRepo = new MateriaRepo(context);
        dialog.setContentView(R.layout.aggiungi_materia_dialog);
        dialog.setTitle("Nuovo corso");

        // set the custom dialog components - text, image and button
        final EditText nuovoNome = (EditText) dialog.findViewById(R.id.nuovo_nome_materia);
        final EditText nuovoCrediti = (EditText) dialog.findViewById(R.id.crediti_nuova_materia);
        Button save = (Button) dialog.findViewById(R.id.aggiungi_button);
        Button cancel = (Button) dialog.findViewById(R.id.cancella_button);

        // if button is clicked, close the custom dialog
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Materia materia = new Materia(nuovoNome.getText().toString(),
                        Integer.parseInt(nuovoCrediti.getText().toString()));
                matRepo.insert(materia);
                Toast.makeText(AggiungiEsame.this, (String) materia.getNome() + " aggiunta!",
                        Toast.LENGTH_LONG).show();
                creaSpinnerMaterie();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
