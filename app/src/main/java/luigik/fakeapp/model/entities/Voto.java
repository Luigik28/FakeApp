package luigik.fakeapp.model.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import luigik.fakeapp.controller.dao.UtenteDAO;
import luigik.fakeapp.model.Entity;

@SuppressWarnings("unused")
public class Voto implements Entity {

    private Utente votato, votante;
    private int nSettimana, nMese;
    private String motivo = "";
    private float voto = 0f;

    public Voto(){}

    public Voto(Utente votato, Utente votante, int nSettimana, int nMese, String motivazione, float voto) {
        this.votato = votato;
        this.votante = votante;
        this.nSettimana = nSettimana;
        this.nMese = nMese;
        this.motivo = motivazione;
        this.voto = voto;
    }

    public Voto(JSONObject jsonObject) throws JSONException, IOException {
        setVotante(new UtenteDAO().select(jsonObject.getString("id_votante")));
        setVotato(new UtenteDAO().select(jsonObject.getString("id_votato")));
        setnMese(jsonObject.getInt("mese"));
        setnSettimana(jsonObject.getInt("n_settimana"));
        setMotivo(jsonObject.getString("motivazione"));
        setVoto((float)jsonObject.getDouble("voto"));
    }

    public Voto(JSONObject jsonObject, Utente votante, List<Utente> list) throws JSONException, IOException {
        setVotante(votante);
        setVotato(Utente.getById(list,jsonObject.getString("id_votato")));
        setnMese(jsonObject.getInt("mese"));
        setnSettimana(jsonObject.getInt("n_settimana"));
        setMotivo(jsonObject.getString("motivazione"));
        setVoto((float)jsonObject.getDouble("voto"));
    }

    public Utente getVotato() {
        return votato;
    }

    public void setVotato(Utente votato) {
        this.votato = votato;
    }

    public Utente getVotante() {
        return votante;
    }

    public void setVotante(Utente votante) {
        this.votante = votante;
    }

    public int getnSettimana() {
        return nSettimana;
    }

    public void setnSettimana(int nSettimana) {
        this.nSettimana = nSettimana;
    }

    public int getnMese() {
        return nMese;
    }

    public void setnMese(int nMese) {
        this.nMese = nMese;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public float getVoto() {
        return voto;
    }

    public void setVoto(float voto) {
        this.voto = voto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Voto voto = (Voto) o;

        if (nSettimana != voto.nSettimana) return false;
        if (nMese != voto.nMese) return false;
        if (votato != null ? !votato.equals(voto.votato) : voto.votato != null) return false;
        return votante != null ? votante.equals(voto.votante) : voto.votante == null;

    }

    @Override
    public int hashCode() {
        int result = votato != null ? votato.hashCode() : 0;
        result = 31 * result + (votante != null ? votante.hashCode() : 0);
        result = 31 * result + nSettimana;
        result = 31 * result + nMese;
        return result;
    }

    @Override
    public String toString() {
        return votante.toString() + " " + votato + " " + voto;
    }

    public static Voto findVoto(List<Voto> votos, Voto voto) {
        for(Voto v:votos) {
            if(v.getVotato().equals(voto.getVotato()) &&
                    v.getVotante().equals(voto.getVotante()) &&
                    v.getnMese() == voto.getnMese() &&
                    v.getnSettimana() == voto.getnSettimana())
                return v;
        }
        return new Voto();
    }
}
