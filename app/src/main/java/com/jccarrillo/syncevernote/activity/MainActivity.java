package com.jccarrillo.syncevernote.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.evernote.edam.type.Note;
import com.jccarrillo.syncevernote.R;
import com.jccarrillo.syncevernote.fragment.AddNoteFragment;
import com.jccarrillo.syncevernote.fragment.LoginFragment;
import com.jccarrillo.syncevernote.fragment.MainFragment;
import com.jccarrillo.syncevernote.manager.EvernoteSessionManager;

public class MainActivity extends AppCompatActivity {

    private Fragment mFragment;
    private MenuItem mMenuItemRefresh;
    private MenuItem mMenuItemSave;
    private MenuItem mMenuItemByDate;
    private MenuItem mMenuItemByTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState == null ) {
            if(EvernoteSessionManager.getSession().isLoggedIn())
                showNoteListFragment();
            else
                showLoginFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mMenuItemRefresh = menu.findItem(R.id.action_refresh);
        mMenuItemSave = menu.findItem(R.id.action_add);
        mMenuItemByDate = menu.findItem(R.id.action_order_date);
        mMenuItemByTitle = menu.findItem(R.id.action_order_title);

        return true;
    }

    public void setupMenu( boolean refresh, boolean save, boolean orderByTitle, boolean orderByDate ){
        if(mMenuItemRefresh!=null)
            mMenuItemRefresh.setVisible(refresh);
        if(mMenuItemSave!=null)
            mMenuItemSave.setVisible(save);
        if(mMenuItemByDate!=null)
            mMenuItemByDate.setVisible(orderByDate);
        if(mMenuItemByTitle!=null)
            mMenuItemByTitle.setVisible(orderByTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.host_fragment);
        if( mFragment != null )
            return mFragment.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    protected void navigateToFragment( Fragment fragment, boolean backStack ){
        mFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft
                .replace(R.id.host_fragment, mFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if( backStack )
            ft.addToBackStack(mFragment.getClass().toString()); // No para el mundo real

        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( mFragment != null )
            mFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showLoginFragment(){
        navigateToFragment(new LoginFragment(), false);
    }

    public void showNoteListFragment(){
        navigateToFragment(new MainFragment(), false);
    }

    public void showAddNoteFragment(@Nullable Note note){
        Bundle bundle = new Bundle();
        if( note != null )
            bundle.putSerializable(AddNoteFragment.BUNDLE_NOTE,note);

        AddNoteFragment fragment = new AddNoteFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    public void showAddNoteFragment(){
        showAddNoteFragment(null);
    }
}
