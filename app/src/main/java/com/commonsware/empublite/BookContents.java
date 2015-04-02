package com.commonsware.empublite;

import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * Created by abc on 3/24/15.
 */
public class BookContents {
    String title;
    List<Chapter> chapters;
    File baseDir = null;

    int getChapterCount() {
        return chapters.size();
    }

    String getChapterFile(int position) {
        return chapters.get(position).file;
    }

    String getTitle() {
        return title;
    }

    static class Chapter {
        String file;
        String title;
    }

    void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    String getChapterPath(int position) {
        String file = getChapterFile(position);
        if(baseDir == null) {
            return ("file:///android_asset/book/" + file);
        }
        return (Uri.fromFile(new File(baseDir, file)).toString());
    }
}
