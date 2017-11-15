package com.checkout.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;
import com.amazonaws.mobilehelper.auth.signin.CognitoUserPoolsSignInProvider;
import com.checkout.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();

        identityManager.addIdentityProvider(CognitoUserPoolsSignInProvider.class);
        identityManager.doStartupAuth(this,
                new StartupAuthResultHandler() {
                    @Override
                    public void onComplete(final StartupAuthResult authResults) {
                        if (authResults.isUserSignedIn()) {
                            final IdentityProvider provider = identityManager.getCurrentIdentityProvider();
                            // If signed in previously with a provider
                            Toast.makeText(SplashActivity.this, String.format("Signed in with %s",
                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
                        } else {
                            // User has never signed in or refresh sign in failed
                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
                            doMandatorySignIn(identityManager);
                            return;
                        }

                        // Go to main activity and finish splash activity
                        goMain(SplashActivity.this);
                    }
                }, 2000);
    }

    private void doMandatorySignIn(final IdentityManager identityManager) {
        identityManager.signInOrSignUp(SplashActivity.this, new DefaultSignInResultHandler() {
            @Override
            public void onSuccess(Activity callingActivity, IdentityProvider provider) {
                if (provider != null) {
                    Log.d("SignInResultsHandler", String.format("User sign-in with %s provider succeeded",
                            provider.getDisplayName()));
                    Toast.makeText(callingActivity, String.format("Sign-in with %s succeeded.",
                            provider.getDisplayName()), Toast.LENGTH_LONG).show();
                }
                goMain(callingActivity);
            }

            @Override
            public boolean onCancel(Activity callingActivity) {
                return true;
            }
        });
        SplashActivity.this.finish();
    }

    /** Go to the main activity. */
    private void goMain(final Activity callingActivity) {
        callingActivity.startActivity(new Intent(callingActivity, ScanActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        callingActivity.finish();
    }
}
