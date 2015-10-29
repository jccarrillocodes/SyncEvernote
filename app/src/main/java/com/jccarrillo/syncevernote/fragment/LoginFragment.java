package com.jccarrillo.syncevernote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;
import com.jccarrillo.syncevernote.R;
import com.jccarrillo.syncevernote.activity.MainActivity;
import com.jccarrillo.syncevernote.manager.EvernoteSessionManager;

/**
 * Created by Juan Carlos on 29/10/2015.
 */
public class LoginFragment extends Fragment {

    private static final int REQUEST_CODE_LOGIN2 = 131930;
    private View mTextInitial;
    private View mTextFail;
    private Button mButtonRetry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        linkListeners();
        everNoteLogin();
    }

    private void initialize( View view ){
        setRetainInstance(true);

        mTextInitial = view.findViewById(R.id.textViewInitialText);
        mTextFail = view.findViewById(R.id.linearLayout1);
        mButtonRetry = (Button) view.findViewById(R.id.buttonRetryLogin);

        ((MainActivity)getActivity()).setupMenu(false,false);
    }

    private void linkListeners(){
        mButtonRetry.setOnClickListener(onButtonRetryClicked);
    }

    private View.OnClickListener onButtonRetryClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            everNoteLogin();
        }
    };

    private void everNoteLogin(){
        if( EvernoteSessionManager.getSession().isLoggedIn() ){

        } else
            EvernoteSessionManager.getSession().authenticate(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_CODE_LOGIN2 )
            resultCode = EvernoteSessionManager.getSession().isLoggedIn() ? Activity.RESULT_OK : Activity.RESULT_CANCELED;

        if( requestCode == EvernoteSession.REQUEST_CODE_LOGIN ||
                requestCode == REQUEST_CODE_LOGIN2 ){
            if( resultCode == Activity.RESULT_OK ){
                // Si el activity de EverNote no está bien implementado, puede causar un loop entre Activity
                everNoteLogin();
            } else {
                mTextFail.setVisibility(View.VISIBLE);
                mTextInitial.setVisibility(View.GONE);
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
