package com.checkout;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.util.AbstractApplicationLifeCycleHelper;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;

/**
 * Application class responsible for initializing singletons and other common components.
 */
public class Application extends MultiDexApplication {

    private final static String LOG_TAG = Application.class.getSimpleName();
    private AbstractApplicationLifeCycleHelper applicationLifeCycleHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        applicationLifeCycleHelper = new AbstractApplicationLifeCycleHelper(this) {
            @Override
            protected void applicationEnteredForeground() {
                final PinpointManager pinpointManager = AWSMobileClient.defaultMobileClient()
                        .getPinpointManager();
                pinpointManager.getSessionClient().startSession();
                // handle any events when app goes to foreground...
            }

            @Override
            protected void applicationEnteredBackground() {
                Log.d(LOG_TAG, "Detected application has entered the background.");
                final PinpointManager pinpointManager = AWSMobileClient.defaultMobileClient()
                        .getPinpointManager();
                pinpointManager.getSessionClient().stopSession();
                pinpointManager.getAnalyticsClient().submitEvents();
                // handle any events when app goes to background...
            }
        };
    }
}