package progettomobdev.it.myuni;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGiorno extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public Integer mPage;
    View view;


    public FragmentGiorno() {
        // Required empty public constructor
    }

    public static FragmentGiorno newInstance(Integer page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentGiorno fragment = new FragmentGiorno();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_giorno, container, false);

        final LezioneRepo lrepo = new LezioneRepo(this.getContext());

        setDay(mPage);

        return view;

    }

    private void setDay(Integer mPage){
        switch (mPage){
            case 1:
                setLayout("Lunedi");
                break;
            case 2:
                setLayout("Martedi");
                break;
            case 3:
                setLayout("Mercoledi");
                break;
            case 4:
                setLayout("Giovedi");
                break;
            case 5:
                setLayout("Venerdi");
                break;
        }
    }

    public void setLayout(String day){

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lezioni_container);
        linearLayout.removeAllViews();

        MateriaRepo mrepo = new MateriaRepo(this.getContext());

        final LezioneRepo lrepo = new LezioneRepo(this.getContext());

        ArrayList<Lezione> arrLez = lrepo.getListaLezioni(day);
        Iterator<Lezione> it = arrLez.iterator();

        while (it.hasNext()){
            Lezione l = it.next();
            EsameRepo erepo = new EsameRepo(this.getContext());
            Esame e;
            e = erepo.getEsameByMateria(l.getIDMateria());
            if(e!= null){
                it.remove();
                lrepo.delete(l.getIDLezione());
            }
        }

        for (final Lezione l: arrLez) {

            SimpleDateFormat ora = new SimpleDateFormat("HH:mm");
            String orainizio = ora.format(l.getOraInizio());
            String orafine = ora.format(l.getOraFine());
            String nomemateria = mrepo.getMateriaByID(l.getIDMateria()).getNome();
            String aulaS = l.getAula();


            // Views

            TextView nomeMateria = new TextView(this.getContext());
            nomeMateria.setId(l.getIDLezione());
            nomeMateria.setText(nomemateria);
            nomeMateria.setPadding(5, 5, 5, 5);
            nomeMateria.setTextSize(20);
            nomeMateria.setTypeface(null, Typeface.BOLD_ITALIC);

            TextView aula = new TextView(this.getContext());
            aula.setId(l.getIDLezione()*10);
            aula.setText("Aula : " + aulaS);
            aula.setPadding(5, 5, 5, 5);
            aula.setTextSize(16);

            TextView oraInizio = new TextView(this.getContext());
            oraInizio.setId(l.getIDLezione() * 100);
            oraInizio.setText(orainizio);
            oraInizio.setPadding(20, 20, 20, 20);
            oraInizio.setTextSize(16);
            oraInizio.setTextColor(Color.parseColor("#e65100"));

            TextView oraFine = new TextView(this.getContext());
            oraFine.setId(l.getIDLezione() * 1000);
            oraFine.setText(orafine);
            oraFine.setPadding(20, 20, 20, 20);
            oraFine.setTextSize(16);
            oraFine.setTextColor(Color.parseColor("#039be5"));

            RelativeLayout rl = new RelativeLayout(this.getContext());
            rl.setBackgroundColor(Color.parseColor("#f5f5f5"));
            rl.setPadding(20, 20, 20, 20);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(15, 15, 15, 5);

            RelativeLayout.LayoutParams layoutParamsNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsNome.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layoutParamsNome.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

            RelativeLayout.LayoutParams layoutParamsMateria = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsMateria.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layoutParamsMateria.addRule(RelativeLayout.BELOW, nomeMateria.getId());


            RelativeLayout.LayoutParams layoutParamsOraInizio = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsOraInizio.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            layoutParamsOraInizio.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);


            RelativeLayout.LayoutParams layoutParamsOraFine = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsOraFine.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            layoutParamsOraFine.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);





            rl.addView(oraFine, layoutParamsOraFine);
            rl.addView(oraInizio, layoutParamsOraInizio);
            rl.addView(nomeMateria, layoutParamsNome);
            rl.addView(aula, layoutParamsMateria);
            linearLayout.addView(rl);


            rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.opzioni_lezione, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // Modifica inizio
                                    modificaInizioDialog(l,lrepo);
                                    break;
                                case 1:// Modifica fine
                                    modificaFineDialog(l, lrepo);
                                    break;
                                case 2://Modifica aula
                                    modificaAulaDialog(l, lrepo);
                                    break;
                                case 3: // Elimina
                                    lrepo.delete(l.getIDLezione());
                                    refresh();
                                    break;
                                default:
                                    //
                                    break;
                            }
                        }
                    }).show();

                    return true;
                }
            });

        }
    }

    public void refresh(){
        Fragment currentFragment = getFragmentManager().findFragmentByTag(getTag());
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

    private Boolean checkOraInizio(Context context,String orainizio, Integer orafine, Integer minutifine){
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        try {
            Date oraIni = parser.parse(orainizio);
            if(oraIni.getHours() < orafine ){
                return true;
            }
            else if(oraIni.getHours() == orafine){
                if (oraIni.getMinutes() < minutifine){
                    return true;
                }
            }
            else{
                Toast.makeText(context, R.string.errorOra, Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
        }
        return false;
    }

    private Boolean checkOraFine(Context context,String oraFine, Integer orainizio, Integer minutiinizio){
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        try {
            Date oraFin = parser.parse(oraFine);
            if(oraFin.getHours() > orainizio ){
                return true;
            }
            else if(oraFin.getHours() == orainizio){
                if (oraFin.getMinutes() > minutiinizio){
                    return true;
                }
            }
            else{
                Toast.makeText(context, R.string.errorOraFine, Toast.LENGTH_SHORT).show();
                return false;
            }


        }catch (Exception e){

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

    private void modificaInizioDialog(final Lezione l, final LezioneRepo lrepo){
        Calendar c = Calendar.getInstance();
        final Integer hour = c.get(Calendar.HOUR_OF_DAY);
        final Integer minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String oraInizio = hourOfDay + ":" + minute;
                        if(checkOraInizio(getContext(), oraInizio, l.getOraFine().getHours(), l.getOraFine().getMinutes())) {
                            l.setOraInizio(getTimestampValue(oraInizio));
                            lrepo.update(l);
                            Toast.makeText(getContext(), getString(R.string.oraInizioAggiornata), Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.setTitle(getString(R.string.titleDialogOraInizio));
        timePickerDialog.show();
    }

    private void modificaFineDialog(final Lezione l, final LezioneRepo lrepo){
        Calendar c = Calendar.getInstance();
        final Integer hour = c.get(Calendar.HOUR_OF_DAY);
        final Integer minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String oraFine = hourOfDay + ":" + minute;
                        if(checkOraFine(getContext(), oraFine, l.getOraInizio().getHours(), l.getOraInizio().getMinutes())) {
                            l.setOraFine(getTimestampValue(oraFine));
                            lrepo.update(l);
                            Toast.makeText(getContext(), getString(R.string.oraFineAggiornata), Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.setTitle(getString(R.string.titleDialogOraFine));
        timePickerDialog.show();
    }

    private void modificaAulaDialog(final Lezione l, final LezioneRepo lrepo){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.modifica_aula_dialog);
        dialog.setTitle(getString(R.string.titleDialogModificaAula));

        final EditText nuovaAula = (EditText) dialog.findViewById(R.id.nuova_aula);
        final Button okButton = (Button) dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.setAula(nuovaAula.getText().toString());
                lrepo.update(l);
                Toast.makeText(getContext(), getString(R.string.aulaModificata), Toast.LENGTH_SHORT).show();
                refresh();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
