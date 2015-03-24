package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.greenrobot.event.EventBus;

public class EmPubLiteActivity extends Activity {

    private ViewPager pager = null;
    private ContentsAdapter adapter = null;
    private static final String MODEL = "model";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        pager = (ViewPager)findViewById(R.id.pager);
        //adapter = new ContentsAdapter(this);
        pager.setAdapter(adapter);
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.about:
                Intent intentAbout = new Intent(this, SimpleContentActivity.class);
                intentAbout.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/about.html");
                startActivity(intentAbout);
                return true;
            case R.id.help:
                Intent intentHelp = new Intent(this, SimpleContentActivity.class);
                intentHelp.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/help.html");
                startActivity(intentHelp);
                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if(adapter == null) {
            ModelFragment mfrag = (ModelFragment)getFragmentManager().findFragmentByTag(MODEL);
            if(mfrag == null) {
                getFragmentManager().beginTransaction().add(new ModelFragment(), MODEL).commit();
            }
            else if(mfrag.getBook() != null) {
                setupPager(mfrag.getBook());
            }
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void setupPager(BookContents contents) {
        adapter = new ContentsAdapter(this, contents);
        pager.setAdapter(adapter);
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(BookLoadedEvent event) {
        setupPager(event.getBook());
    }
}
