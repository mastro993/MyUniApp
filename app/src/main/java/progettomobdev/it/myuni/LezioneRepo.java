package progettomobdev.it.myuni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Federico on 13/04/2016.
 */
public class LezioneRepo {
    private static DBHandler dbHelper;

    private Context context;

    public LezioneRepo(Context context) {
        dbHelper = new DBHandler(context);
        this.context = context;
    }

    // Inserisce un nuova lezione
    public int insert(Lezione lezione) {
        // Apertura connessine per la scrittura dei dati
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Lezione.KEY_IDmateria, lezione.getIDMateria());
        values.put(Lezione.KEY_orafine, lezione.getOraFine().toString());
        values.put(Lezione.KEY_orainizio, lezione.getOraInizio().toString());
        values.put(Lezione.KEY_giorno, lezione.getGiorno());
        values.put(Lezione.KEY_aula, lezione.getAula());

        // Inserimento riga e ritorna l'id ottenuto nel db
        long idLezione = db.insert(Lezione.TABLE, null, values);
        db.close();

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);

        return (int) idLezione;
    }

    // Elimina una lezione nel db con l'id dato
    public void delete(int idLezione) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Lezione.TABLE, Lezione.KEY_IDLezione + "= ?", new String[] { String.valueOf(idLezione) });
        db.close(); // Chiusura connessione al database

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Aggiorna lezione
    public void update(Lezione lezione) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Lezione.KEY_IDmateria, lezione.getIDMateria());
        values.put(Lezione.KEY_orafine, lezione.getOraFine().toString());
        values.put(Lezione.KEY_orainizio, lezione.getOraInizio().toString());
        values.put(Lezione.KEY_giorno, lezione.getGiorno());
        values.put(Lezione.KEY_aula, lezione.getAula());

        db.update(Lezione.TABLE, values, Lezione.KEY_IDLezione + "= ?", new String[]{String.valueOf(lezione.getIDLezione())});
        db.close(); // Closing database connection

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Ottiene la lista delle lezioni per giorno
    public static ArrayList<Lezione> getListaLezioni(String giorno) {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Lezione.KEY_IDLezione + "," +
                Lezione.KEY_IDmateria + "," +
                Lezione.KEY_orainizio + "," +
                Lezione.KEY_orafine + ","+
                Lezione.KEY_giorno + ","+
                Lezione.KEY_aula +
                " FROM " + Lezione.TABLE +
                " WHERE " + Lezione.KEY_giorno +
                " = '" + giorno + "' ORDER BY "
                + Lezione.KEY_orainizio;


        ArrayList<Lezione> listaLezioni = new ArrayList<Lezione>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge  lezione alla lista
        if (cursor.moveToFirst()) {
            do {
                Lezione lezione = new Lezione();
                lezione.setIDMateria(cursor.getInt(cursor.getColumnIndex(Lezione.KEY_IDmateria)));
                lezione.setOraInizio(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orainizio)));
                lezione.setOraFine(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orafine)));
                lezione.setGiorno(cursor.getString(cursor.getColumnIndex(Lezione.KEY_giorno)));
                lezione.setAula(cursor.getString(cursor.getColumnIndex(Lezione.KEY_aula)));
                lezione.setIDLezione(cursor.getInt(cursor.getColumnIndex(Lezione.KEY_IDLezione)));
                listaLezioni.add(lezione);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaLezioni;
    }

    // Ottiene una lezione dando il suo ID
    public Lezione getLezioneById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Lezione.KEY_IDLezione + "," +
                Lezione.KEY_IDmateria + "," +
                Lezione.KEY_orainizio + "," +
                Lezione.KEY_orafine + ","+
                Lezione.KEY_giorno + "," +
                " FROM " + Lezione.TABLE
                + " WHERE " +
                Lezione.KEY_IDLezione + "=?";


        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );
        Lezione lezione = new Lezione();

        if (cursor.moveToFirst()) {
            do {
                lezione.setIDMateria(cursor.getInt(cursor.getColumnIndex(Lezione.KEY_IDmateria)));
                lezione.setOraInizio(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orainizio)));
                lezione.setOraFine(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orafine)));
                lezione.setGiorno(cursor.getString(cursor.getColumnIndex(Lezione.KEY_giorno)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lezione;
    }

    // Ottiene una lezione dando l'ID della materia
    public ArrayList<Lezione> getLezioneByMateria(int IDMateria){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Lezione.KEY_IDLezione + "," +
                Lezione.KEY_IDmateria + "," +
                Lezione.KEY_orafine + "," +
                Lezione.KEY_orafine + ","+
                Lezione.KEY_giorno + "," +
                " FROM " + Lezione.TABLE +
                " WHERE "+ Lezione.KEY_IDmateria
                + " =?";

        ArrayList<Lezione> listaLezioni = new ArrayList<Lezione>();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(IDMateria) } );

        // Itera nelle righe generate dalla query ed aggiunge  lezione alla lista
        if (cursor.moveToFirst()) {
            do {
                Lezione lezione = new Lezione();
                lezione.setIDMateria(cursor.getInt(cursor.getColumnIndex(Lezione.KEY_IDmateria)));
                lezione.setOraInizio(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orainizio)));
                lezione.setOraFine(cursor.getString(cursor.getColumnIndex(Lezione.KEY_orafine)));
                lezione.setGiorno(cursor.getString(cursor.getColumnIndex(Lezione.KEY_giorno)));
                listaLezioni.add(lezione);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaLezioni;
    }
}
