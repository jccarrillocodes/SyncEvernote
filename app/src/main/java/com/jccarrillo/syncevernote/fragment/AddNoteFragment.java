package com.jccarrillo.syncevernote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.jccarrillo.syncevernote.R;
import com.jccarrillo.syncevernote.activity.MainActivity;
import com.jccarrillo.syncevernote.manager.EvernoteSessionManager;

import java.util.ArrayList;

/**
 * Created by Juan Carlos on 29/10/2015.
 */
public class AddNoteFragment extends Fragment {

    private static final int REQUEST_CODE = 0x1234;

    private EditText etTitle;
    private EditText etContent;
    private Button btnVoice;
    private View llSaving;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        linkListeners();
    }

    private void initialize( View view ){
        setRetainInstance(true);

        etTitle = (EditText) view.findViewById(R.id.editText);
        etContent = (EditText) view.findViewById(R.id.editText2);
        btnVoice = (Button) view.findViewById(R.id.btnSpeak);
        llSaving = view.findViewById(R.id.llSaving);

        ((MainActivity)getActivity()).setupMenu(false,true,false,false);
    }

    private void linkListeners() {
        btnVoice.setOnClickListener(onBtnVoiceClick);
    }

    private View.OnClickListener onBtnVoiceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startVoiceRecognitionActivity();
        }
    };

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_input_add));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS);
            StringBuilder sb = new StringBuilder();
            if( etContent.getText().length() != 0 ) {
                sb.append(etContent.getText().toString());
                sb.append("\n");
            } else
                sb.append(etContent.getText().toString());

            for( String match: matches ) {
                sb.append(match);
                // Se deberían mostrar las posibilidades, pero no es el caso
                break;
            }

            etContent.setText(sb);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.action_add ){
            addNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNote(){
        if (!EvernoteSessionManager.getSession().isLoggedIn()) {
            return;
        }

        llSaving.setVisibility(View.VISIBLE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSessionManager.getSession().getEvernoteClientFactory().getNoteStoreClient();

        Note note = new Note();
        note.setTitle(etTitle.getText().toString());
        note.setContent(EvernoteUtil.NOTE_PREFIX + etContent.getText().toString() + EvernoteUtil.NOTE_SUFFIX);

        noteStoreClient.createNoteAsync(note, mOnCreateNoteAsync );
    }

    private EvernoteCallback<Note> mOnCreateNoteAsync = new EvernoteCallback<Note>() {
        @Override
        public void onSuccess(Note result) {
            if( getActivity() != null ) {
                Toast.makeText(getActivity().getApplicationContext(), result.getTitle() + " ha sido creada", Toast.LENGTH_LONG).show();
                llSaving.setVisibility(View.GONE);
                getFragmentManager().popBackStack();
            }
        }

        @Override
        public void onException(Exception exception) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.addnote_error, Toast.LENGTH_LONG).show();
            llSaving.setVisibility(View.GONE);
        }
    };
}
