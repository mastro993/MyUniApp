package progettomobdev.it.myuni;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class LibrettoFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    Intent nuovoEsame_intent, modificaEsame_intent;
    View view;
    MateriaRepo mrepo;
    EsameRepo erepo;
    TextView text;

    public LibrettoFragment() {
        // Required empty public constructor
    }

    public static LibrettoFragment newInstance(String param1, String param2) {
        LibrettoFragment fragment = new LibrettoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_libretto, container, false);
        text = (TextView) view.findViewById(R.id.no_esami);
        erepo = new EsameRepo(this.getContext());
        mrepo = new MateriaRepo(this.getContext());

        // Floating button per aggiungere un esame
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_aggiungi_esami);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuovoEsame_intent = new Intent(getContext(), AggiungiEsame.class);
                getContext().startActivity(nuovoEsame_intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Mostra la lista esami se non è vuota
        if(!erepo.getListaEsami().isEmpty()){
            text.setVisibility(View.INVISIBLE);
            refreshData(view);
        } else {
            text.setVisibility(View.VISIBLE);
        }
    }


    // Funzione utilizzata per refreshare la lista degli esami nel libretto
    @TargetApi(21)
    public void refreshData(final View view) {

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.esami_container);
        // In caso di refresh elimina views figlie dal layout
        linearLayout.removeAllViews();

        // Itera per ogni esame nella lista esami e li aggiunge al layout
        for (final Esame e : erepo.getListaEsami()) {

            // Attributi esame
            Integer voto = e.getVoto();
            Integer crediti = mrepo.getMateriaByID(e.getIdMateria()).getCrediti();
            String nome = mrepo.getMateriaByID(e.getIdMateria()).getNome();
            String data = new SimpleDateFormat("dd/MM/yyyy").format(e.getData());

            // Views
            RelativeLayout rl = new RelativeLayout(this.getContext());
            rl.setBackgroundColor(0xffFFFFFF);
            rl.setPadding(20, 20, 20, 20);

            if(Build.VERSION.SDK_INT >= 21){
                rl.setTranslationZ(2f);
            }

            TextView nomeMateria = new TextView(this.getContext());
            nomeMateria.setId(e.getId());
            nomeMateria.setText(nome);
            nomeMateria.setPadding(20, 20, 20, 20);
            nomeMateria.setTextSize(24);

            ImageView immagineData = new ImageView(this.getContext());
            immagineData.setId(e.getId() * 100);
            immagineData.setImageResource(R.drawable.ic_menu_date);
            immagineData.setPadding(10, 25, -10, 0);
            immagineData.setColorFilter(0xFF16A085);

            TextView dataMateria = new TextView(this.getContext());
            dataMateria.setId(e.getId() * 1000);
            dataMateria.setText(data);
            dataMateria.setPadding(20, 20, 20, 20);
            dataMateria.setTextSize(16);

            ImageView immagineCrediti = new ImageView(this.getContext());
            immagineCrediti.setId(e.getId() * 10000);
            immagineCrediti.setImageResource(R.drawable.ic_menu_credits);
            immagineCrediti.setPadding(10, 25, -10, 0);
            immagineCrediti.setColorFilter(0xFF16A085);

            TextView creditiMateria = new TextView(this.getContext());
            creditiMateria.setId(e.getId() * 100000);
            creditiMateria.setText(crediti.toString());
            creditiMateria.setPadding(20, 20, 20, 20);
            creditiMateria.setTextSize(16);

            TextView votoMateria = new TextView(this.getContext());
            if (voto == Libretto.getValoreLode()) {
                votoMateria.setText("30L");
            } else if (voto == 0 ){
                votoMateria.setText("ID");
            } else {
                votoMateria.setText(voto.toString());
            }
            votoMateria.setPadding(20, 20, 20, 20);
            votoMateria.setTextSize(48);
            // Cambia il colore in base al voto
            if (voto == 0){
                votoMateria.setTextColor(0xff27ae60);
            } else if (voto <= 20) {
                votoMateria.setTextColor(0xffc0392b);
            } else if (voto <= 25) {
                votoMateria.setTextColor(0xfff1c40f);
            } else if (voto <= 30) {
                votoMateria.setTextColor(0xff27ae60);
            } else {
                votoMateria.setTextColor(0xff2980b9);
            }

            votoMateria.setId(e.getId() * 1000000);

            // Parametri
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(15, 15, 15, 5);

            RelativeLayout.LayoutParams layoutParamsNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsNome.addRule(RelativeLayout.LEFT_OF, e.getId() * 1000000);

            RelativeLayout.LayoutParams layoutParamsDataImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsDataImage.addRule(RelativeLayout.BELOW, e.getId());
            layoutParamsDataImage.height = 80;
            layoutParamsDataImage.width = 80;

            RelativeLayout.LayoutParams layoutParamsData = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsData.addRule(RelativeLayout.BELOW, e.getId());
            layoutParamsData.addRule(RelativeLayout.RIGHT_OF, e.getId() * 100);

            RelativeLayout.LayoutParams layoutParamsCreditiImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsCreditiImage.addRule(RelativeLayout.BELOW, e.getId());
            layoutParamsCreditiImage.addRule(RelativeLayout.RIGHT_OF, e.getId() * 1000);
            layoutParamsCreditiImage.height = 80;
            layoutParamsCreditiImage.width = 80;

            RelativeLayout.LayoutParams layoutParamsCrediti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsCrediti.addRule(RelativeLayout.BELOW, e.getId());
            layoutParamsCrediti.addRule(RelativeLayout.RIGHT_OF, e.getId() * 10000);

            RelativeLayout.LayoutParams layoutParamsVoto = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsVoto.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

            // Aggiunta views
            linearLayout.addView(rl, layoutParams);
            rl.addView(votoMateria, layoutParamsVoto);
            rl.addView(nomeMateria, layoutParamsNome);
            rl.addView(dataMateria, layoutParamsData);
            rl.addView(immagineData, layoutParamsDataImage);
            rl.addView(creditiMateria, layoutParamsCrediti);
            rl.addView(immagineCrediti, layoutParamsCreditiImage);

            rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.option_array_libretto, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0://Condividi
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Ho superato " + mrepo.getMateriaByID(e.getIdMateria()).getNome() +
                                                                            " con voto " + e.getVoto()+ ".\n\n\nPublicato utilizzando #MyUniapp.");
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);
                                    break;

                                case 1: // Modifica
                                    modificaEsame_intent = new Intent(getContext(), ModificaEsame.class);
                                    modificaEsame_intent.putExtra("esame", e.getId().toString()); //Optional parameters
                                    getContext().startActivity(modificaEsame_intent);
                                    break;
                                case 2:
                                    deleteEsameDialog(view, e.getId());
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

    // Dialog per eliminazione esame
    public void deleteEsameDialog(final View view, final Integer id) {
        new AlertDialog.Builder(getContext())
                .setTitle("Elimina esame")
                .setMessage("Sei sicuro di voler eliminare questo esame?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EsameRepo erepo = new EsameRepo(getContext());
                        erepo.delete(id);
                        Toast.makeText(getActivity(), (String) "Esame eliminato!",
                                Toast.LENGTH_LONG).show();
                        refreshData(view);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
