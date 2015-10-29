package com.jccarrillo.syncevernote.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.jccarrillo.syncevernote.R;
import com.jccarrillo.syncevernote.activity.MainActivity;
import com.jccarrillo.syncevernote.adapter.NotesAdapter;
import com.jccarrillo.syncevernote.manager.EvernoteSessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan Carlos on 28/10/2015.
 */
public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mActionButtonAdd;
    private View mLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        linkListeners();
        populate();
    }

    /**
     * Inicializamos las variables, sobretodo las dependientes de la vista
     * @param view
     */
    private void initialize( View view ) {
        setRetainInstance(true);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvNotesList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.btnAdd);

        mAdapter = new NotesAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        mLoading = view.findViewById(R.id.pbLoading);

        ((MainActivity)getActivity()).setupMenu(true,false);

    }

    private void linkListeners(){
        mActionButtonAdd.setOnClickListener(onActionButtonAddClick);
    }

    /**
     * Poblamos los datos
     */
    private void populate(){
        if( EvernoteSessionManager.getSession().isLoggedIn() ) {
            EvernoteNoteStoreClient noteStoreClient = EvernoteSessionManager.getSession().getEvernoteClientFactory().getNoteStoreClient();
            mLoading.setVisibility(View.VISIBLE);
            noteStoreClient.listNotebooksAsync(onNotesListBookCallBack);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.action_refresh )
            populate();
        return super.onOptionsItemSelected(item);
    }

    private EvernoteCallback<List<Notebook>> onNotesListBookCallBack = new EvernoteCallback<List<Notebook>>() {
        @Override
        public void onSuccess(List<Notebook> result) {
            new AsyncLoadNotebooks().execute(result);
        }

        @Override
        public void onException(Exception exception) {
            Log.e(MainFragment.class.toString(), "Error retrieving notebooks", exception);
            Toast.makeText(getActivity().getApplicationContext(), R.string.list_error, Toast.LENGTH_LONG).show();
            mLoading.setVisibility(View.GONE);
        }
    };

    private class AsyncLoadNotebooks extends AsyncTask<List<Notebook>,Void,List<Note>>{

        @Override
        protected List<Note> doInBackground(List<Notebook>... params) {
            EvernoteNoteStoreClient noteStoreClient = EvernoteSessionManager.getSession().getEvernoteClientFactory().getNoteStoreClient();

            List<Note> result = new ArrayList<>();

            int pageSize = 10;

            NoteFilter filter = new NoteFilter();
            filter.setOrder(NoteSortOrder.UPDATED.getValue());

            NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
            spec.setIncludeTitle(true);
            spec.setIncludeNotebookGuid(true);


            try {
                NotesMetadataList notes = noteStoreClient.findNotesMetadata( filter, 0, pageSize, spec);

                for(NoteMetadata note: notes.getNotes()){

                    Note n = noteStoreClient.getNote(note.getGuid(),true,false,false,false);

                    result.add(n);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            if( getActivity() != null ){
                mLoading.setVisibility(View.GONE);
                mAdapter.setData(notes);
            }
        }
    }

    private View.OnClickListener onActionButtonAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
