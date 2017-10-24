package luigik.fakeapp.util;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public abstract class URLUtil {

    private final static String SCHEME = "http";
    private final static String AUTHORITY = "www.luigik.altervista.org";
    private final static String PRIMARY_FOLDER = "fake";

    private static Uri.Builder getDefaultBuilder() {
        return new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).appendPath(PRIMARY_FOLDER);
    }

    public static Uri buildUri(String path) {
        return getDefaultBuilder().appendPath(path).build();
    }

    public static Uri buildUri(String path, Map<String,String> parameters) {
        Uri.Builder builder = buildUri(path).buildUpon();
        for(String key : parameters.keySet())
            builder.appendQueryParameter(key,parameters.get(key));
        return builder.build();
    }

    public static String getResponseFromURL(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        String output = "";
        while((line = br.readLine()) != null) {
            output += line;
        }
        br.close();
        System.out.println(url);
        return output;
    }
}
