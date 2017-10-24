package luigik.fakeapp.controller.facebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.facebook.Profile;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import luigik.fakeapp.controller.dao.UtenteDAO;
import luigik.fakeapp.model.entities.Utente;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ProfileManager {

    private static ProfileManager profileManager;
    private Profile currentProfile;

    private ProfileManager() {
    }

    public static ProfileManager getProfileManager() {
        if (profileManager == null)
            profileManager = new ProfileManager();
        return profileManager;
    }

    void setProfile(Profile profile) {
        currentProfile = profile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public Bitmap getProfilePicture() throws IOException {
        return BitmapFactory.decodeStream(new URL(getCurrentProfile().getProfilePictureUri(200, 200).toString()).openConnection().getInputStream());
    }

    public void pictureToImageViewAsync(final ImageView imageView) {
        new AsyncTask<Object, Object, Object>() {

            Bitmap bitmap = null;

            @Override
            protected Object doInBackground(Object... params) {
                try {
                    bitmap = getProfilePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
                imageView.invalidate();
            }
        }.execute();
    }

    public Boolean registerToDB() {
        try {
            new UtenteDAO().insert(new Utente(getCurrentProfile()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRegistered() {
        try {
            return new UtenteDAO().select(getCurrentProfile().getId()) != null;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
