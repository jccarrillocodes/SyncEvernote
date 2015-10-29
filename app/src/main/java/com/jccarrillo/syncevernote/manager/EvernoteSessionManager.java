package com.jccarrillo.syncevernote.manager;

import android.content.Context;
import com.evernote.client.android.EvernoteSession;

/**
 * Created by Juan Carlos on 27/10/2015.
 */
public class EvernoteSessionManager {

    private static final String CONSUMER_KEY = "jccarrillo";
    private static final String CONSUMER_SECRET = "c0c5da5b8b35637f";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    private static EvernoteSession mEvernoteSession;

    public static void initialize( Context context ){
        mEvernoteSession = new EvernoteSession.Builder(context)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }

    public static EvernoteSession getSession(){
        return mEvernoteSession;
    }
}
