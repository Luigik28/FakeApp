package luigik.fakeapp.controller.facebook;


import android.app.Activity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.Collection;

import static com.facebook.FacebookSdk.getApplicationContext;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FacebookLogin {

    private static FacebookLogin facebookLogin = new FacebookLogin();
    private boolean initialized = false;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    public static Collection<String> defaultPermission = Arrays.asList("public_profile", "user_friends", "email");

    private FacebookLogin() {
    }

    public static FacebookLogin getFacebookLogin() {
        return facebookLogin;
    }

    @SuppressWarnings("deprecation")
    public void initialize(Activity activity) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(activity);
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void login(Activity activity, FacebookCallback<LoginResult> facebookCallback) {
        login(activity,facebookCallback, defaultPermission);
        ProfileManager.getProfileManager().setProfile(Profile.getCurrentProfile());
    }

    public void login(Activity activity, FacebookCallback<LoginResult> facebookCallback, Collection<String> permission) {
        LoginManager.getInstance().registerCallback(this.getCallbackManager(), facebookCallback);
        LoginManager.getInstance().logInWithReadPermissions(activity, permission);
    }

    public void logOut() {
        LoginManager.getInstance().logOut();
    }
}
