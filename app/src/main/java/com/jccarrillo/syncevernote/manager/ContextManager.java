package com.jccarrillo.syncevernote.manager;

import android.content.Context;

/**
 * Created by Juan Carlos on 27/10/2015.
 */
public class ContextManager {

    private static Context mSharedContext;

    /**
     * Obtiene el context compartido
     * @return
     */
    public static Context getContext(){
        return mSharedContext;
    }

    /**
     * Almacena el contexto para compartirlo en futuras ocasiones
     * @param context
     */
    public static void setContext( Context context ){
        mSharedContext = context;
    }
}
