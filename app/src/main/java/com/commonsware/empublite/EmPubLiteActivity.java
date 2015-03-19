package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EmPubLiteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
                startActivity(intentAbout);
                return true;
            case R.id.help:
                Intent intentHelp = new Intent(this, SimpleContentActivity.class);
                startActivity(intentHelp);
                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
