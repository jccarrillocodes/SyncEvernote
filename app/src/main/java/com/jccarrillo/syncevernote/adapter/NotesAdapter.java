package com.jccarrillo.syncevernote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.jccarrillo.syncevernote.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan Carlos on 28/10/2015.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Note> mList;

    public NotesAdapter( Context context ){
        mLayoutInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
    }

    public void setData( List<Note> list ){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mLayoutInflater.inflate(R.layout.item_note, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Note item = mList.get(i);
        viewHolder.mTitle.setText(item.getTitle() );
        viewHolder.mContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public TextView mContent;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.textView);
            mContent = (TextView) v.findViewById(R.id.textView2);
        }
    }

}
