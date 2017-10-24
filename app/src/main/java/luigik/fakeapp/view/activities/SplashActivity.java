package luigik.fakeapp.view.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import luigik.fakeapp.controller.facebook.AccessTracker;
import luigik.fakeapp.controller.facebook.FacebookLogin;
import luigik.fakeapp.controller.facebook.ProfileManager;
import luigik.fakeapp.R;

public class SplashActivity extends AppCompatActivity {

    FacebookLogin facebookLogin = FacebookLogin.getFacebookLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "luigik.fakeapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */
        facebookLogin.initialize(this);
        if (AccessTracker.getAccessTracker().getAccessToken() != null) {
            doLogin();
        } else {
            showLoginDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    private void doLogin() {
        facebookLogin.login(this, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("ID: " + ProfileManager.getProfileManager().getCurrentProfile().getId());
                System.out.println("TOKEN: " + AccessTracker.getAccessTracker().getAccessToken().getToken());
                new AsyncTask<Object, Object, Boolean>() {

                    boolean newUser = true;

                    @Override
                    protected Boolean doInBackground(Object... params) {
                        if (!ProfileManager.getProfileManager().isRegistered())
                            return ProfileManager.getProfileManager().registerToDB();
                        return !(newUser = false);
                    }

                    @Override
                    protected void onPostExecute(Boolean o) {
                        super.onPostExecute(o);
                        if (o != null && o) {
                            final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            if (newUser) {
                                new AlertDialog.Builder(SplashActivity.this)
                                        .setCancelable(false)
                                        .setMessage("Benvenuto, ora puoi competere anche tu e diventare FAKE dell'anno!")
                                        .setPositiveButton("Continua", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(intent);
                                            }
                                        })
                                        .create().show();
                            } else {
                                startActivity(intent);
                            }
                        } else {
                            new AlertDialog.Builder(SplashActivity.this).setCancelable(false)
                                    .setMessage("Errore").setNegativeButton("Esci",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SplashActivity.this.finish();
                                        }
                                    }).create().show();
                        }
                    }
                }.execute();
            }

            @Override
            public void onCancel() {
                SplashActivity.this.finish();
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                SplashActivity.this.finish();
            }
        });
        AccessTracker.getAccessTracker().startTracking();
    }

    private void showLoginDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Login Facebook").setMessage("Per proseguire effettuare il login tramite facebook");
        alertDialogBuilder.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashActivity.this.finish();
            }
        });
        alertDialogBuilder.setPositiveButton("Prosegui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doLogin();
            }
        }).setCancelable(false);
        alertDialogBuilder.create().show();
    }

}
