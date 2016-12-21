package progettomobdev.it.myuni;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by Federico on 26/04/2016.
 */
public class SyncService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    private static DropboxAPI<AndroidAuthSession> mDBApi;
    static AndroidAuthSession session;


    DbxRequestConfig config;
    DbxClientV2 client;

    // Handler che riceve i messaggi dal thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            int id = 1; // a caso

            // Notifica l'avvio della sincronizzazione
            mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(SyncService.this);
            mBuilder.setContentTitle("MyUni")
                    .setContentText("Sincronizzazione in corso")
                    .setSmallIcon(R.drawable.ic_school);

            mBuilder.setProgress(5, 0, true);
            mNotifyManager.notify(id, mBuilder.build());

            Integer remote_updates = Sincronizzazione.dbUpdates;
            Integer local_updates = DBHandler.getDatabaseUpdates(SyncService.this);

            // Avvia la sessione DropBox con l'access token ottenuto all'associazione dell'account
            session = new AndroidAuthSession(Sincronizzazione.appKeys);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().setOAuth2AccessToken(Sincronizzazione.ACCESS_TOKEN);

            if (remote_updates < local_updates) {
                // Il file remoto è meno aggiornato quindi carico quello nuovo
                uploadDB(local_updates);
            } else {
                // Il file locale è meno aggiornato quindi scarico il nuovo file
                downloadDB(remote_updates);
            }


            // Notifica la fine della sincronizzazione
            mBuilder.setContentText("Sincronizzazione completata")
                    .setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());

            stopSelf(msg.arg1);
        }
    }

    private void uploadDB(Integer local_updates) {
        FileInputStream fis = null;
        FileOutputStream outputStream;
        File file;

        // Carico il database
        file = new File(getDatabasePath("myUni.db").toString());
        try {
            fis = new FileInputStream(file);

            try {
                mDBApi.delete("myUni.db");
            } catch (Exception e) {
                Log.w("ELIMINAZIONE DB", e.toString());
            }

            DropboxAPI.Entry newEntry = mDBApi.putFile("myUni.db", fis, file.length(), null, null);
            Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
        } catch (Exception e) {
            Log.d("DB UPLOAD", e.toString());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }

        // Creo il nuovo file dbUpdates.txt
        file = new File(getFilesDir().toString() + "/dbUpdates.txt");
        try {
            outputStream = new FileOutputStream(file);
            //outputStream = openFileOutput("dbUpdatesTemp.txt", Context.MODE_PRIVATE);
            outputStream.write(local_updates.toString().getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("CREAZIONE DBUPDATES", e.toString());
        }

        // Carico il file dbUpdates.txt aggiornato
        try {
            fis = new FileInputStream(file);

            try {
                mDBApi.delete("dbUpdates.txt");
            } catch (Exception e) {
                Log.d("ELIMINAZIONE DBUPDATES", e.toString());
            }

            mDBApi.putFile("dbUpdates.txt", fis, file.length(), null, null);
        } catch (Exception e) {
            Log.d("UPLOAD DBUPDATES", e.toString());
        }
    }

    private void downloadDB(Integer remote_updates) {
        // Scarico il database
        FileInputStream fis = null;
        FileOutputStream outputStream;
        File file;

        // Creo il nuovo file dbUpdates.txt
        file = new File(getDatabasePath("myUni.db").toString());
        try {
            outputStream = new FileOutputStream(file);
            mDBApi.getFile("myUni.db", null, outputStream, null);
            outputStream.close();
        } catch (Exception e) {
            Log.d("DOWNLOAD DATABASE", e.toString());
        }

        // Salvo localmente la versione del database
        DBHandler.setDatabaseUpdates(SyncService.this, remote_updates);
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Sincronizzazione avviata", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Nessun Binding quindi null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Sincronizzazione completata con successo!", Toast.LENGTH_SHORT).show();
        Sincronizzazione.sincronizza_ora.setText("Sincronizza");
        Sincronizzazione.sincronizza_ora.setEnabled(true);
        Sincronizzazione.getSyncStatus(Sincronizzazione.ACCESS_TOKEN);
    }


}
