package com.commonsware.empublite;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadCheckService extends IntentService {

    private static final String OUR_BOOK_DATE = "20120418";
    private static final String UPDATE_FILENAME = "book.zip";
    public static final String UPDATE_BASEDIR = "updates"; // the latest book update will reside

    public DownloadCheckService() {
        super("DownloadCheckService");
    }

    private static void unzip(File src, File dest) throws IOException {
        InputStream is = new FileInputStream(src);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;

        dest.mkdirs();

        while((ze = zis.getNextEntry()) != null) {
            byte[] buffer = new byte[16384];
            int count;
            FileOutputStream fos = new FileOutputStream(new File(dest, ze.getName()));
            BufferedOutputStream out = new BufferedOutputStream(fos);

            try {
                while((count = zis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.flush();
            } finally {
                fos.getFD().sync();
                out.close();
            }
            zis.closeEntry();
        }
        zis.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String url = getUpdateUrl();
            if (url != null) {
                File book = download(url);
                File updateDir = new File(getFilesDir(), UPDATE_BASEDIR);

                updateDir.mkdirs();
                unzip(book, updateDir);
                book.delete();
                EventBus.getDefault().post(new BookUpdatedEvent());
            }
        } catch( Exception e) {
            Log.e(getClass().getSimpleName(), "Exception downloading update", e);
        }
    }

    private String getUpdateUrl() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://commonsware.com").build();
        BookUpdateInterface updateInterface = restAdapter.create(BookUpdateInterface.class);
        BookUpdateInfo info = updateInterface.update();
        if(info.updatedOn.compareTo(OUR_BOOK_DATE) > 0) {
            return info.updateUrl;
        }
        return null;
    }

    private File download(String url) throws MalformedURLException, IOException {
        //create a file in our internal storage(getFileDir())
        File output = new File(getFilesDir(), UPDATE_FILENAME);

        if(output.exists()) {
            output.delete();
        }

        HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
        FileOutputStream fos = new FileOutputStream(output.getPath());
        BufferedOutputStream out = new BufferedOutputStream(fos);
        try {
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[16384];
            int len = 0;

            while((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.flush();
        } finally {
            fos.getFD().sync();
            out.close();
            c.disconnect();
        }
        return output;
    }
}
