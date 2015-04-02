package com.commonsware.empublite;

/**
 * Created by abc on 4/2/15.
 */
import retrofit.http.GET;
public interface BookUpdateInterface {
    @GET("/misc/empublite-update.json")
    BookUpdateInfo update();
}
