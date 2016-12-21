package progettomobdev.it.myuni;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class OrarioFragment extends Fragment {


    public OrarioFragment() {
        // Required empty public constructor
    }

    private TabLayout tabLayout;
    Context context;
    FloatingActionButton fab;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orario, container, false);
        context = this.getContext();


        tabLayout = (TabLayout) view.findViewById(R.id.tabnav);

        viewPager  = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getFragmentManager(), this.getContext()));
        tabLayout.setupWithViewPager(viewPager);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_aggiungi_lezione);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuovaLezione(getContext(), viewPager);
            }
        });


        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Controlla se esistono materie nel database e

        FragmentGiorno fg = (FragmentGiorno) getFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getCurrentItem()));

        if(fg!= null)
            fg.refresh();

        fg = (FragmentGiorno) getFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getCurrentItem() + 1 ));
        //quello dopo
        if (fg != null)
            fg.refresh();
    }

    public void nuovaLezione(final Context context, final ViewPager viewPager ){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.aggiungi_lezione_dialog);
        dialog.setTitle("Nuova Lezione");

        final Context c = context;
        final Spinner materia = (Spinner) dialog.findViewById(R.id.spinnerCorso);
        final Spinner giorno = (Spinner) dialog.findViewById(R.id.spinnerGiorno);
        final EditText etInizio = (EditText) dialog.findViewById(R.id.inizio);
        final EditText etFine = (EditText) dialog.findViewById(R.id.fine);
        final EditText aula = (EditText) dialog.findViewById(R.id.aula);
        Button ok = (Button) dialog.findViewById(R.id.okButton);
        etInizio.setFocusable(false);
        etFine.setFocusable(false);


        List<String> materie = new ArrayList<>();

        MateriaRepo mrepo = new MateriaRepo(c);

        for (Materia m: mrepo.getListaMaterie()){
            materie.add(m.getNome());
        }

        for (Iterator<String> it = materie.iterator(); it.hasNext(); ) {
            String materiaCorrente = it.next();
            EsameRepo erepo = new EsameRepo(this.getContext());
            Esame e;
            e = erepo.getEsameByMateria(mrepo.getMateriaByNome(materiaCorrente).getID());
            if (e != null) {
                it.remove();
            }
        }

        ArrayAdapter<String> adpt1 = new ArrayAdapter<String>(c,R.layout.spinner_row, materie);
        adpt1.setDropDownViewResource(R.layout.spinner_row);
        materia.setAdapter(adpt1);

        etInizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                final Integer hour = c.get(Calendar.HOUR_OF_DAY);
                final Integer minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String oraInizio = hourOfDay + ":" + minute;
                                    etInizio.setText(oraInizio);
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle(getString(R.string.titleDialogOraInizio));
                timePickerDialog.show();
            }
        });

        etInizio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar c = Calendar.getInstance();
                    final Integer hour = c.get(Calendar.HOUR_OF_DAY);
                    final Integer minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    String oraInizio = hourOfDay + ":" + minute;
                                    etInizio.setText(oraInizio);
                                }
                            }, hour, minute, true);
                    timePickerDialog.setTitle(getString(R.string.titleDialogOraInizio));
                    timePickerDialog.show();
                }
            }
        });

        etFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                final Integer hour = c.get(Calendar.HOUR_OF_DAY);
                final Integer minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String oraFine = hourOfDay + ":" + minute;
                                etFine.setText(oraFine);
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle(getString(R.string.titleDialogOraFine));
                timePickerDialog.show();

            }
        });

        etFine.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar c = Calendar.getInstance();
                    final Integer hour = c.get(Calendar.HOUR_OF_DAY);
                    final Integer minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    String oraFine = hourOfDay + ":" + minute;
                                    etFine.setText(oraFine);
                                }
                            }, hour, minute, true);
                    timePickerDialog.setTitle(getString(R.string.titleDialogOraFine));
                    timePickerDialog.show();
                }
            }
        });

        // if button is clicked, close the custom dialog
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //trovo id materia scelta
                if(materia.getSelectedItem() !=null) {
                    Lezione lez = new Lezione();
                    final String materiaS = materia.getSelectedItem().toString();
                    MateriaRepo mrepo = new MateriaRepo(context);
                    Integer IDMateria = mrepo.getMateriaByNome(materiaS).getID();
                    //leggo giorno scelto
                    String giornoS = giorno.getSelectedItem().toString();

                    //leggo aula scelta
                    if (aula.getText() != null) {
                        String aulaS = aula.getText().toString();
                        lez.setAula(aulaS);
                    }

                    lez.setIDMateria(IDMateria);
                    lez.setGiorno(giornoS);

                    if (checkInizioFine(etInizio, etFine, context)) {
                        lez.setOraInizio(getTimestampValue(etInizio.getText().toString()));
                        lez.setOraFine(getTimestampValue(etFine.getText().toString()));

                        LezioneRepo lrepo = new LezioneRepo(c);
                        lrepo.insert(lez);
                        //aggiorno il fragment che ho in background

                        FragmentGiorno fg = (FragmentGiorno) getFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getCurrentItem()));

                        fg.refresh();

                        fg = (FragmentGiorno) getFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getCurrentItem() + 1));
                        //quello prima
                        if (fg != null)
                            fg.refresh();

                        //e quello dopo
                        fg = (FragmentGiorno) getFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getCurrentItem() - 1));
                        if (fg != null)
                            fg.refresh();

                        dialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(context, "Impossibile impostare lezione senza materia", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    public String getFragmentTag(int pos){
        return "android:switcher:"+R.id.viewpager+":"+pos;
    }


    public Boolean checkInizioFine(EditText inizio, EditText fine, Context context){

        String inizioS, fineS;
        inizioS = inizio.getText().toString();
        fineS = fine.getText().toString();

        Timestamp oraIniTmp = getTimestampValue(inizioS);
        Timestamp oraFinTmp = getTimestampValue(fineS);

        if(oraIniTmp.getHours() < oraFinTmp.getHours() ){
            return true;
        }
        else if(oraIniTmp.getHours() == oraFinTmp.getHours()){
            if (oraIniTmp.getMinutes() < oraFinTmp.getMinutes()){
                return true;
            }
            if(oraIniTmp.getHours() == oraFinTmp.getMinutes()){
                Toast.makeText(context, R.string.errorOra, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        else{
            Toast.makeText(context, R.string.errorOra, Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;

    }

    public Timestamp getTimestampValue (String ora){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date parsed = dateFormat.parse(ora);
            Timestamp oraTS = new java.sql.Timestamp(parsed.getTime());
            return oraTS;

        }catch(Exception e) {
        }
        return null;
    }

}