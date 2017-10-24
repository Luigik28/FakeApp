package luigik.fakeapp.view.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import luigik.fakeapp.R;
import luigik.fakeapp.controller.dao.VotoDAO;
import luigik.fakeapp.util.Costanti;

public class MainActivity extends AppCompatActivity {

    SimpleAdapter simpleAdapter;
    Spinner chooserMese;
    Spinner chooserSett;
    List<Map<String, String>> dataSet = new LinkedList<>();
    ListView listView;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listMedia);
        chooserMese = (Spinner) findViewById(R.id.chooserMese);
        chooserSett = (Spinner) findViewById(R.id.chooserSett);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        new BuildViews().execute();


    }

    public void onVota(View view) {
        startActivity(new Intent(this, VotaUtenti.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        chooserMese.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new AsyncTask<Object, Object, Object>() {

                    String[] sett;
                    String item;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        item = (String) chooserMese.getSelectedItem();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            /*
                            switch (item) {
                                case "July":
                                case "Luglio":
                                    selectedMonth = 5;
                                    break;
                                case "Giugno":
                                case "June":
                                    selectedMonth = 6;
                                    break;
                                default:
                                    selectedMonth = -1;
                            }7*/
                            sett = new VotoDAO().getAvailablesWeekOfMonth(Costanti.MapMesi.get(item));
                            if (sett.length == 0) {
                                dataSet = new VotoDAO().getTotAverage();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            sett = new String[0];
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        if (sett.length == 0) {
                            simpleAdapter = new SimpleAdapter(MainActivity.this,
                                    dataSet,
                                    R.layout.row_utente_media, new String[]{"Nome", "Media"}, new int[]{R.id.item1,
                                    R.id.item2});
                            listView.setAdapter(simpleAdapter);
                        }
                        chooserSett.setAdapter(new ArrayAdapter<>(MainActivity.this,
                                R.layout.spinner_item, sett));
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        chooserSett.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new AsyncTask<Object, Object, Object>() {

                    String sett = (String) chooserSett.getSelectedItem();
                    String item;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressBar.setVisibility(View.VISIBLE);
                        item = (String) chooserMese.getSelectedItem();
                    }

                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            dataSet = new VotoDAO().getAverage(Integer.parseInt(sett), Costanti.MapMesi.get(item));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        simpleAdapter = new SimpleAdapter(MainActivity.this,
                                dataSet,
                                R.layout.row_utente_media, new String[]{"Nome", "Media"}, new int[]{R.id.item1,
                                R.id.item2});
                        listView.setAdapter(simpleAdapter);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }.execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private class BuildViews extends AsyncTask<Object, Object, List<Map<String, String>>> {

        String[] mesi;

        @Override
        protected List<Map<String, String>> doInBackground(Object... params) {
            try {
                mesi = new VotoDAO().getAvailablesMonths();
                dataSet = new VotoDAO().getTotAverage();
                return dataSet;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> stringDoubleLinkedHashMap) {
            super.onPostExecute(stringDoubleLinkedHashMap);
            chooserMese.setAdapter(new ArrayAdapter<>(MainActivity.this,
                    R.layout.spinner_item, mesi));
            chooserMese.setSelection(mesi.length - 1);
            simpleAdapter = new SimpleAdapter(MainActivity.this,
                    dataSet,
                    R.layout.row_utente_media, new String[]{"Nome", "Media"}, new int[]{R.id.item1,
                    R.id.item2});
            listView.setAdapter(simpleAdapter);
            progressBar.setVisibility(View.INVISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new BuildAlertMotivazioni(position).execute();
                }
            });

        }
    }

    private class BuildAlertMotivazioni extends AsyncTask<Object, Object, List<String>> {

        int position;
        String item;
        Integer sett;

        BuildAlertMotivazioni(int position) {
            this.position = position;
            this.item = (String) chooserMese.getSelectedItem();
            sett = chooserSett.getSelectedItemPosition();
        }

        @Override
        protected List<String> doInBackground(Object... params) {
            try {
                return new VotoDAO().getMotivs(dataSet.get(position).get("id"),Costanti.MapMesi.get(item), sett);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            String title = dataSet.get(position).get("Nome") + " ha " + strings.size() + " motivazioni!";
            String message = "";
            int i = 0;
            for(String s : strings)
                message += (++i) + ": " + s + "\n";
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(title).setMessage(message).create().show();
        }
    }

}
