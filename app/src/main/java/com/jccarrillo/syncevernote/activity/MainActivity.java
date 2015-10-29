package com.jccarrillo.syncevernote.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jccarrillo.syncevernote.R;

public class MainActivity extends AppCompatActivity {

    private Fragment mFragment;
    private MenuItem mMenuItemRefresh;
    private MenuItem mMenuItemSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mMenuItemRefresh = menu.findItem(R.id.action_refresh);
        mMenuItemSave = menu.findItem(R.id.action_add);

        return true;
    }

    public void setupMenu( boolean refresh, boolean save ){
        if(mMenuItemRefresh!=null)
            mMenuItemRefresh.setVisible(refresh);
        if(mMenuItemSave!=null)
            mMenuItemSave.setVisible(save);
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
            mFragment.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
