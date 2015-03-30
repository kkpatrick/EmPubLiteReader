package com.commonsware.empublite;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import de.greenrobot.event.EventBus;

/**
 * Created by abc on 3/27/15.
 */
public class NoteFragment extends Fragment implements TextWatcher{
    private static final String KEY_POSITION = "position";
    private EditText editor = null;
    private ShareActionProvider share = null;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND).setType("text/plain");

    public interface Contract {
        void closeNotes();
    }

    static NoteFragment newInstance(int position) {
        NoteFragment frag = new NoteFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.editor, container, false);
        editor = (EditText)result.findViewById(R.id.editor);
        editor.addTextChangedListener(this);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(TextUtils.isEmpty(editor.getText())) {
            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
            db.loadNote(getPosition());
        }
    }

    @Override
    public void onPause() {
        DatabaseHelper.getInstance(getActivity()).updateNote(getPosition(), editor.getText().toString());
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);
        share = (ShareActionProvider)menu.findItem(R.id.share).getActionProvider();
        share.setShareIntent(shareIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete) {
            editor.setText(null);
            getContract().closeNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getPosition() {
        return getArguments().getInt(KEY_POSITION, -1);
    }

    public void onEventMainThread(NoteLoadedEvent event) {
        if(event.getPosition() == getPosition()) {
            editor.setText(event.getProse());
        }
    }

    //contract pattern, 需要关闭fragment所在的activity，用到contract pattern。
    private Contract getContract() {
        return (Contract)getActivity();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, editable.toString());
    }
}
