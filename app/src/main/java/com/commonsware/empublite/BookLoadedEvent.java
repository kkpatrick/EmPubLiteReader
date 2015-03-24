package com.commonsware.empublite;

/**
 * Created by abc on 3/24/15.
 */
public class BookLoadedEvent {
    private BookContents contents = null;

    public BookLoadedEvent(BookContents contents) {
        this.contents = contents;
    }

    public BookContents getBook() {
        return contents;
    }
}
