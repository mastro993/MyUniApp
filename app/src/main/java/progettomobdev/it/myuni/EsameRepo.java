package progettomobdev.it.myuni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico on 06/04/2016.
 * Gestione degli esami nel database
 */
public class EsameRepo {
    private static DBHandler dbHelper;

    private Context context;

    public EsameRepo(Context context) {
        dbHelper = new DBHandler(context);
        this.context = context;
    }

    // Inserisce un nuovo esame
    public int insert(Esame esame) {
        // Apertura connessine per la scrittura dei dati
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Esame.KEY_IDMateria, esame.getIdMateria());
        values.put(Esame.KEY_voto, esame.getVoto());
        values.put(Esame.KEY_data, esame.getData().toString());

        // Inserimento riga e ritorna l'id ottenuto nel db
        long idEsame = db.insert(Esame.TABLE, null, values);
        db.close();

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);

        return (int) idEsame;
    }

    // Elimina un esame nel db con l'id dato
    public void delete(int idEsame) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Esame.TABLE, Esame.KEY_ID + "= ?", new String[] { String.valueOf(idEsame) });
        db.close(); // Chiusura connessione al database

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Aggiorna l'esame
    public void update(Esame esame) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Esame.KEY_IDMateria, esame.getIdMateria());
        values.put(Esame.KEY_voto,esame.getVoto());
        values.put(Esame.KEY_data, esame.getData().toString());
        values.put(Esame.KEY_ID, esame.getId());

        db.update(Esame.TABLE, values, Esame.KEY_ID + "= ?", new String[]{String.valueOf(esame.getId())});
        db.close(); // Closing database connection

        // incrementa il valore del database per verificarne lo stato di aggiornamento
        DBHandler.updateDB(context);
    }

    // Ottiene la lista degli esami
    public static ArrayList<Esame> getListaEsami() {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_ID + "," +
                Esame.KEY_IDMateria + "," +
                Esame.KEY_voto + "," +
                Esame.KEY_data +
                " FROM " + Esame.TABLE +
                " ORDER BY " + Esame.KEY_data;

        ArrayList<Esame> listaEsami = new ArrayList<Esame>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge  esami alla lista
        if (cursor.moveToFirst()) {
            do {
                Esame esame = new Esame();
                esame.setID(cursor.getInt(cursor.getColumnIndex(Esame.KEY_ID)));
                esame.setIdMateria(cursor.getInt(cursor.getColumnIndex(Esame.KEY_IDMateria)));
                esame.setVoto(cursor.getInt(cursor.getColumnIndex(Esame.KEY_voto)));
                esame.setData(cursor.getString(cursor.getColumnIndex(Esame.KEY_data)));
                listaEsami.add(esame);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaEsami;
    }

    // Ottiene un esame dando il suo ID
    public Esame getEsameByID(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_ID + "," +
                Esame.KEY_IDMateria + "," +
                Esame.KEY_voto + "," +
                Esame.KEY_data +
                " FROM " + Esame.TABLE
                + " WHERE " +
                Esame.KEY_ID + "=?";

        int iCount =0;

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );
        Esame esame = new Esame();

        if (cursor.moveToFirst()) {
            do {
                esame.setID(cursor.getInt(cursor.getColumnIndex(Esame.KEY_ID)));
                esame.setIdMateria(cursor.getInt(cursor.getColumnIndex(Esame.KEY_IDMateria)));
                esame.setVoto(cursor.getInt(cursor.getColumnIndex(Esame.KEY_voto)));
                esame.setData(cursor.getString(cursor.getColumnIndex(Esame.KEY_data)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return esame;
    }

    // Ottiene un esame dando l'ID della materia
    public Esame getEsameByMateria(int IDMateria){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_ID + "," +
                Esame.KEY_IDMateria + "," +
                Esame.KEY_voto + "," +
                Esame.KEY_data +
                " FROM " + Esame.TABLE
                + " WHERE " +
                Esame.KEY_IDMateria + "=?";

        int iCount =0;

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(IDMateria) } );
        Esame esame = null;

        if (cursor.moveToFirst()) {
            do {
                esame = new Esame();
                esame.setID(cursor.getInt(cursor.getColumnIndex(Esame.KEY_ID)));
                esame.setIdMateria(cursor.getInt(cursor.getColumnIndex(Esame.KEY_IDMateria)));
                esame.setVoto(cursor.getInt(cursor.getColumnIndex(Esame.KEY_voto)));
                esame.setData(cursor.getString(cursor.getColumnIndex(Esame.KEY_data)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return esame;
    }

    //Ottiene la moda dei voti, ovvero quante volte compare un voto rispetto ad un altro
    public HashMap<Integer, Integer> getModaEsami(){
        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_voto + "," +
                "COUNT(*) as numero" +
                " FROM " + Esame.TABLE +
                " GROUP BY " + Esame.KEY_voto +
                " ORDER BY " + Esame.KEY_voto;

        HashMap<Integer, Integer> moda = new HashMap<Integer, Integer>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                moda.put(cursor.getInt(cursor.getColumnIndex(Esame.KEY_voto)), cursor.getInt(cursor.getColumnIndex("numero")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return moda;
    }

    public static Integer getTotCrediti(){

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_ID + "," +
                Esame.KEY_IDMateria + "," +
                Esame.KEY_voto + "," +
                Esame.KEY_data +
                " FROM " + Esame.TABLE +
                " ORDER BY " + Esame.KEY_data;

        ArrayList<Esame> listaEsami = new ArrayList<Esame>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        Integer crediti = 0, creditiTemp;

        // Itera nelle righe generate dalla query ed aggiunge  esami alla lista
        if (cursor.moveToFirst()) {
            do {
                creditiTemp = MateriaRepo.getMateriaByID(
                        cursor.getInt(cursor.getColumnIndex(Esame.KEY_IDMateria))).getCrediti();
                crediti += creditiTemp;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return crediti;
    }

    public static ArrayList<Esame> getListaEsamiPerVoto(Integer voto) {

        // Apertura connessione db sola lettura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Esame.KEY_ID + "," +
                Esame.KEY_IDMateria + "," +
                Esame.KEY_voto + "," +
                Esame.KEY_data +
                " FROM " + Esame.TABLE +
                " WHERE " + Esame.KEY_voto +
                " = " + voto;

        ArrayList<Esame> listaEsami = new ArrayList<Esame>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Itera nelle righe generate dalla query ed aggiunge  esami alla lista
        if (cursor.moveToFirst()) {
            do {
                Esame esame = new Esame();
                esame.setID(cursor.getInt(cursor.getColumnIndex(Esame.KEY_ID)));
                esame.setIdMateria(cursor.getInt(cursor.getColumnIndex(Esame.KEY_IDMateria)));
                esame.setVoto(cursor.getInt(cursor.getColumnIndex(Esame.KEY_voto)));
                esame.setData(cursor.getString(cursor.getColumnIndex(Esame.KEY_data)));
                listaEsami.add(esame);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaEsami;
    }
}
