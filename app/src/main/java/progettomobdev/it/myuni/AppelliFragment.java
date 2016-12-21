package progettomobdev.it.myuni;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class AppelliFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    View view;
    ImageView immagine_tipo;
    LinearLayout ll;
    RelativeLayout rl;
    AppelloRepo apRepo;
    MateriaRepo matRepo;
    FloatingActionButton fab;
    TextView text;
    Intent nuovoAppello_intent, modificaAppello_intent;

    public AppelliFragment() {
        // Required empty public constructor
    }

    public static AppelliFragment newInstance(String param1, String param2) {
        AppelliFragment fragment = new AppelliFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_appelli, container, false);
        ll = (LinearLayout) view.findViewById(R.id.appelli_container);
        text = (TextView) view.findViewById(R.id.no_appelli);

        apRepo = new AppelloRepo(getContext());
        matRepo = new MateriaRepo((getContext()));

        fab = (FloatingActionButton) view.findViewById(R.id.fab_aggiungi_appello);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovoAppello_intent = new Intent(getContext(), AggiungiAppello.class);
                getContext().startActivity(nuovoAppello_intent);
            }
        });

        return view;
    }

    // Ripopola la lista
    public void refreshData(final View view) {

        ll.removeAllViews(); // Elimina eventuali view figlie già presenti

        // Integer utilizzato per assegnare ID dinamicamente
        Integer id = 1;

        for (final Appello app : apRepo.getListaAppelliNoEsame()) {

            final String nome = matRepo.getMateriaByID(app.getIdMateria()).getNome();
            final String data = new SimpleDateFormat("dd/MM/yyyy").format(app.getData());
            final String ora = new SimpleDateFormat("HH:mm").format(app.getData());
            final String aula = app.getAula();

            // Views
            RelativeLayout rl = new RelativeLayout(this.getContext());
            ImageView immagineTipo = new ImageView(this.getContext());
            TextView nomeMateria = new TextView(this.getContext());
            ImageView immagineData = new ImageView(this.getContext());
            TextView dataAppello = new TextView(this.getContext());
            ImageView immagineOra = new ImageView(this.getContext());
            TextView oraAppello = new TextView(this.getContext());
            ImageView immagineAula = new ImageView(this.getContext());
            TextView aulaAppello = new TextView(this.getContext());

            // Se API >= 21 imposta una traslazione sull'asse z
            if(Build.VERSION.SDK_INT >= 21){
                rl.setTranslationZ(2f);
            }

            // Assegnazione dinamica degli ID
            nomeMateria.setId(id++);
            immagineData.setId(id++);
            dataAppello.setId(id++);
            immagineOra.setId(id++);
            oraAppello.setId(id++);
            dataAppello.setId(id++);
            aulaAppello.setId(id++);

            switch(app.getTipo()){
                case 0:
                    immagineTipo.setImageResource(R.drawable.ic_scritto);
                    break;
                case 1:
                    immagineTipo.setImageResource(R.drawable.ic_orale);
                    break;
                case 2:
                    immagineTipo.setImageResource(R.drawable.ic_pratico);
                    break;
                default:
                    break;
            }

            // Imposto il colore delle icone
            immagineTipo.setColorFilter(0x66F3F3F3);
            immagineData.setColorFilter(0xFF16A085);
            immagineOra.setColorFilter(0xFF16A085);
            immagineAula.setColorFilter(0xFF16A085);

            nomeMateria.setText(nome);
            immagineData.setImageResource(R.drawable.ic_menu_date);
            dataAppello.setText(data);
            immagineOra.setImageResource(R.drawable.ic_menu_clock);
            oraAppello.setText(ora);
            immagineAula.setImageResource(R.drawable.ic_location);
            aulaAppello.setText(aula);

            rl.setBackgroundColor(0xffFFFFFF);
            rl.setPadding(20, 20, 20, 20);
            immagineData.setPadding(10, 25, -10, 0);
            immagineOra.setPadding(10, 25, -10, 0);
            immagineAula.setPadding(10, 25, -10, 0);
            dataAppello.setPadding(20, 20, 20, 20);
            oraAppello.setPadding(20, 20, 20, 20);
            aulaAppello.setPadding(20, 20, 20, 20);
            nomeMateria.setTextSize(24);
            dataAppello.setTextSize(16);
            oraAppello.setTextSize(16);
            aulaAppello.setTextSize(16);
            nomeMateria.setPadding(20, 20, 20, 20);


            // Relative Layout
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(15, 15, 15, 5);

            ll.addView(rl, lp);

            // Immagine tipo
            RelativeLayout.LayoutParams lpTipoImm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpTipoImm.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lpTipoImm.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            lpTipoImm.setMarginEnd(15);
            lpTipoImm.height = 150;
            lpTipoImm.width = 150;

            rl.addView(immagineTipo, lpTipoImm);

            // Nome
            RelativeLayout.LayoutParams lpNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            rl.addView(nomeMateria, lpNome);

            // Immagine data
            RelativeLayout.LayoutParams lpDataImm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpDataImm.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpDataImm.height = 80;
            lpDataImm.width = 80;

            rl.addView(immagineData, lpDataImm);

            // Data
            RelativeLayout.LayoutParams lpData = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpData.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpData.addRule(RelativeLayout.RIGHT_OF, immagineData.getId());

            rl.addView(dataAppello, lpData);

            // Immagine ora
            RelativeLayout.LayoutParams lpOraImm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpOraImm.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpOraImm.addRule(RelativeLayout.RIGHT_OF, dataAppello.getId());
            lpOraImm.height = 75;
            lpOraImm.width = 75;

            if(!ora.equals("00:00")) rl.addView(immagineOra, lpOraImm);

            // ora
            RelativeLayout.LayoutParams lpOra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpOra.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpOra.addRule(RelativeLayout.RIGHT_OF, immagineOra.getId());

            if(!ora.equals("00:00")) rl.addView(oraAppello, lpOra);

            // Immagine Aula
            RelativeLayout.LayoutParams lpAulaImm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpAulaImm.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpAulaImm.addRule(RelativeLayout.LEFT_OF, aulaAppello.getId());
            lpAulaImm.height = 80;
            lpAulaImm.width = 80;

            if(!aula.equals("")) rl.addView(immagineAula, lpAulaImm);

            // Aula
            RelativeLayout.LayoutParams lpAula = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpAula.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpAula.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            if(!aula.equals("")) rl.addView(aulaAppello, lpAula);


            rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.option_array_appello, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // Modifica
                                    modificaAppello_intent = new Intent(getContext(), ModificaAppello.class);
                                    modificaAppello_intent.putExtra("appello", app.getID().toString()); //Optional parameters
                                    getContext().startActivity(modificaAppello_intent);
                                    break;
                                case 1:
                                    // Crea un dialog per visualizzare una previsione sull'andamento della media in base
                                    // al voto ottenuto ed i crediti della materia dell'appello
                                    mostraPrevisione(v, app.getIdMateria());
                                    break;
                                case 2:
                                    deleteAppelloDialog(v,app.getID());
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

    // Funzione per arrotondare un dobule ._."
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Funzione per visualizzare un dialog
    public void mostraPrevisione(final View v, Integer idMat){
        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.dialog_previsioni);

        TableLayout tl = (TableLayout) dialog.findViewById(R.id.previsioni_content);

        // Imposto il titolo
        dialog.setTitle("Previsione media");

        // Itero per le previsioni e mostro il risutlato
        ArrayList<Previsione> previsioni = Libretto.getPrevisione(getContext(), idMat);

        Integer id = 0;
        Double voto_temp;


        // Per ogni previsione genera un elemento del layout e lo aggiunge
        for(Previsione p: previsioni){

            // Crea una riga della tabella e la aggiune alla tabella
            TableRow tr = new TableRow(getContext());

            // Ritrovo il valore della larghezza in dp
            Integer width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());

            TableLayout.LayoutParams trlp = new TableLayout.LayoutParams(width, TableLayout.LayoutParams.WRAP_CONTENT);
            tl.addView(tr, trlp);

            // VOTO

            TextView voto = new TextView(getContext());
            voto.setId(id++);
            voto.setGravity(Gravity.CENTER);

            if(p.getVoto() == Libretto.getValoreLode()){
                voto.setText("30 lode");
            } else {
                voto.setText(p.getVoto().toString());
            }

            TableRow.LayoutParams votolp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            tr.addView(voto, votolp);

            // NUOVA MEDIA

            TextView nuova_media = new TextView(getContext());
            Double nuova_media_temp = round(p.getNuovaMedia(),2);
            nuova_media.setText(nuova_media_temp.toString());
            nuova_media.setId(id++);
            nuova_media.setGravity(Gravity.CENTER);

            TableRow.LayoutParams nuovamedialp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            tr.addView(nuova_media, nuovamedialp);

            // VARIAZIONE MEDIA

            TextView var_media = new TextView(getContext());
            var_media.setId(id++);
            var_media.setGravity(Gravity.CENTER);

            if(p.getVariazione() < 0.0) {
                var_media.setText(Double.toString(p.getVariazione()));
                var_media.setTextColor(0xffc0392b);
            } else {
                var_media.setText("+" + Double.toString(p.getVariazione()));
                var_media.setTextColor(0xff27ae60);
            }

            TableRow.LayoutParams varmedialp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            tr.addView(var_media, varmedialp);
        }

        dialog.show();
    }

    public void deleteAppelloDialog(final View view, final Integer id){
        new AlertDialog.Builder(getContext())
                .setTitle("Elimina appello")
                .setMessage("Sei sicuro di voler eliminare questo appello?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        apRepo.delete(id);
                        Toast.makeText(getActivity(), (String)"Appello eliminato!",
                                Toast.LENGTH_LONG).show();
                        refreshData(view);
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Controlla se esistono materie nel database e
        if(!matRepo.getListaMaterie().isEmpty()) {
            text.setVisibility(View.INVISIBLE);
            refreshData(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
