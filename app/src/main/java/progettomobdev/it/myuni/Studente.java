package progettomobdev.it.myuni;

import android.content.SharedPreferences;

/**
 * Created by Federico on 06/04/2016.
 */
public class Studente {

    private String _nome, _cognome, _matricola, _img;

    // Costruttore con passaggio dei parametri
    public Studente(String nome, String cognome, String matricola){
        this._nome = nome;
        this._cognome = cognome;
        this._matricola = matricola;
    }

    // Costruttore dalle shared preferences
    public Studente(){
    }

    // Getter
    public String getNome(){
        return this._nome;
    }
    public String getCognome(){
        return this._cognome;
    }
    public String getMatricola(){
        return this._matricola;
    }
    public String getImg(){
        return this._img;
    }

    // Setter
    public void setNome(String nome){
        this._nome = nome;
    }
    public void setCognome(String cognome){
        this._cognome = cognome;
    }
    public void setMatricola(String matricola){
        this._matricola = matricola;
    }
    public void setImg(String imgCoded){
        this._img = imgCoded;
    }

    // Salva i dati dello studente nelle shared preferences
    public boolean save(SharedPreferences prefs){

        SharedPreferences.Editor editor = prefs.edit();

        try{
            editor.putString("Nome", this._nome);
            editor.putString("Cognome", this._cognome);
            editor.putString("Matricola", this._matricola);
            editor.putString("Immagine", this._img);
            editor.commit();
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    // Carica i dati per generare l'oggetto studente dalla shared preferences
    public Studente load(SharedPreferences prefs){

        Studente stud = new Studente();
        stud._nome = prefs.getString("Nome", null);
        stud._cognome = prefs.getString("Cognome", null);
        stud._matricola = prefs.getString("Matricola", null);
        stud._img = prefs.getString("Immagine", null);
        return stud;
    }


}
