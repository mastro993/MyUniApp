package progettomobdev.it.myuni;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ModificaAppello extends AppCompatActivity {

    Spinner selezione_materia, selezione_tipo;
    MateriaRepo matRepo;
    AppelloRepo apRepo;
    ArrayList<String> materie;
    EditText aula, data, ora;
    FloatingActionButton salva;
    Integer idAppello;
    Appello app;
    Materia m;
    String aulaAppello;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_appello);
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

        // Ottendo il parametro passato dall'activity precedente
        intent = getIntent();
        idAppello = Integer.parseInt(intent.getStringExtra("appello"));

        matRepo = new MateriaRepo(ModificaAppello.this);
        apRepo = new AppelloRepo(ModificaAppello.this);

        app = apRepo.getAppelloByID(idAppello);

        // Spinneder per la selezione della materia
        selezione_materia = (Spinner) findViewById(R.id.nome_materia_appello);
        selezione_tipo = (Spinner) findViewById(R.id.tipo_appello);
        selezione_tipo.setSelection(app.getTipo());
        creaSpinnerMaterie(matRepo.getMateriaByID(app.getIdMateria()).getNome());

        // Aula
        aula = (EditText) findViewById(R.id.aula_appello);
        aula.setText(app.getAula());

        // Data
        data = (EditText) findViewById(R.id.data_appello);
        data.setText(new SimpleDateFormat("dd/MM/yyyy").format(app.getData()));

        // Datepicker per la data
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

        // Mostra il datepicker una volta cliccato sul campo di testo della data
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ModificaAppello.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(ModificaAppello.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        // Ora
        ora = (EditText) findViewById(R.id.ora_appello);
        ora.setText(new SimpleDateFormat("HH:mm").format(app.getData()));

        // Mostra un time picker una volta cliccato sul campo di testo dell'ora
        ora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ModificaAppello.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ora.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Seleziona un orario");
                mTimePicker.show();
            }
        });

        ora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(ModificaAppello.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            ora.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Seleziona un orario");
                    mTimePicker.show();
                }
            }
        });

        // Bottone per salvare
        salva = (FloatingActionButton) findViewById(R.id.salva);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(selezione_materia.getSelectedItem().toString(), "Seleziona un corso")){
                    Toast.makeText(ModificaAppello.this, (String) "Inserisci un corso!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(aula.getText().toString(), "Seleziona un aula")){
                    Toast.makeText(ModificaAppello.this, (String) "Inserisci il voto!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(data.getText().toString(), "")) {
                    Toast.makeText(ModificaAppello.this, (String) "Inserisci una data!",
                            Toast.LENGTH_LONG).show();
                }else if (Objects.equals(ora.getText().toString(), "")){
                    Toast.makeText(ModificaAppello.this, (String) "Inserisci un orario!",
                            Toast.LENGTH_LONG).show();
                } else {

                    // Procede con il salvataggio dell'appello

                    // Id del corso selezionato
                    m = matRepo.getMateriaByNome(selezione_materia.getSelectedItem().toString());
                    Integer id = m.getID();

                    // Aula dell'appello
                    aulaAppello = aula.getText().toString();

                    if(ora.getText().toString().equals("")){
                        ora.setText("00:00");
                    }

                    // Nuova data
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    java.util.Date parsedDate = null;
                    try {
                        parsedDate = dateFormat.parse(data.getText().toString() + " " + ora.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Timestamp data = new Timestamp(parsedDate.getTime());

                    Integer tipo_appello = selezione_tipo.getSelectedItemPosition();

                    Appello a = new Appello(app.getID(), id, tipo_appello, data, aulaAppello);
                    apRepo.update(a);

                    // Notifica il successo
                    Toast.makeText(ModificaAppello.this, (String) "Appello aggiunto con successo!",
                            Toast.LENGTH_LONG).show();

                    // Chiude l'activity
                    finish();
                }
            }
        });
    }

    // Popola lo spinner delle materie
    private void creaSpinnerMaterie(String materia) {

        materie = new ArrayList<String>();
        materie.add("Seleziona un corso");

        for (Materia mat : matRepo.getListaMaterie()) {
            materie.add(mat.getNome());
        }

        ArrayAdapter<String> adapter_materie = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, materie);
        adapter_materie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selezione_materia.setAdapter(adapter_materie);

        Integer i;
        String tempItem;

        for(i=0;i<selezione_materia.getCount(); i++){
            tempItem = selezione_materia.getItemAtPosition(i).toString();
            if(Objects.equals(tempItem, materia)){
                selezione_materia.setSelection(i);
                break;
            }
        }
    }

}
