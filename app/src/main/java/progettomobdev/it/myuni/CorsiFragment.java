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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CorsiFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CorsiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CorsiFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    MateriaRepo matRepo;
    LinearLayout ll;
    View view;
    TextView text;
    FloatingActionButton fab;

    Integer ID_materia;

    public CorsiFragment() {
        // Required empty public constructor
    }

    public static CorsiFragment newInstance(String param1, String param2) {
        CorsiFragment fragment = new CorsiFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_corsi, container, false);
        ll = (LinearLayout) view.findViewById(R.id.corsi_container);
        text = (TextView) view.findViewById(R.id.no_corsi);

        matRepo = new MateriaRepo(getContext());

        fab = (FloatingActionButton) view.findViewById(R.id.fab_aggiungi_corsi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovoCorso(getContext());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Controlla se esistono materie nel database e le mostra
        if(!matRepo.getListaMaterie().isEmpty()) {
            text.setVisibility(View.INVISIBLE);
            refreshData(view);
        } else {
            Log.d("MATERIE","Nessuna materia nel database");
        }
    }

    // Dialog per l'aggiunta della materia
    public void nuovoCorso(Context context){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.aggiungi_materia_dialog);
        dialog.setTitle("Nuovo corso");

        // set the custom dialog components - text, image and button
        final EditText nuovoNome = (EditText) dialog.findViewById(R.id.nuovo_nome_materia);
        final EditText nuovoCrediti = (EditText) dialog.findViewById(R.id.crediti_nuova_materia);
        final EditText email = (EditText) dialog.findViewById(R.id.email_prof);
        Button save = (Button) dialog.findViewById(R.id.aggiungi_button);
        Button cancel = (Button) dialog.findViewById(R.id.cancella_button);

        // if button is clicked, close the custom dialog
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Controllo dei contenuti
                if(Objects.equals(nuovoNome.getText().toString(), "")){
                    Toast.makeText(getActivity(), (String) "Devi inserire un nome al nuovo corso!",
                            Toast.LENGTH_LONG).show();
                } else if (Objects.equals(nuovoCrediti.getText().toString(), "")){
                    Toast.makeText(getActivity(), (String) "Devi inserire il valore in crediti del corso!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Materia materia = new Materia(nuovoNome.getText().toString(),
                            Integer.parseInt(nuovoCrediti.getText().toString()));
                    String check = email.getText().toString();
                    if(!email.getText().toString().equals(""))
                        materia.setEmail(email.getText().toString());

                    // Inserimento materia
                    ID_materia = matRepo.insert(materia);

                    Log.d("NUOVA MATERIA", "ID: " + ID_materia + " Nome: " + materia.getNome().toString() + " Crediti: " + materia.getCrediti().toString());

                    Toast.makeText(getActivity(), (String) materia.getNome() + " aggiunto!",
                            Toast.LENGTH_LONG).show();

                    refreshData(v);

                    dialog.dismiss();
                }
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

    // Ripopola la lista
    public void refreshData(final View view) {

        ll.removeAllViews(); // Elimina eventuali view figlie già presenti

        // Integer per ID dinamici
        Integer ID = 1;

        for (final Materia mat : matRepo.getListaMaterie()) {

            // Attributi
            final String nome = mat.getNome(), email= mat.getEmail();
            final Integer id = mat.getID(), crediti = mat.getCrediti();

            // Views
            RelativeLayout rl = new RelativeLayout(this.getContext());
            TextView nomeMateria = new TextView(this.getContext());
            ImageView immagineCrediti = new ImageView(this.getContext());
            TextView creditiMateria = new TextView(this.getContext());
            ImageView immagineEmail = new ImageView(this.getContext());
            TextView emailProf = new TextView(this.getContext());

            // Se API >= 21 imposta una traslazione sull'asse z
            if(Build.VERSION.SDK_INT >= 21){
                rl.setTranslationZ(2f);
            }

            // Assegnazione ID dinamica
            nomeMateria.setId(ID++);
            immagineCrediti.setId(ID++);
            creditiMateria.setId(ID++);
            immagineEmail.setId(ID++);
            emailProf.setId(ID++);

            // Contenuto
            nomeMateria.setText(nome);
            creditiMateria.setText(crediti.toString());
            emailProf.setText(email);

            // Imposto il colore e le icone
            immagineCrediti.setImageResource(R.drawable.ic_menu_credits);
            immagineCrediti.setColorFilter(0xFF16A085);
            immagineEmail.setImageResource(R.drawable.ic_email);
            immagineEmail.setColorFilter(0xFF16A085);

            // Altri attributi
            rl.setBackgroundColor(0xffFFFFFF);
            rl.setPadding(20, 20, 20, 20);
            nomeMateria.setPadding(20, 20, 20, 20);
            nomeMateria.setTextSize(24);
            immagineCrediti.setPadding(10, 25, -10, 0);
            creditiMateria.setPadding(20, 20, 20, 20);
            creditiMateria.setTextSize(16);
            immagineEmail.setPadding(10, 25, -10, 0);
            emailProf.setPadding(20, 20, 20, 20);
            emailProf.setTextSize(16);

            // Parametri layout

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(15, 15, 15, 5);

            ll.addView(rl, lp);

            RelativeLayout.LayoutParams lpNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            rl.addView(nomeMateria, lpNome);

            RelativeLayout.LayoutParams lpCreditiImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpCreditiImage.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpCreditiImage.height = 80;
            lpCreditiImage.width = 80;

            rl.addView(immagineCrediti, lpCreditiImage);

            RelativeLayout.LayoutParams lpCrediti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpCrediti.addRule(RelativeLayout.BELOW, nomeMateria.getId());
            lpCrediti.addRule(RelativeLayout.RIGHT_OF, immagineCrediti.getId());

            rl.addView(creditiMateria, lpCrediti);

            if(email != null){
                RelativeLayout.LayoutParams lpEmailImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpEmailImage.addRule(RelativeLayout.BELOW, nomeMateria.getId());
                lpEmailImage.addRule(RelativeLayout.RIGHT_OF, creditiMateria
                        .getId());
                lpEmailImage.height = 80;
                lpEmailImage.width = 80;

                rl.addView(immagineEmail, lpEmailImage);

                RelativeLayout.LayoutParams lpEmail = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpEmail.addRule(RelativeLayout.BELOW, nomeMateria.getId());
                lpEmail.addRule(RelativeLayout.RIGHT_OF, immagineEmail.getId());

                rl.addView(emailProf, lpEmail);
            }

            rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.option_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:// Mail a docente
                                    mailToProf(v,mat);
                                    break;
                                case 1: // Modifica
                                    editCorsoDialog(v,mat);
                                    break;
                                case 2:
                                    deleteCorsoDialog(v,id);
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

    // Mostra il dialog per modifica la materia
    public void editCorsoDialog(final View view, final Materia mat){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.modifica_materia_layout);
        dialog.setTitle("Modifica materia");

        final EditText nuovoNome = (EditText) dialog.findViewById(R.id.nuovo_nome_materia);
        final EditText nuovoCrediti = (EditText) dialog.findViewById(R.id.crediti_nuova_materia);
        final EditText email = (EditText) dialog.findViewById(R.id.email_prof);
        Button save = (Button) dialog.findViewById(R.id.modifica_button);
        Button cancel = (Button) dialog.findViewById(R.id.cancella_button);

        nuovoNome.setText(mat.getNome());
        nuovoCrediti.setText(mat.getCrediti().toString());
        if(mat.getEmail() != null)
            email.setText(mat.getEmail());

        // if button is clicked, close the custom dialog
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {

                Materia materia = new Materia();
                materia.setID(mat.getID());
                materia.setNome(nuovoNome.getText().toString());
                materia.setCrediti(Integer.parseInt(nuovoCrediti.getText().toString()));
                if(!email.getText().toString().equals(""))
                    materia.setEmail(email.getText().toString());

                matRepo.update(materia);

                Toast.makeText(getActivity(), (String) materia.getNome() + " modificata!",
                        Toast.LENGTH_LONG).show();
                refreshData(view);

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

    public void deleteCorsoDialog(final View view, final Integer id){
        new AlertDialog.Builder(getContext())
                .setTitle("Elimina corso")
                .setMessage("Eliminando un corso eliminerai qualsiasi elemento a cui fa riferimento, come appelli, esami e lezioni. Sicuro di voler procedere?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        matRepo.delete(id);
                        Toast.makeText(getActivity(), (String)"Corso eliminato!",
                                Toast.LENGTH_LONG).show();
                        refreshData(view);
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void mailToProf(View v, Materia mat){
        if(mat.getEmail()!= null) {

            /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setData(Uri.parse(mat.getEmail()));
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "\n \n \n Mandato da MyUni");
            startActivity(sendIntent);*/


            // Mostra la selezione delle app, in caso non si avesse Gmail
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
            emailIntent.setType("application/octet-stream");
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mat.getEmail()});
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n \n \n Mandato da MyUni");
            startActivity(Intent.createChooser(emailIntent, "Contatta il docente"));

        }
        else
            Toast.makeText(getActivity(), "Imposta prima mail docente!",
                    Toast.LENGTH_LONG).show();
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
