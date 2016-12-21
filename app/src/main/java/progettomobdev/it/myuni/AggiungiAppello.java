package progettomobdev.it.myuni;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AggiungiAppello extends AppCompatActivity {

    Spinner selezione_materia, selezione_tipo;
    MateriaRepo matRepo;
    AppelloRepo apRepo;
    ArrayList<String> materie;
    EditText aula, data, ora;
    FloatingActionButton salva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_appello);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        selezione_materia = (Spinner) findViewById(R.id.nome_materia_appello);
        selezione_tipo = (Spinner) findViewById(R.id.tipo_appello);

        matRepo = new MateriaRepo(AggiungiAppello.this);
        apRepo = new AppelloRepo(AggiungiAppello.this);

        // Materia
        creaSpinnerMaterie();

        aula = (EditText) findViewById(R.id.aula_appello);
        data = (EditText) findViewById(R.id.data_appello);

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

                data.setText(sdf.format(myCalendar.getTime()));
            }

        };

        data = (EditText) this.findViewById(R.id.data_appello);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AggiungiAppello.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(AggiungiAppello.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        ora = (EditText) findViewById(R.id.ora_appello);

        ora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AggiungiAppello.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ora.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Seleziona un orario");
                mTimePicker.show();
            }
        });

        ora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AggiungiAppello.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            ora.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Seleziona un orario");
                    mTimePicker.show();
                }
            }
        });


        salva = (FloatingActionButton) findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Objects.equals(selezione_materia.getSelectedItem().toString(), "")) {
                    Toast.makeText(AggiungiAppello.this, (String) "Inserisci un corso!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(data.getText().toString(), "")) {
                    Toast.makeText(AggiungiAppello.this, (String) "Inserisci una data!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Materia m = new Materia();
                    m = matRepo.getMateriaByNome(selezione_materia.getSelectedItem().toString());
                    Integer id = m.getID();

                    String aula_appello = aula.getText().toString();

                    if (ora.getText().toString().equals("")) {
                        ora.setText("00:00");
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    java.util.Date parsedDate = null;
                    try {
                        parsedDate = dateFormat.parse(data.getText().toString() + " " + ora.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Timestamp data = new Timestamp(parsedDate.getTime());

                    Integer tipo_appello = selezione_tipo.getSelectedItemPosition();

                    Appello a = new Appello(null, id, tipo_appello, data, aula_appello);
                    apRepo.insert(a);

                    Toast.makeText(AggiungiAppello.this, (String) "Appello aggiunto con successo!",
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
                Toast.makeText(AggiungiAppello.this, (String) materia.getNome() + " aggiunta!",
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

    @Override
    protected void onStart() {
        super.onStart();

        selezione_materia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String test = selezione_materia.getItemAtPosition(position).toString();
                if (Objects.equals(test, "+ Aggiungi nuovo corso")) {
                    nuovoCorso(AggiungiAppello.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NULLA
            }
        });
    }
}
