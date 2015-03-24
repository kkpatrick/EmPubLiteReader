package com.commonsware.empublite;

import java.util.List;

/**
 * Created by abc on 3/24/15.
 */
public class BookContents {
    String title;
    List<Chapter> chapters;

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
}
