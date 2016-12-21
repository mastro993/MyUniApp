package progettomobdev.it.myuni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Federico on 06/04/2016.
 */
public class MateriaRepo {
    private static DBHandler dbHelper;

    private Context context;

    public MateriaRepo(Context context) {
        dbHelper = new DBHandler(context);
        this.context = context;
    }

    // Inserisce una nuova materia
    public int insert(Materia materia) {
        // Apertura connessine per la scrittura dei dati
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Materia.KEY_nome, materia.getNome());
        values.put(Materia.KEY_crediti, materia.getCrediti());
        values.put(Materia.KEY_email, materia.getEmail());

        // Inserimento riga
        long idMateria = db.insert(Materia.TABLE, null, values);
        db.close();

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);

        return (int) idMateria;
    }

    // Elimina la materia nel db con l'id dato
    public void delete(int idMateria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Materia.TABLE, Materia.KEY_ID + "= ?", new String[] { String.valueOf(idMateria) });
        // Elimina gli esami riferiti a questa materia.
        db.delete(Esame.TABLE, Esame.KEY_IDMateria + "= ?", new String[] { String.valueOf(idMateria) });
        // Elimina gli appelli riferiti a questa materia
        db.delete(Appello.TABLE, Appello.KEY_IDMateria + "= ?", new String[] { String.valueOf(idMateria) });
        db.delete(Lezione.TABLE, Lezione.KEY_IDmateria + "=?", new String[] { String.valueOf(idMateria)});

        db.close(); // Chiusura connessione al database

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Aggiorna la materia data
    public void update(Materia materia) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Materia.KEY_ID, materia.getID().toString());
        values.put(Materia.KEY_nome, materia.getNome());
        values.put(Materia.KEY_crediti, materia.getCrediti());
        values.put(Materia.KEY_email, materia.getEmail());

        db.update(Materia.TABLE, values, Materia.KEY_ID + "= ?", new String[]{String.valueOf(materia.getID())});
        db.close(); // Closing database connection

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Ottiene la lista delle materie in ordine alfabetico
    public ArrayList<Materia> getListaMaterie() {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Materia.KEY_ID + "," +
                Materia.KEY_nome + "," +
                Materia.KEY_email + "," +
                Materia.KEY_crediti +
                " FROM " + Materia.TABLE + " ORDER BY " + Materia.KEY_nome;

        ArrayList<Materia> listaMaterie = new ArrayList<Materia>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge le materie alla lista
        if (cursor.moveToFirst()) {
            do {
                Materia m = new Materia();
                m.setID(cursor.getInt(cursor.getColumnIndex(Materia.KEY_ID)));
                m.setNome(cursor.getString(cursor.getColumnIndex(Materia.KEY_nome)));
                m.setEmail(cursor.getString(cursor.getColumnIndex(Materia.KEY_email)));
                m.setCrediti(cursor.getInt(cursor.getColumnIndex(Materia.KEY_crediti)));
                listaMaterie.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaMaterie;
    }

    // Ottiene i dati della materia con l'id dato
    public static Materia getMateriaByID(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Materia.KEY_ID + "," +
                Materia.KEY_nome + "," +
                Materia.KEY_email + "," +
                Materia.KEY_crediti +
                " FROM " + Materia.TABLE
                + " WHERE " +
                Materia.KEY_ID + "=?";

        int iCount =0;

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );
        Materia materia = new Materia();

        if (cursor.moveToFirst()) {
            do {
                materia.setID(cursor.getInt(cursor.getColumnIndex(Materia.KEY_ID)));
                materia.setNome(cursor.getString(cursor.getColumnIndex(Materia.KEY_nome)));
                materia.setEmail(cursor.getString(cursor.getColumnIndex(Materia.KEY_email)));
                materia.setCrediti(cursor.getInt(cursor.getColumnIndex(Materia.KEY_crediti)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return materia;
    }

    public static Materia getMateriaByNome(String nome){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Materia.KEY_ID + "," +
                Materia.KEY_nome + "," +
                Materia.KEY_email + "," +
                Materia.KEY_crediti +
                " FROM " + Materia.TABLE
                + " WHERE " +
                Materia.KEY_nome + " LIKE '" + nome + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Materia m = new Materia();

        if (cursor.moveToFirst()) {
            do {
                m.setID(cursor.getInt(cursor.getColumnIndex(Materia.KEY_ID)));
                m.setNome(cursor.getString(cursor.getColumnIndex(Materia.KEY_nome)));
                m.setEmail(cursor.getString(cursor.getColumnIndex(Materia.KEY_email)));
                m.setCrediti(cursor.getInt(cursor.getColumnIndex(Materia.KEY_crediti)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return m;
    }

    // Ottiene la lista delle materie che non hanno un esame registrato nel db
    public ArrayList<Materia> getListaMaterieNoEsame() {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Materia.KEY_ID + "," +
                Materia.KEY_nome + "," +
                Materia.KEY_email + "," +
                Materia.KEY_crediti +
                " FROM " + Materia.TABLE +
                " WHERE " + Materia.KEY_ID +
                " NOT IN " +
                "   (SELECT " + Esame.KEY_IDMateria + " FROM " + Esame.TABLE + ")" +
                " ORDER BY " + Materia.KEY_nome;

        ArrayList<Materia> listaMaterie = new ArrayList<Materia>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge le materie alla lista
        if (cursor.moveToFirst()) {
            do {
                Materia m = new Materia();
                m.setID(cursor.getInt(cursor.getColumnIndex(Materia.KEY_ID)));
                m.setNome(cursor.getString(cursor.getColumnIndex(Materia.KEY_nome)));
                m.setEmail(cursor.getString(cursor.getColumnIndex(Materia.KEY_email)));
                m.setCrediti(cursor.getInt(cursor.getColumnIndex(Materia.KEY_crediti)));
                listaMaterie.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaMaterie;
    }
}
