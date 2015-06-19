package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import de.greenrobot.event.EventBus;

public class EmPubLiteActivity extends Activity {

    private ViewPager pager = null;
    private ContentsAdapter adapter = null;
    private static final String MODEL = "model";
    private static final String PREF_LAST_POSITION="lastPosition";
    private static final String PREF_KEEP_SCREEN_ON="keepScreenOn";
    private static final String PREF_SAVE_LAST_POSITION = "saveLastPosition";
    private ModelFragment mfrag=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupStrictMode();

        setContentView(R.layout.main);
        pager = (ViewPager)findViewById(R.id.pager);
        findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
        pager.setVisibility(View.GONE);

        getActionBar().setHomeButtonEnabled(true);
        MyReceiver.scheduleAlarm(this);
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
                pager.setCurrentItem(0, false);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, Preferences.class));
                return true;
            case R.id.notes:
                Intent noteActivity = new Intent(this, NoteActivity.class);
                noteActivity.putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem());
                startActivity(noteActivity);
                return true;
            case R.id.update:
                //startService(new Intent(this, DownloadCheckService.class));
                WakefulIntentService.sendWakefulWork(this, DownloadCheckService.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if(adapter == null) {
            mfrag = (ModelFragment)getFragmentManager().findFragmentByTag(MODEL);
            if(mfrag == null) {
                mfrag = new ModelFragment();
                getFragmentManager().beginTransaction().add(mfrag, MODEL).commit();
            }
            else if(mfrag.getBook() != null) {
                setupPager(mfrag.getBook());
            }
        }

        if(mfrag.getPrefs() != null) {
            boolean isKeepScreenOn = mfrag.getPrefs().getBoolean(PREF_KEEP_SCREEN_ON, false);
            pager.setKeepScreenOn(isKeepScreenOn);
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        if(mfrag.getPrefs() != null) {
            int position = pager.getCurrentItem();
            mfrag.getPrefs().edit().putInt(PREF_LAST_POSITION, position).apply();
        }
        super.onPause();
    }

    private void setupPager(BookContents contents) {
        adapter = new ContentsAdapter(this, contents);
        SharedPreferences prfs = mfrag.getPrefs();

        pager.setAdapter(adapter);
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);

        if(prfs != null) {
            if(prfs.getBoolean(PREF_SAVE_LAST_POSITION, false)) {
                pager.setCurrentItem(prfs.getInt(PREF_LAST_POSITION, 0));
            }
        }
        pager.setKeepScreenOn(prfs.getBoolean(PREF_KEEP_SCREEN_ON, false));
    }

    public void onEventMainThread(BookLoadedEvent event) {
        setupPager(event.getBook());
    }

    private void setupStrictMode() {
        StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder().detectNetwork();
        if(BuildConfig.DEBUG) {
            builder.penaltyDeath();
        }
        else {
            builder.penaltyLog();
        }
        StrictMode.setThreadPolicy(builder.build());
    }
}
