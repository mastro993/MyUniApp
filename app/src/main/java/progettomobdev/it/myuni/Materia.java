package progettomobdev.it.myuni;

/**
 * Created by Federico on 06/04/2016.
 */
public class Materia {

    // Attributi Database
    public static final String TABLE = "Materie";
    public static final String KEY_ID = "id";
    public static final String KEY_nome = "nome";
    public static final String KEY_crediti = "crediti";
    public static final String KEY_email = "email";


    // Attributi
    private Integer _ID;
    private String _nome;
    private Integer _crediti;
    private String _email;

    // Costruttore con parametri
    public Materia(String nome, Integer crediti){
        this._nome = nome;
        this._crediti = crediti;
    }
    // Costruttore vuoto
    public Materia() {}

    // Getter
    public Integer getID() {
        return this._ID;
    }
    public String getNome(){
        return this._nome;
    }
    public Integer getCrediti(){
        return this._crediti;
    }
    public String getEmail(){ return this._email;}

    // Setter
    public void setID(Integer id){
        this._ID = id;
    }
    public void setNome(String nome){
        this._nome = nome;
    }
    public void setCrediti(Integer crediti){this._crediti = crediti;}
    public void setEmail (String email){ this._email = email;}
}

