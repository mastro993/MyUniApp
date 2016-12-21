package progettomobdev.it.myuni;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Federico on 06/04/2016.
 */
public class Appello {

    // Attributi Database
    public static final String TABLE = "Appelli";
    public static final String KEY_ID = "id";
    public static final String KEY_IDMateria = "idMateria";
    public static final String KEY_tipo = "tipo";
    public static final String KEY_data = "data";
    public static final String KEY_aula = "aula";

    // Attributi
    private Integer _ID; // Impostato SOLO dopo l'inserimento nel DB
    private Integer _IdMateria;
    private Timestamp _data;
    private String _aula;
    private Integer _tipo; // 0: Scritto, 1: Orale, 2: Pratico

    // Costruttore per nuovo appello da inserire
    public Appello(Integer id, Integer idMateria, Integer tipo, Timestamp data, String aula){
        this._ID = id;
        this._IdMateria = idMateria;
        this._tipo = tipo;
        this._data = data;
        this._aula = aula;
    }

    // Costruttore vuoto
    public Appello(){}

    // GETTER
    public Integer getID(){
        return this._ID;
    }
    public Integer getIdMateria(){
        return this._IdMateria;
    }
    public Timestamp getData(){
        return this._data;
    }
    public String getAula(){
        return this._aula;
    }
    public Integer getTipo() {return this._tipo; }

    // SETTER
    public void setID(Integer ID){
        this._ID = ID;
    }
    public void setIdMateria(Integer id){
        this._IdMateria = id;
    }
    public void setDate(Timestamp data){
        this._data = data;
    }
    public void setDate(String data){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(data);
            this._data = new Timestamp(parsedDate.getTime());
        }catch(Exception e){
            // Gestione dell'eccezione
        }
    }
    public void setAula(String aula){
        this._aula = aula;
    }
    public void setTipo(Integer tipo){this._tipo = tipo; }
}
