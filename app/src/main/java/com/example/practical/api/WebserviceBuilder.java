package com.example.practical.api;


import com.example.practical.api.modal.MediaModal;

import io.reactivex.Observable;
import retrofit2.http.GET;


/**
 * Declare all the APIs in this class with specific interface
 * e.g. Profile for Login/Register Apis
 */
public interface WebserviceBuilder {
    /**
     * for using custom Sub URL
     *
     * @return
     */
    //    for custom get URL
    @GET("getPressReleasesDocs")
    Observable<MediaModal> getMediaList();

    /**
     * ApiNames to differentiate APIs
     */
    enum ApiNames {
        mediaList
    }

}
