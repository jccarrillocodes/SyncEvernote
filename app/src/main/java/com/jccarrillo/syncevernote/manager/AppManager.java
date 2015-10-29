package com.jccarrillo.syncevernote.manager;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;

import com.jccarrillo.syncevernote.R;

import org.scribe.exceptions.OAuthException;

/**
 * Created by Juan Carlos on 27/10/2015.
 */
public class AppManager extends Application {

    private static Thread.UncaughtExceptionHandler defaultUEH;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextManager.setContext(this);
        EvernoteSessionManager.initialize(this);

        // Cazamos excepciones en hilos
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }

    public static Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                if( ex instanceof OAuthException ){
                    showThreadedToast(R.string.bad_credentials);
                }
                defaultUEH.uncaughtException(thread, ex);
            } catch (Exception e) {

            }
            System.exit(0);
        }
    };

    private static void showThreadedToast( final int resText ){
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(ContextManager.getContext(),resText,Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }
}
