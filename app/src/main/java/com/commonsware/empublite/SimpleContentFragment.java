package com.commonsware.empublite;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.annotation.SuppressLint;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleContentFragment extends WebViewFragment {

    private static final String KEY_FILE = "file";

    //this is a static factory method, in order to pass a parameter into this fragment
    protected static SimpleContentFragment newInstance(String file) {
        SimpleContentFragment f = new SimpleContentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater,container,savedInstanceState);

        getWebView().getSettings().setJavaScriptEnabled(true);
        getWebView().getSettings().setSupportZoom(true);
        getWebView().getSettings().setBuiltInZoomControls(true);
        getWebView().loadUrl(getPage());

        return result;
    }

    private String getPage() {
        String res;
        res = getArguments().getString(KEY_FILE);
        return res;
    }
}
