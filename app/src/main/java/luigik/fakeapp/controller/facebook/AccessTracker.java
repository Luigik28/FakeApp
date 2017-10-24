package luigik.fakeapp.controller.facebook;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AccessTracker {

    private static AccessTracker accessTracker = null;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private AccessTracker() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                setAccessToken(currentAccessToken);
            }
        };
        setAccessToken(AccessToken.getCurrentAccessToken());
    }

    public static AccessTracker getAccessTracker() {
        if (accessTracker == null) {
            accessTracker = new AccessTracker();
        }
        return accessTracker;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void stopTracking() {
        accessTokenTracker.stopTracking();
    }

    public void startTracking() {
        accessTokenTracker.startTracking();
    }

}
