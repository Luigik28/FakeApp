package luigik.fakeapp.view.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import luigik.fakeapp.R;
import luigik.fakeapp.controller.dao.VotoDAO;
import luigik.fakeapp.model.UtenteAdapter;
import luigik.fakeapp.model.entities.Utente;
import luigik.fakeapp.model.entities.Voto;

public class VotaUtenti extends AppCompatActivity {


    List<Utente> list;
    List<Voto> myVoti;
    ListView listView;
    UtenteAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vota_utenti);
        listView = (ListView) findViewById(R.id.listUtenti);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                try {
                    list = Utente.getCurrentUser().getFriendList();
                    myVoti = new VotoDAO().select(Utente.getCurrentUser(),list);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                adapter = new UtenteAdapter(VotaUtenti.this, R.layout.row_utente_voto, list, myVoti);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
                listView.invalidate();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vota, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.done:
                item.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                listView.setEnabled(false);
                new AsyncTask<Object, Object, List<Voto>>() {

                    @Override
                    protected List<Voto> doInBackground(Object... params) {
                        List<Voto> votoFalse = new LinkedList<>();
                        for (Voto v : adapter.voti) {
                            try {
                                if(!Utente.getCurrentUser().vota(v))
                                    votoFalse.add(v);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return votoFalse;
                    }

                    @Override
                    protected void onPostExecute(List<Voto> votoFalse) {
                        super.onPostExecute(votoFalse);
                        if(!votoFalse.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(VotaUtenti.this);
                            builder.setMessage("Errore nell'inserimento dei voti");
                            builder.create().show();
                        }
                        final Intent intent = new Intent(VotaUtenti.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
