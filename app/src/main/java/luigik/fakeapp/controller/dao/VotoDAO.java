package luigik.fakeapp.controller.dao;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import luigik.fakeapp.model.entities.Utente;
import luigik.fakeapp.model.entities.Voto;
import luigik.fakeapp.util.URLUtil;
import luigik.fakeapp.util.Util;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantCast"})
public class VotoDAO extends PhpDAO<Voto> {

    private static final String SELECT_QUERY = "select * from voti_settimanali where id_votante='";

    public List<Voto> select(Utente utente, List<Utente> list) throws IOException, JSONException {
        String query = SELECT_QUERY + utente.getId() + "'";
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        List<Voto> listVoti = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            listVoti.add(new Voto(jsonArray.getJSONObject(i), utente, list));
        }
        return listVoti;
        /*
        try {
            List<Voto> all = new VotoDAO().getAll();
            List<Voto> myVoti = new LinkedList<>();
            for (Voto voto : all)
                if (voto.getVotante().equals(utente) &&
                        voto.getnMese() == ((int) Calendar.getInstance().get(Calendar.MONTH)) &&
                        voto.getnSettimana() == ((int) Calendar.getInstance().get(Calendar.WEEK_OF_MONTH))
                        )
                    myVoti.add(voto);
            return myVoti;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
        */
    }

    @Override
    public void insert(Voto toInsert) throws IOException {

    }

    @Override
    public Voto select(String id) throws IOException, JSONException {
        return null;
    }

    public String[] getAvailablesMonths() throws IOException, JSONException {
        String query = "SELECT DISTINCT mese FROM `voti_settimanali` ORDER BY mese";
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        String[] months = new String[jsonArray.length() + 1];
        for (int i = 0; i < jsonArray.length(); i++) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, jsonArray.getJSONObject(i).getInt("mese"));
            months[i] = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String s = months[i].substring(0, 1).toUpperCase();
            months[i] = s + months[i].substring(1);
        }
        months[jsonArray.length()] = "Totale";
        return months;
    }

    public String[] getAvailablesWeekOfMonth(Integer month) throws IOException, JSONException {
        if(month==null)
            month = -1;
        String query = "SELECT DISTINCT n_settimana FROM `voti_settimanali` WHERE mese = " + month + " ORDER BY n_settimana";
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        String[] week = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            week[i] = jsonArray.getJSONObject(i).getString("n_settimana");
        }
        return week;
    }

    public List<String> getMotivs(String id, Integer mese, Integer sett) throws IOException, JSONException {
        String query = "SELECT motivazione " +
                "FROM voti_settimanali " +
                "WHERE id_votato = " + id;
        if(mese != null)
            query += " AND mese = " + mese + " AND n_settimana = " + sett;
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        List<String> motivs = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            String m = o.getString("motivazione");
            if(!m.isEmpty())
                motivs.add(m);
        }
        return motivs;
    }

    public List<Map<String, String>> getTotAverage() throws IOException, JSONException {
        String query = "SELECT id, nome, cognome, s.media " +
                "FROM (SELECT id_votato, avg(voto) as media " +
                "FROM `voti_settimanali` " +
                "GROUP BY id_votato" +
                ") as s, utenti " +
                "WHERE id_votato = id";
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        List<Map<String, String>> mediaVoti = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            Map<String, String> datum = new HashMap<>(3);
            datum.put("id",o.getString("id"));
            datum.put("Nome", Util.formatPhpString(o.getString("nome")) + " " + Util.formatPhpString(o.getString("cognome")));
            datum.put("Media", new DecimalFormat("#.##").format(Double.parseDouble(o.getString("media"))).replace(',','.'));
            mediaVoti.add(datum);
        }
        Collections.sort(mediaVoti, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                int compare = Double.compare(Double.parseDouble(o1.get("Media")), Double.parseDouble(o2.get("Media")));
                if (compare > 0)
                    compare = -1;
                else if (compare < 0)
                    compare = 1;
                return compare;
            }
        });
        return mediaVoti;
    }

    public List<Map<String, String>> getAverage(int n_settimana, int mese) throws IOException, JSONException {
        if (mese == -1)
            return getTotAverage();
        String query = "SELECT id, nome, cognome, s.media " +
                "FROM (SELECT id_votato, avg(voto) as media " +
                "FROM `voti_settimanali` " +
                "GROUP BY id_votato, n_settimana, mese " +
                "HAVING mese = " + mese + " AND n_settimana = " + n_settimana + " " +
                ") as s, utenti " +
                "WHERE id_votato = id";
        JSONArray jsonArray = new JSONArray(URLUtil.getResponseFromURL(getConnection(query)));
        List<Map<String, String>> mediaVoti = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            Map<String, String> datum = new HashMap<>(3);
            datum.put("id",o.getString("id"));
            datum.put("Nome", o.getString("nome") + " " + o.getString("cognome"));
            datum.put("Media", o.getString("media"));
            mediaVoti.add(datum);
        }
        Collections.sort(mediaVoti, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                int compare = Double.compare(Double.parseDouble(o1.get("Media")), Double.parseDouble(o2.get("Media")));
                if (compare > 0)
                    compare = -1;
                else if (compare < 0)
                    compare = 1;
                return compare;
            }
        });
        return mediaVoti;
    }

    public List<Map<String, String>> getAverage() throws IOException, JSONException {
        Calendar calendar = Calendar.getInstance();
        return getAverage(calendar.get(Calendar.WEEK_OF_MONTH), calendar.get(Calendar.MONTH));
    }

    public List<Voto> getAll() throws IOException {
        Uri connection = URLUtil.buildUri("selectVoto.php");
        try {
            String response = URLUtil.getResponseFromURL(new URL(connection.toString()));
            JSONArray jsonArray = new JSONArray(response);
            ArrayList<Voto> voti = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                voti.add(new Voto(jsonArray.getJSONObject(i)));
            }
            return voti;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
