
package com.example.practical.api.modal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MediaModal {

    @SerializedName("content")
    private ArrayList<Content> mContent;
    @SerializedName("lang")
    private String mLang;
    @SerializedName("status")
    private Boolean mStatus;

    public ArrayList<Content> getContent() {
        return mContent;
    }

    public void setContent(ArrayList<Content> content) {
        mContent = content;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

}
