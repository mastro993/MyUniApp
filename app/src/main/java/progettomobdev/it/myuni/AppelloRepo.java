package progettomobdev.it.myuni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico on 06/04/2016.
 * Classe per la gestione del DB degli appello
 */
public class AppelloRepo {
    private DBHandler dbHelper;

    private Context context;

    public AppelloRepo(Context context) {
        dbHelper = new DBHandler(context);
        this.context = context;
    }

    // Inserisce un nuovo appello
    public int insert(Appello appello) {
        // Apertura connessine per la scrittura dei dati
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Appello.KEY_IDMateria, appello.getIdMateria());
        values.put(Appello.KEY_tipo, appello.getTipo());
        values.put(Appello.KEY_aula, appello.getAula());
        values.put(Appello.KEY_data, appello.getData().toString());

        // Inserimento riga e ritorna l'id ottenuto nel db
        long idAppello = db.insert(Appello.TABLE, null, values);
        db.close();

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);

        return (int) idAppello;
    }

    // Elimina un appello nel db con l'id dato
    public void delete(int idAppello) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Appello.TABLE, Appello.KEY_ID + "= ?", new String[]{String.valueOf(idAppello)});
        db.close(); // Chiusura connessione al database

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Aggiorna l'appello dato
    public void update(Appello appello) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Appello.KEY_IDMateria, appello.getIdMateria());
        values.put(Appello.KEY_tipo, appello.getTipo());
        values.put(Appello.KEY_aula, appello.getAula());
        values.put(Appello.KEY_data, appello.getData().toString());
        values.put(Appello.KEY_ID, appello.getID().toString());

        db.update(Appello.TABLE, values, Appello.KEY_ID + "= ?", new String[]{String.valueOf(appello.getID())});
        db.close(); // Closing database connection

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Ottiene la lista degli appelli in ordine cronologico
    public ArrayList<Appello> getListaAppelli() {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Appello.KEY_ID + "," +
                Appello.KEY_IDMateria + "," +
                Appello.KEY_tipo + "," +
                Appello.KEY_aula + "," +
                Appello.KEY_data +
                " FROM " + Appello.TABLE + " ORDER BY " + Appello.KEY_data;

        ArrayList<Appello> listaAppelli = new ArrayList<Appello>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge gli appelli alla lista
        if (cursor.moveToFirst()) {
            do {
                Appello app = new Appello();
                app.setID(cursor.getInt(cursor.getColumnIndex(Appello.KEY_ID)));
                app.setIdMateria(cursor.getInt(cursor.getColumnIndex(Appello.KEY_IDMateria)));
                app.setTipo(cursor.getInt(cursor.getColumnIndex(Appello.KEY_tipo)));
                app.setAula(cursor.getString(cursor.getColumnIndex(Appello.KEY_aula)));
                app.setDate(cursor.getString(cursor.getColumnIndex(Appello.KEY_data)));
                listaAppelli.add(app);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaAppelli;
    }

    // Ottiene la lista degli appelli in ordine cronologico di una data materia
    public ArrayList<HashMap<String, String>> getListaAppelliByMateria(int IDMateria) {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Appello.KEY_ID + "," +
                Appello.KEY_IDMateria + "," +
                Appello.KEY_tipo + "," +
                Appello.KEY_aula + "," +
                Appello.KEY_data +
                " FROM " + Appello.TABLE +
                " WHERE " + Appello.KEY_IDMateria + "=" + IDMateria +
                " ORDER BY " + Appello.KEY_data;

        ArrayList<HashMap<String, String>> listaAppelli = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge gli appelli alla lista
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> appello = new HashMap<String, String>();
                appello.put("id", cursor.getString(cursor.getColumnIndex(Appello.KEY_ID)));
                appello.put("idMateria", cursor.getString(cursor.getColumnIndex(Appello.KEY_IDMateria)));
                appello.put("tipo", cursor.getString(cursor.getColumnIndex(Appello.KEY_IDMateria)));
                appello.put("aula", cursor.getString(cursor.getColumnIndex(Appello.KEY_aula)));
                appello.put("data", cursor.getString(cursor.getColumnIndex(Appello.KEY_data)));
                listaAppelli.add(appello);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaAppelli;
    }

    // Ottiene un appello dando il suo ID
    public Appello getAppelloByID(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Appello.KEY_ID + "," +
                Appello.KEY_IDMateria + "," +
                Appello.KEY_tipo + "," +
                Appello.KEY_aula + "," +
                Appello.KEY_data +
                " FROM " + Appello.TABLE
                + " WHERE " +
                Appello.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount = 0;

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});
        Appello appello = new Appello();

        if (cursor.moveToFirst()) {
            do {
                appello.setID(cursor.getInt(cursor.getColumnIndex(Appello.KEY_ID)));
                appello.setIdMateria(cursor.getInt(cursor.getColumnIndex(Appello.KEY_IDMateria)));
                appello.setTipo(cursor.getInt(cursor.getColumnIndex(Appello.KEY_tipo)));
                appello.setDate(cursor.getString(cursor.getColumnIndex(Appello.KEY_data)));
                appello.setAula(cursor.getString(cursor.getColumnIndex(Appello.KEY_aula)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return appello;
    }

    // Ottiene la lista degli appelli senza esame già passato
    public ArrayList<Appello> getListaAppelliNoEsame() {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Appello.KEY_ID + "," +
                Appello.KEY_IDMateria + "," +
                Appello.KEY_tipo + "," +
                Appello.KEY_aula + "," +
                Appello.KEY_data +
                " FROM " + Appello.TABLE +
                " WHERE " + Appello.KEY_IDMateria + " NOT IN (" +
                "SELECT " + Esame.KEY_IDMateria + " FROM " + Esame.TABLE + ")" +
                " ORDER BY " + Appello.KEY_data;

        ArrayList<Appello> listaAppelli = new ArrayList<Appello>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge gli appelli alla lista
        if (cursor.moveToFirst()) {
            do {
                Appello app = new Appello();
                app.setID(cursor.getInt(cursor.getColumnIndex(Appello.KEY_ID)));
                app.setIdMateria(cursor.getInt(cursor.getColumnIndex(Appello.KEY_IDMateria)));
                app.setTipo(cursor.getInt(cursor.getColumnIndex(Appello.KEY_tipo)));
                app.setAula(cursor.getString(cursor.getColumnIndex(Appello.KEY_aula)));
                app.setDate(cursor.getString(cursor.getColumnIndex(Appello.KEY_data)));
                listaAppelli.add(app);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaAppelli;
    }
}
