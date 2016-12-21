package progettomobdev.it.myuni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class PreferenzeActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    EditText nome, cognome, matricola, cfu, vlode;
    String nomeS, cognomeS, matricolaS;
    Integer cfuI, vlodeI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferenze);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreferenzeActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        nome = (EditText) findViewById(R.id.nome_edit_text);
        cognome = (EditText) findViewById(R.id.surname_edit_text);
        matricola = (EditText) findViewById(R.id.matricola_edit_text);
        cfu = (EditText) findViewById(R.id.ncrediti);
        vlode = (EditText) findViewById(R.id.val_lode);

        nome.setText(null);

        prefs = getSharedPreferences("progettomobdev.it.myuni.Preferenze" , Context.MODE_PRIVATE);
        editor = prefs.edit();

        setCampi();

        FloatingActionButton okButton = (FloatingActionButton) findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cfu.getText().toString().equals("") || nome.getText().toString().equals("")
                        || cognome.getText().toString().equals("") || matricola.getText().toString().equals("")) {

                    Toast.makeText(getApplicationContext(), "Completa tutti i campi", Toast.LENGTH_SHORT).show();
                }

                else{
                    setSharedPreferences();
                    Intent i = new Intent(PreferenzeActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });




    }


    private void setCampi(){
        String cfuS = new String();
        nome.setText(prefs.getString("Nome", null));
        cognome.setText(prefs.getString("Cognome", null));
        matricola.setText(prefs.getString("Matricola", null));
        Integer cfux = prefs.getInt("CreditiLaurea", 0);
        cfu.setText(cfux.toString());
        Integer valode = prefs.getInt("ValoreLode", 0);
        vlode.setText(valode.toString());
    }

    void setSharedPreferences() {

        nomeS = nome.getText().toString();
        cognomeS = cognome.getText().toString();
        matricolaS = matricola.getText().toString();
        cfuI = Integer.parseInt(cfu.getText().toString());
        vlodeI = Integer.parseInt(vlode.getText().toString());

        Studente stud = new Studente();
        stud.setNome(nomeS);
        stud.setCognome(cognomeS);
        stud.setMatricola(matricolaS);
        stud.save(prefs);

        Libretto lib = new Libretto();
        lib.setCreditiLaurea(cfuI);
        lib.setValoreLode(vlodeI);
        lib.save(prefs);


    }

}
