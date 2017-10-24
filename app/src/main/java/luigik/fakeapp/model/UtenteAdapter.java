package luigik.fakeapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import luigik.fakeapp.R;
import luigik.fakeapp.controller.facebook.AccessTracker;
import luigik.fakeapp.model.entities.Utente;
import luigik.fakeapp.model.entities.Voto;

import static luigik.fakeapp.model.entities.Voto.findVoto;

@SuppressWarnings("unused")
public class UtenteAdapter extends ArrayAdapter<Utente> {

    private class ViewHolder {

        private ImageView profileImage;
        private TextView nome;
        private RatingBar ratingBar;
        private ProgressBar progressBar;
        private EditText text;

    }

    public List<Voto> voti;
    private List<Utente> utenti;

    public UtenteAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Utente> objects, @Nullable List<Voto> myVoti) {
        super(context, resource, objects);
        utenti = objects;
        voti = new ArrayList<>();
        for (Utente utente : objects) {
            Voto v = new Voto();
            v.setVotante(Utente.getCurrentUser());
            v.setVotato(utente);
            v.setnMese(Calendar.getInstance().get(Calendar.MONTH));
            v.setnSettimana(Calendar.getInstance().get(Calendar.WEEK_OF_MONTH));
            if (myVoti != null) {
                Voto voto = findVoto(myVoti,v);
                v.setVoto(voto.getVoto());
                v.setMotivo(voto.getMotivo());
            }
            voti.add(v);
        }
    }

    public UtenteAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Utente> objects) {
        this(context, resource, objects, null);
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_utente_voto, parent, false);
            mViewHolder.profileImage = (ImageView) convertView.findViewById(R.id.profileImage);
            mViewHolder.nome = (TextView) convertView.findViewById(R.id.nome);
            mViewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            mViewHolder.text = (EditText) convertView.findViewById(R.id.motivazioni);
            mViewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final Utente utente = utenti.get(position);
        if (utente != null) {
            if(utente.getProfilePic() == null) {
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessTracker.getAccessTracker().getAccessToken(),
                        "/" + utente.getId() + "/picture",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(final GraphResponse response) {
                                new AsyncTask<Object, Object, Bitmap>() {

                                    @Override
                                    protected Bitmap doInBackground(Object... params) {
                                        String src;
                                        try {
                                            src = response.getJSONObject().getJSONObject("data").getString("url");
                                            URL url = new URL(src);
                                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                            connection.setDoInput(true);
                                            connection.connect();
                                            InputStream input = connection.getInputStream();
                                            return BitmapFactory.decodeStream(input);
                                        } catch (JSONException | IOException | NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Bitmap o) {
                                        super.onPostExecute(o);
                                        utente.setProfilePic(o);
                                        mViewHolder.profileImage.setImageBitmap(utente.getProfilePic());
                                        mViewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }.execute();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("redirect", "false");
                parameters.putString("type", "large");
                parameters.putString("w‌​idth", "100");
                parameters.putString("height", "100");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
                mViewHolder.profileImage.setImageBitmap(utente.getProfilePic());
            }
            mViewHolder.nome.setText(utente.toString());
            mViewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    UtenteAdapter.this.voti.get(position).setVoto(rating);
                }
            });
            mViewHolder.ratingBar.setRating(voti.get(position).getVoto());
            mViewHolder.text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    UtenteAdapter.this.voti.get(position).setMotivo(((TextView) v).getText().toString());
                }
            });
            mViewHolder.text.setText(voti.get(position).getMotivo());
        }
        return convertView;
    }
}
