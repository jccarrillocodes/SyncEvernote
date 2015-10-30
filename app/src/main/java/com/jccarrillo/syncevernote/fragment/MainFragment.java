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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Juan Carlos on 28/10/2015.
 */
public class MainFragment extends Fragment {

    private static final int ORDERTYPE_DATE = 0,
                            ORDERTYPE_TITLE = 1;

    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mActionButtonAdd;
    private View mLoading;
    private int mOrderType = ORDERTYPE_DATE;

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

        updateMenu();
    }

    private void updateMenu(){
        ((MainActivity)getActivity()).setupMenu(
                true,
                false,
                mOrderType != ORDERTYPE_TITLE,
                mOrderType != ORDERTYPE_DATE
        );
    }

    private void linkListeners(){
        mActionButtonAdd.setOnClickListener(onActionButtonAddClick);
        mAdapter.setOnItemClickListener(onItemClicked);
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
        switch (item.getItemId()){
            case R.id.action_refresh:
                populate();
                break;
            case R.id.action_order_date:
                mOrderType = ORDERTYPE_DATE;
                populateList();
                break;
            case R.id.action_order_title:
                mOrderType = ORDERTYPE_TITLE;
                populateList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateList(){
        if( mAdapter != null )
            populateList( mAdapter.getList() );
    }

    private void populateList( List<Note> list ){
        Collections.sort(list, new NotesComparator(mOrderType));
        mAdapter.setData(list);
        updateMenu();
    }

    private NotesAdapter.OnNoteClick onItemClicked = new NotesAdapter.OnNoteClick() {
        @Override
        public void onNoteClicked(Note note) {
            ((MainActivity)getActivity()).showAddNoteFragment(note);
        }
    };

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

    public static class NotesComparator implements Comparator<Note> {
        private int mOrdertype;

        public NotesComparator( int orderType ){
            super();
            mOrdertype = orderType;
        }


        @Override
        public int compare(Note lhs, Note rhs) {
            switch (mOrdertype){
                case ORDERTYPE_DATE:
                    return lhs.getCreated() < rhs.getCreated() ? -1 :
                            lhs.getCreated() > rhs.getCreated() ? 1 :
                                    0;
                case ORDERTYPE_TITLE:
                    return lhs.getTitle().compareTo(rhs.getTitle());
                default:
                    return 0;
            }
        }
    }

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
                populateList(notes);
            }
        }
    }

    private View.OnClickListener onActionButtonAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            activity.showAddNoteFragment();
        }
    };
}
