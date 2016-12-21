package progettomobdev.it.myuni;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Federico on 07/04/2016.
 * Classe per la gestione del libretto
 */
public class Libretto {

    static MainActivity main = new MainActivity();

    private String _corso;
    private String _annoAccademico;
    private static Integer _creditiLaurea; // Crediti necessari alla laurea. Es. Parma: 150 su 180
    private static Integer _valoreLode; // Es.: Se è impostata a 32 ogni voto con 32 sarà contato come lode

    // GETTER
    public String getCorso() {
        return _corso;
    }
    public String getAnnoAccademico() {
        return _annoAccademico;
    }
    public static Integer getCreditiLaurea() {
        return _creditiLaurea;
    }
    public static Integer getValoreLode() {
        return _valoreLode;
    }

    // SETTER
    public void setCorso(String corso) {
        this._corso = corso;
    }
    public void setAnnoAccademico(String anno) {
        this._annoAccademico = anno;
    }
    public void setCreditiLaurea(Integer crediti) {
        this._creditiLaurea = crediti;
    }
    public static void setValoreLode(Integer valore) {
        _valoreLode = valore;
    }

    // Ottiene la somma dei crediti degli esami dati
    public Integer getCreditiTotali() {
        Integer _sommaCrediti = 0;
        EsameRepo eRepo = new EsameRepo(main.getApplicationContext());

        ArrayList<Esame> esami = eRepo.getListaEsami();

        for (Esame esame : esami) {
            _sommaCrediti = esame.getCrediti();
        }
        return _sommaCrediti;
    }

    // Ottiene la media aritmetica totale
    public Double getMediaAritmetica() {
        Double _numeroEsami = 0.0, _sommaVoti = 0.0;

        EsameRepo eRepo = new EsameRepo(main.getApplicationContext());

        ArrayList<Esame> esami = eRepo.getListaEsami();

        for (Esame esame : esami) {
            if(esame.getVoto() >= 18){
                _numeroEsami += 1;
                _sommaVoti += esame.getVoto();
            }
        }

        return _sommaVoti / _numeroEsami;
    }

    // Ottiene la media ponderata totale
    public static Double getMediaPonderata(ArrayList<Esame> esami) {

        Double _sommaCrediti = 0.0, _sommaVoti = 0.0;
        Integer _creditiTemp;

        for (Esame esame : esami) {
            if(esame.getVoto() >= 18){
                _creditiTemp = Integer.valueOf(esame.getCrediti());
                _sommaCrediti += _creditiTemp;
                _sommaVoti += esame.getVoto() * _creditiTemp;
            }
        }
        if(_sommaCrediti == 0.0 || _sommaVoti == 0.0) return null;
        else return _sommaVoti / _sommaCrediti;
    }

    //  Ottiene la base di laurea aritmetica
    public Double getBaseLaureaAritmetica() {
        // Media aritmetica / 11 * 3
        return (getMediaAritmetica() * 11) / 3;
    }

    // Ottiene la base di laurea ponderata, tenendo conto dei crediti minimi
    // necessari per il corso di laurea (_creditiLaurea). Quelli in eccesso non verranno contati
    public Double getBaseLaureaPonderata(Context context) {

        Double _sommaCrediti = 0.0, _sommaPesi = 0.0;
        Integer _peso, _creditiLaureaTemp = _creditiLaurea, _creditiTemp = 0;

        EsameRepo eRepo = new EsameRepo(context);

        ArrayList<Esame> esami = eRepo.getListaEsami();

        // Riordina la lista degli esami mettendo prima quelli "più pesanti" (voto * crediti)
        int i, j;
        for (i = 0; i < esami.size(); i++) {
            for (j = 0; j < esami.size(); j++) {
                if (i != j && esami.get(i).getVoto() > esami.get(j).getVoto()) {
                    Collections.swap(esami, i, j);
                }
            }
        }

        // Calcolo media ponderata per la base di laurea
        for (Esame esame : esami) {
            _creditiTemp = esame.getCrediti();
            if (_creditiLaureaTemp >= _creditiTemp) {
                _sommaPesi += esame.getVoto() * _creditiTemp;
                _sommaCrediti += _creditiTemp;
                _creditiLaureaTemp -= _creditiTemp;
            } else {
                _sommaPesi += esame.getVoto() * _creditiLaureaTemp;
                _sommaCrediti += _creditiLaureaTemp;
                break;
            }
        }
        return (_sommaPesi / _sommaCrediti) * 11 / 3;


    }

    // Restituisce un array di previsioni della media ponderata (Array  di voti da 18 a _votoLode)
    // Prende come parametro i crediti di un ipotetico esame
    public static ArrayList<Previsione> getPrevisione(Context context, Integer idMat) {

        // Lista esami temporanea per iterazioni
        ArrayList<Esame> esamiTemp = new ArrayList<Esame>();

        Double oldMedia, media, variazione;
        Integer i, voto;
        EsameRepo eRepo = new EsameRepo(context);
        MateriaRepo mRepo = new MateriaRepo(context);

        // Ottiene la lista degli esami
        ArrayList<Esame> esami = eRepo.getListaEsami();
        ArrayList<Previsione> previsioni = new ArrayList<Previsione>();

        // Calcola la media prima della previsione
        oldMedia = getMediaPonderata(esami);

        if(oldMedia == null) oldMedia = 0.0;

        // Itera per i voti da 18 al 30 e lode
        for (i = 18; i <= _valoreLode; i++) {
            if (i <= 30 || i == _valoreLode) {

                // Svuota la lista esami temporanea
                esamiTemp.clear();

                // Ci copia la lista esami
                esamiTemp.addAll(esami);

                // Crea un esame e lo inserisce nella lista
                Esame e = new Esame(idMat, i, null);

                esamiTemp.add(e);

                // Calcola la media ponderata
                media = getMediaPonderata(esamiTemp);
                if(media == null) media = 0.0;

                // Calcola la variazione
                variazione = media - oldMedia;

                Previsione prev = new Previsione(i, media, variazione);
                previsioni.add(prev);
            }
        }
        return previsioni;
    }

    // Salva i dati del libretto nelle shared preferences
    public boolean save(SharedPreferences prefs) {

        SharedPreferences.Editor editor = prefs.edit();

        try {
            editor.putString("Corso", this._corso);
            editor.putString("Anno", this._annoAccademico);
            editor.putInt("CreditiLaurea", this._creditiLaurea);
            editor.putInt("ValoreLode", this._valoreLode);
            editor.commit();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // Carica i dati per generare l'oggetto libretto dalla shared preferences
    public Libretto load(SharedPreferences prefs) {
        Libretto lib = new Libretto();
        lib._corso = prefs.getString("Corso", null);
        lib._annoAccademico = prefs.getString("Anno", null);
        lib._creditiLaurea = prefs.getInt("CreditiLaurea", 0);
        lib._valoreLode = prefs.getInt("ValoreLode", 0);
        return lib;
    }

}
