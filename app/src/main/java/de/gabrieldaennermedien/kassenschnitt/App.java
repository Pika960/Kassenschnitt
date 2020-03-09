package de.gabrieldaennermedien.kassenschnitt;

//imports
import android.app.Application;
import android.content.Context;

/**
 * App is a helper class which has the purpose to give non android classes access to the
 * application context.
 */
@SuppressWarnings({"WeakerAccess", "RedundantSuppression"})
public class App extends Application {
    //private static instances
    private static App mApp;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    /**
     * getContext returns the current application context.
     * @return the application context.
     */
    public static Context getContext() {
        return mApp.getApplicationContext();
    }
}
