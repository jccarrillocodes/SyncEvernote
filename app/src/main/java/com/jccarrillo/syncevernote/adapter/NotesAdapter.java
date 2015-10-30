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

    public interface OnNoteClick {
        public void onNoteClicked( Note note );
    }

    private LayoutInflater mLayoutInflater;
    private List<Note> mList;
    private OnNoteClick mOnNoteClick;


    public NotesAdapter( Context context ){
        mLayoutInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
    }

    public void setData( List<Note> list ){
        mList = list;
        notifyDataSetChanged();
    }

    public List<Note> getList(){
        return mList;
    }

    public void setOnItemClickListener( OnNoteClick listener ){
        mOnNoteClick = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mLayoutInflater.inflate(R.layout.item_note, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(mOnClickListener);
        return vh;

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( mOnNoteClick != null && v.getTag() != null ){
                try {
                    Note item = mList.get((Integer) v.getTag());
                    mOnNoteClick.onNoteClicked(item);
                }catch (Exception e ){

                }
            }
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Note item = mList.get(i);
        viewHolder.mTitle.setText(item.getTitle() );
        viewHolder.mContent.setText(item.getContent());
        viewHolder.itemView.setTag(i);
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
