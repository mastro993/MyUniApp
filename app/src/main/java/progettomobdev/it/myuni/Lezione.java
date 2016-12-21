package progettomobdev.it.myuni;


import java.sql.Timestamp;

/**
 * Created by Federico on 13/04/2016.
 */
public class Lezione {
    // Attributi Database
    public static final String TABLE = "Lezioni";
    public static final String KEY_IDLezione = "id";
    public static final String KEY_IDmateria = "IDmateria";
    public static final String KEY_orainizio = "ora_inizio";
    public static final String KEY_orafine = "ora_fine";
    public static final String KEY_giorno = "giorno";
    public static final String KEY_aula = "aula";

    // Attributi
    private Integer _IDmateria;
    private Integer _IDLezione;
    private Timestamp _orainizio;
    private Timestamp _orafine;
    private String _giorno;
    private String _aula;


    // Costruttore con parametri
    public Lezione(Integer IDmateria, Timestamp orainizio, Timestamp orafine, String giorno, String aula){
        this._IDmateria = IDmateria;
        this._orainizio = orainizio;
        this._orafine = orafine;
        this._giorno = giorno;
        this._aula = aula;
    }
    // Costruttore vuoto
    public Lezione() {}

    // Getter
    public Integer getIDMateria() {
        return this._IDmateria;
    }
    public Integer getIDLezione() {
        return this._IDLezione;
    }
    public Timestamp getOraInizio(){
        return this._orainizio;
    }
    public Timestamp getOraFine (){
        return this._orafine;
    }
    public String getGiorno(){ return this._giorno;}
    public String getAula(){ return this._aula;}


    // Setter
    public void setIDMateria(Integer IDmateria){this._IDmateria = IDmateria;}
    public void setIDLezione(Integer IDLezione){this._IDLezione = IDLezione;}
    public void setOraInizio(Timestamp oraInizio){
        this._orainizio = oraInizio;
    }
    public void setOraFine (Timestamp oraFine){
        this._orafine = oraFine;
    }
    public void setGiorno(String giorno){ this._giorno = giorno;}
    public void setAula(String aula) { this._aula = aula;}
    public void setOraInizio(String oraInizio) {
        try {
            Timestamp oraIniTmp = Timestamp.valueOf(oraInizio);
            this._orainizio = oraIniTmp;
        } catch (Exception e) {
            // Gestione dell'eccezione
        }
    }
    public void setOraFine(String oraFine) {
        try {
            this._orafine = Timestamp.valueOf(oraFine);
        } catch (Exception e) {
            // Gestione dell'eccezione
        }
    }



}
