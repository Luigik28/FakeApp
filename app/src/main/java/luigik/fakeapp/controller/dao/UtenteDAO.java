package luigik.fakeapp.controller.dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import luigik.fakeapp.model.entities.Utente;
import luigik.fakeapp.util.URLUtil;
import luigik.fakeapp.util.Util;

@SuppressWarnings("unused")
public class UtenteDAO extends PhpDAO<Utente> {


    private static final String INSERT_QUERY = "insert into utenti values(";
    private static final String SELECT_QUERY = "select * from utenti where id=";

    @Override
    public void insert(Utente toInsert) throws IOException {
        String query = INSERT_QUERY +
                "\"" + toInsert.getId() + "\"," +
                "\"" + toInsert.getNome() + "\"," +
                "\"" + toInsert.getCognome() + "\")";
        URLUtil.getResponseFromURL(getConnection(query));
    }

    @Override
    public Utente select(String id) throws IOException, JSONException {
        String response = URLUtil.getResponseFromURL(getConnection(SELECT_QUERY + id));
        JSONArray jsonArray = new JSONArray(response);
        return new Utente(jsonArray.getJSONObject(0));
    }

    public boolean vota(Utente votante, Utente votato, int nMese, int nSettimana,float voto, String motivazione) throws IOException {
        //noinspection unchecked
        connection = URLUtil.buildUri(
                "insertVoto.php",
                new Util.MapBuilder<String, String>()
                        .put("id_votante",votante.getId())
                        .put("id_votato",votato.getId())
                        .put("n_settimana", String.valueOf(nSettimana))
                        .put("mese", String.valueOf(nMese))
                        .put("motivazione", motivazione)
                        .put("voto",String.valueOf(voto))
                        .build()
        );
        return URLUtil.getResponseFromURL(new URL(connection.toString())).equals("1");
    }

    @Override
    public List<Utente> getAll() throws IOException {
        connection = URLUtil.buildUri("getUtenti.php");
        try {
            String response = URLUtil.getResponseFromURL(new URL(connection.toString()));
            JSONArray jsonArray = new JSONArray(response);
            LinkedList<Utente> utenti = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                utenti.add(new Utente(jsonArray.getJSONObject(i)));
            }
            return utenti;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
