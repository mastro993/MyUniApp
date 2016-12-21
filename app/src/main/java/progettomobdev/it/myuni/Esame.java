package progettomobdev.it.myuni;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Federico on 06/04/2016.
 */
public class Esame {

    // Attributi Database
    public static final String TABLE = "Esami";
    public static final String KEY_ID = "id";
    public static final String KEY_IDMateria = "idMateria";
    public static final String KEY_voto = "voto";
    public static final String KEY_data= "data";

    // Attributi
    private Integer _ID;
    private Integer _idMateria;
    private Integer _voto;
    private Timestamp _data;

    // Costruttore esame
    public Esame(Integer idMateria, Integer voto, Timestamp data){
        this._idMateria = idMateria;
        this._voto = voto;
        this._data = data;
    }
    // Costruttore vuoto
    public Esame(){

    }

    // GETTER
    public Integer getId() {
        return this._ID;
    }
    public Integer getIdMateria(){
        return this._idMateria;
    }
    public Integer getVoto(){
        return this._voto;
    }
    public Integer getCrediti(){
        return MateriaRepo.getMateriaByID(this.getIdMateria()).getCrediti();
    }
    public Timestamp getData(){
        return this._data;
    }

    // SETTER
    public void setID(Integer id){
        this._ID = id;
    }
    public void setIdMateria(Integer idMateria){
        this._idMateria = idMateria;
    }
    public void setVoto(Integer voto){
        this._voto = voto;
    }
    public void setData(Timestamp data){
        this._data = data;
    }
    public void setData(String data){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            java.util.Date parsedDate = dateFormat.parse(data);
            this._data = new Timestamp(parsedDate.getTime());
        }catch(Exception e){
            // Gestione dell'eccezione
        }
    }
}
