package progettomobdev.it.myuni;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Federico on 06/04/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static SharedPreferences DBprefs;

    // Nome del database
    private static final String NOME_DB = "myUni.db";

    public DBHandler(Context context) {
        super(context, NOME_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creazione database per materie
        String CREATE_TABLE_MATERIE = "CREATE TABLE " + Materia.TABLE + "("
                + Materia.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Materia.KEY_nome + " TEXT, "
                + Materia.KEY_email + " TEXT, "
                + Materia.KEY_crediti + " INTEGER )";

        // Creazione database per gli esami
        String CREATE_TABLE_ESAMI = "CREATE TABLE " + Esame.TABLE + "("
                + Esame.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Esame.KEY_IDMateria + " INTEGER UNIQUE,"
                + Esame.KEY_voto + " INTEGER, "
                + Esame.KEY_data + " DATETIME )";

        // Creazione database per gli appelli
        String CREATE_TABLE_APPELLI = "CREATE TABLE " + Appello.TABLE + "("
                + Appello.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Appello.KEY_IDMateria + " INTEGER,"
                + Appello.KEY_tipo + " INTEGER, "
                + Appello.KEY_aula + " INTEGER, "
                + Appello.KEY_data + " DATETIME )";

        // Creazione database per lezioni
        String CREATE_TABLE_LEZIONI = "CREATE TABLE " + Lezione.TABLE + "("
                + Lezione.KEY_IDLezione + " INTEGER PRIMARY KEY AUTOINCREMENT not NULL, "
                + Lezione.KEY_IDmateria + " INTEGER,"
                + Lezione.KEY_orafine + " DATETIME not NULL, "
                + Lezione.KEY_orainizio + " DATETIME not NULL, "
                + Lezione.KEY_aula + " TEXT, "
                + Lezione.KEY_giorno + " TEXT )";

        //esecuzione query

        db.execSQL(CREATE_TABLE_MATERIE);
        db.execSQL(CREATE_TABLE_ESAMI);
        db.execSQL(CREATE_TABLE_APPELLI);
        db.execSQL(CREATE_TABLE_LEZIONI);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Elimina i database se esistono
        db.execSQL("DROP TABLE IF EXISTS " + Materia.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Esame.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Appello.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Lezione.TABLE);

        // Crea i database nuovamente
        onCreate(db);

    }

    public static int getDatabaseUpdates(Context context) {
        DBprefs = context.getSharedPreferences("progettomobdev.it.myuni.DBoxAccess", 0);
        return DBprefs.getInt("DBUpdates", 0);
    }

    public static void setDatabaseUpdates(Context context, Integer updates) {
        DBprefs = context.getSharedPreferences("progettomobdev.it.myuni.DBoxAccess", 0);
        SharedPreferences.Editor edit = DBprefs.edit();
        edit.putInt("DBUpdates", updates);
        edit.apply();
    }

    public static void updateDB(Context context) {
        Integer old_updates = getDatabaseUpdates(context);
        setDatabaseUpdates(context, old_updates + 1);
    }
}
