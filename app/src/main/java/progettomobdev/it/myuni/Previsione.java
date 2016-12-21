package progettomobdev.it.myuni;

/**
 * Created by Federico on 11/04/2016.
 * Classe di supporto per le previsioni
 */
public class Previsione {
    private Integer _voto;
    private Double _nuovaMediaPonderata;
    private Double _variazioneMediaPonderata;

    public Previsione(Integer voto, Double media, Double variazione){
        this._voto = voto;
        this._nuovaMediaPonderata = media;
        this._variazioneMediaPonderata = variazione;
    }

    public Integer getVoto(){
        return this._voto;
    }

    public Double getNuovaMedia(){
        return this._nuovaMediaPonderata;
    }

    public double getVariazione(){
        return DashboardFragment.round(this._variazioneMediaPonderata,2);
    }
}
