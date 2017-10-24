package luigik.fakeapp.controller.dao;

import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import luigik.fakeapp.model.Entity;
import luigik.fakeapp.util.URLUtil;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PhpDAO<T extends Entity> {

    protected Uri connection = null;

    public URL getConnection(final String query) throws MalformedURLException {
        connection = URLUtil.buildUri("query.php",new HashMap<String, String>() {
            {
                put("query",query);
            }
        });
        return new URL(connection.toString());
    }

    public abstract void insert(T toInsert) throws IOException;

    public abstract T select(String id) throws IOException, JSONException;

    public abstract List<T> getAll() throws IOException;

}
