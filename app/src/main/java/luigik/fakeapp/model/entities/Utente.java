package luigik.fakeapp.model.entities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import luigik.fakeapp.controller.dao.UtenteDAO;
import luigik.fakeapp.controller.facebook.ProfileManager;
import luigik.fakeapp.model.Entity;
import luigik.fakeapp.util.Util;


@SuppressWarnings({"unused", "WeakerAccess"})
public class Utente implements Entity, Cloneable {

    private String id, nome, cognome;

    private Bitmap profilePic = null;

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Utente() {
    }

    public Utente(Profile profile) {
        setId(profile.getId());
        if (profile.getMiddleName().isEmpty())
            setNome(profile.getFirstName());
        else
            setNome(profile.getFirstName() + " " + profile.getMiddleName());
        setCognome(profile.getLastName());
    }

    public Utente(JSONObject json) throws JSONException {
        setId(json.getString("id"));
        setNome(json.getString("nome"));
        setCognome(json.getString("cognome"));
    }

    public static Utente getCurrentUser() {
        return new Utente(ProfileManager.getProfileManager().getCurrentProfile());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Util.formatPhpString(nome);
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = Util.formatPhpString(cognome);
    }

    public List<Utente> getFriendList() throws IOException {
        List<Utente> list = new UtenteDAO().getAll();
        list.remove(list.indexOf(Utente.getCurrentUser()));
        return new LinkedList<>(list);
    }

    public static Utente getById(List<Utente> list, String id) {
        for(Utente u: list)
            if(id.equals(u.getId()))
                return u;
        return null;
    }

    public boolean vota(Voto voto) throws IOException {
        return new UtenteDAO().vota(voto.getVotante(),voto.getVotato(),voto.getnMese(),voto.getnSettimana(),voto.getVoto(),voto.getMotivo());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && getId().equals(((Utente) o).getId());

    }

    @Override
    public int hashCode() {
        return Integer.parseInt(id);
    }

    @Override
    public String toString() {
        return getNome() + " " + getCognome();
    }

    public Map<String, String> getMap() {
        //noinspection unchecked
        return (Map<String, String>) new Util.MapBuilder<String, String>()
                .put("id", getId())
                .put("nome", getNome())
                .put("cognome", getCognome()).build();
    }
}
