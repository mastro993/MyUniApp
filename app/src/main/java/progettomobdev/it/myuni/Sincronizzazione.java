package progettomobdev.it.myuni;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;

public class Sincronizzazione extends AppCompatActivity {

    final static public String DROPBOX_APP_KEY = "jh5b8qh80vez78k";
    final static public String DROPBOX_APP_SECRET = "yemx7na5qymgmid";
    private static DropboxAPI<AndroidAuthSession> mDBApi;
    private final static Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    public static String ACCESS_TOKEN = null;

    public static AppKeyPair appKeys;
    public static AndroidAuthSession session;

    static RelativeLayout dropbox_container, stato_sync_container;
    static TextView no_account, email_account, sync_status;
    static Button aggiungi_account, sincronizza_ora;
    static ImageView elimina_account;
    static RelativeLayout account_loading, sync_loading;

    static OutputStream out;
    static File in;
    static Integer dbUpdates;

    static SharedPreferences DBprefs;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizzazione);
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

        // Salva il context in una variabile pubblica
        context = Sincronizzazione.this;

        // Shared Preferences
        DBprefs = getSharedPreferences("progettomobdev.it.myuni.DBoxAccess", 0);

        account_loading = (RelativeLayout) findViewById(R.id.dropboax_account_loading);
        sync_loading = (RelativeLayout) findViewById(R.id.dropboax_sync_loading);

        // Views
        dropbox_container = (RelativeLayout) findViewById(R.id.account_container);
        stato_sync_container = (RelativeLayout) findViewById(R.id.stato_sync_container);

        // Chiavi API
        appKeys = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);

        // Controlla se esiste una sessione (Access Token) salvata
        if (checkToken()) {
            dropbox_container.removeAllViews();
            // Se esiste la carica dalle Shared Prefs
            ACCESS_TOKEN = loadAccessToken();
            // Carica i dati dell'utente e li visualizza nel view per una rapida gestione
            getAccountInfo(ACCESS_TOKEN);
        } else {
            noAccountsView();
        }
    }

    // Imposta la view senza accounts
    private static void noAccountsView() {

        dropbox_container.removeAllViews();
        stato_sync_container.setVisibility(View.GONE);
        account_loading.setVisibility(View.GONE);

        no_account = new TextView(context);
        no_account.setText(R.string.no_accounts);
        no_account.setId(dropbox_container.getId() + 1);
        no_account.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        no_account.setPadding(0, 0, 0, 20);

        RelativeLayout.LayoutParams no_account_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        no_account_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        dropbox_container.addView(no_account, no_account_lp);

        aggiungi_account = new Button(context);
        aggiungi_account.setText("Aggiungi un account");

        RelativeLayout.LayoutParams aggiungi_account_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        aggiungi_account_lp.addRule(RelativeLayout.BELOW, no_account.getId());
        aggiungi_account_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        dropbox_container.addView(aggiungi_account, aggiungi_account_lp);

        aggiungi_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Autenticazione Dropbox
                session = new AndroidAuthSession(appKeys);
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().startOAuth2Authentication(context);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi != null) {
            // Controlla l'esistenza di un autenticazione in corso e la conclude salvando la sessione
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Fa il bind dell'auth con la sessione
                    mDBApi.getSession().finishAuthentication();
                    // Salva l'access token per non dover ogni volta rifare il login
                    ACCESS_TOKEN = mDBApi.getSession().getOAuth2AccessToken();
                    storeToken(ACCESS_TOKEN);
                    // Preleva i dati dell'account e li visualizza
                    getAccountInfo(ACCESS_TOKEN);
                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }
            }
        }
    }

    // Crea lw View per la gestione dell'account
    private static void creaInfoAccount(FullAccount account) {

        if (account != null) {
            dropbox_container.removeAllViews();

            email_account = new TextView(context);
            email_account.setText(account.getEmail());
            email_account.setId(dropbox_container.getId() + 2);
            email_account.setPadding(0, 0, 0, 20);
            email_account.setTextSize(16);

            RelativeLayout.LayoutParams email_account_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            dropbox_container.addView(email_account, email_account_lp);

            elimina_account = new ImageView(context);
            elimina_account.setImageResource(R.drawable.ic_clear);

            RelativeLayout.LayoutParams elimina_account_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            elimina_account_lp.addRule(RelativeLayout.ALIGN_PARENT_END, email_account.getId());

            dropbox_container.addView(elimina_account, elimina_account_lp);

            elimina_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle("Rimuovi account")
                            .setMessage("Sei sicuro di voler eliminare l'account Dropbox associato? ")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SharedPreferences.Editor rm = DBprefs.edit();
                                    rm.remove("AccessSecretKey");
                                    rm.apply();
                                    noAccountsView();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            stato_sync_container.setVisibility(View.VISIBLE);
            getSyncStatus(ACCESS_TOKEN);

        } else {
            Toast.makeText(context, "Impossibile ottenere le informazioni del profilo.", Toast.LENGTH_SHORT).show();
            dropbox_container.removeAllViews();
            email_account = new TextView(context);
            email_account.setText("Impossibile ottenere le informazioni del profilo utente associato. Controlla lo stato della connessione dati e riprova.");
            email_account.setId(dropbox_container.getId() + 2);
            email_account.setPadding(0, 0, 0, 20);
            email_account.setTextSize(16);

            RelativeLayout.LayoutParams email_account_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            dropbox_container.addView(email_account, email_account_lp);

            sincronizza_ora = new Button(context);
            sincronizza_ora.setText("Riprova");

            RelativeLayout.LayoutParams sincronizza_ora_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            sincronizza_ora_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            sincronizza_ora_lp.addRule(RelativeLayout.BELOW, email_account.getId());
            sincronizza_ora_lp.setMargins(10, 10, 10, 0);

            dropbox_container.addView(sincronizza_ora, sincronizza_ora_lp);

            sincronizza_ora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkToken()) {
                        dropbox_container.removeAllViews();
                        // Se esiste la carica dalle Shared Prefs
                        ACCESS_TOKEN = loadAccessToken();
                        // Carica i dati dell'utente e li visualizza nel view per una rapida gestione
                        getAccountInfo(ACCESS_TOKEN);
                    } else {
                        noAccountsView();
                    }
                }
            });
        }
    }

    // Crea le view per la gestione dello stato del sync
    private static void creaStatoSync(Integer dbupdates){
        Integer localUpdates = DBHandler.getDatabaseUpdates(context);
        Log.d("LOCAL SYNC", localUpdates.toString());


        stato_sync_container.removeAllViews();

        sync_status = new TextView(context);
        sync_status.setId(stato_sync_container.getId() + 1);
        sync_status.setId(dropbox_container.getId() + 2);
        sync_status.setPadding(0, 0, 0, 20);
        sync_status.setTextSize(16);
        sync_status.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        RelativeLayout.LayoutParams sync_status_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        sincronizza_ora = new Button(context);
        sincronizza_ora.setText("Sincronizza");

        RelativeLayout.LayoutParams sincronizza_ora_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sincronizza_ora_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        sincronizza_ora_lp.addRule(RelativeLayout.BELOW, sync_status.getId());
        sincronizza_ora_lp.setMargins(10, 10, 10, 0);

        sincronizza_ora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizza_ora.setEnabled(false);
                Sincronizzazione.sincronizza_ora.setText("Sincronizzazione in corso");
                Intent intent = new Intent(context, SyncService.class);
                context.startService(intent);
            }
        });

        if(dbupdates==DBHandler.getDatabaseUpdates(context)){
            sync_status.setText("Il tuo profilo MyUni è correttamente sincronizzato con il tuo account DropBox");
            sync_status.setTextColor(context.getResources().getColor(R.color.ottimo ));
            stato_sync_container.addView(sync_status, sync_status_lp);
        } else if(dbupdates>DBHandler.getDatabaseUpdates(context)){
            sync_status.setText("Il tuo profilo MyUni non è sincronizzato, clicca su \"Sincronizza\" per scaricare una versione più recente dei tuoi dati.");
            stato_sync_container.addView(sync_status, sync_status_lp);
            stato_sync_container.addView(sincronizza_ora, sincronizza_ora_lp);
        } else {
            sync_status.setText("I file nell'account DropBox risalgono ad una versione meno recente dei dati del tuo profilo MyUni, clicca \"Sincronizza\" per caricare i dati più aggiornati.");
            stato_sync_container.addView(sync_status, sync_status_lp);
            stato_sync_container.addView(sincronizza_ora, sincronizza_ora_lp);
        }


    }

    // Salva l'auth key nelle shared preferences
    private void storeToken(String token) {
        if (token != null) {
            SharedPreferences.Editor edit = DBprefs.edit();
            edit.putString("AccessSecretKey", token);
            edit.apply();
        }
    }

    // Carica l'auth dalle shared preferences
    private static String loadAccessToken() {
        String token = DBprefs.getString("AccessSecretKey", null);
        return token;
    }

    // Controlla che vi sia una sessione salvata
    @NonNull
    private static Boolean checkToken() {
        String session = DBprefs.getString("AccessSecretKey", null);
        if (session != null) {
            return true;
        } else {
            return false;
        }
    }

    // Async Task per ottenere i dati dell'account Dropbox associato
    private static void getAccountInfo(String ACCESS_TOKEN) {
        class AccountData extends AsyncTask<String, Integer, FullAccount> {

            FullAccount account;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                account_loading.setVisibility(View.VISIBLE);
            }

            @Override
            protected FullAccount doInBackground(String... ACCESS_TOKEN) {
                DbxRequestConfig config = new DbxRequestConfig("dropbox/MyUniApp", "it_IT");
                DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN[0]);

                // Ottiene le info dell'account
                try {
                    account = client.users().getCurrentAccount();
                } catch (Exception e) {
                    Log.d("ACCOUNT", e.toString());
                }

                return account;
            }

            @Override
            protected void onPostExecute(FullAccount account) {
                account_loading.setVisibility(View.GONE);
                Sincronizzazione.creaInfoAccount(account); // Li mostra all'utente
            }
        }
        new AccountData().execute(ACCESS_TOKEN);
    }

    // Async Task per ottenere lo stato di sincronizzazione
    public static void getSyncStatus(String ACCESS_TOKEN) {
        class SyncData extends AsyncTask<String, Integer, Integer> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                sync_loading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(String... ACCESS_TOKEN) {

                // Avvia la sessione DropBox con l'access token ottenuto all'associazione dell'account
                session = new AndroidAuthSession(appKeys);
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().setOAuth2AccessToken(ACCESS_TOKEN[0]);

                // Scarica il file dbUpdates.txt
                try {
                    out = new BufferedOutputStream(context.openFileOutput("dbUpdates.txt", MODE_PRIVATE));
                    mDBApi.getFile("dbUpdates.txt", null, out, null);
                } catch (Exception e) {
                    Log.d("DB_FILE", e.toString());
                    dbUpdates = 0;
                }

                // Legge il file
                try {
                    in = context.getFileStreamPath("dbUpdates.txt");
                    BufferedReader br = new BufferedReader(new FileReader(in));
                    dbUpdates = Integer.parseInt(br.readLine());
                    br.close();
                } catch (Exception e) {
                    Log.d("LETTURA", e.toString());
                }
                Log.d("UPDATES", dbUpdates.toString());

                return dbUpdates;
            }

            @Override
            protected void onPostExecute(Integer dbupdates) {
                sync_loading.setVisibility(View.GONE);
                Sincronizzazione.creaStatoSync(dbupdates); // Li mostra all'utente
            }
        }
        new SyncData().execute(ACCESS_TOKEN);
    }
}
