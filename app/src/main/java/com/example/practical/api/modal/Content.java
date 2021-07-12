
package com.example.practical.api.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Content implements Parcelable {

    @SerializedName("mediaDate")
    private MediaDate mMediaDate;
    @SerializedName("mediaId")
    private Long mMediaId;
    @SerializedName("mediaTitleCustom")
    private String mMediaTitleCustom;
    @SerializedName("mediaType")
    private String mMediaType;
    @SerializedName("mediaUrl")
    private String mMediaUrl;
    @SerializedName("mediaUrlBig")
    private String mMediaUrlBig;

    protected Content(Parcel in) {
        if (in.readByte() == 0) {
            mMediaId = null;
        } else {
            mMediaId = in.readLong();
        }
        mMediaTitleCustom = in.readString();
        mMediaType = in.readString();
        mMediaUrl = in.readString();
        mMediaUrlBig = in.readString();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public MediaDate getMediaDate() {
        return mMediaDate;
    }

    public void setMediaDate(MediaDate mediaDate) {
        mMediaDate = mediaDate;
    }

    public Long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(Long mediaId) {
        mMediaId = mediaId;
    }

    public String getMediaTitleCustom() {
        return mMediaTitleCustom;
    }

    public void setMediaTitleCustom(String mediaTitleCustom) {
        mMediaTitleCustom = mediaTitleCustom;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    public String getMediaUrl() {
        return mMediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        mMediaUrl = mediaUrl;
    }

    public String getMediaUrlBig() {
        return mMediaUrlBig;
    }

    public void setMediaUrlBig(String mediaUrlBig) {
        mMediaUrlBig = mediaUrlBig;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mMediaId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(mMediaId);
        }
        dest.writeString(mMediaTitleCustom);
        dest.writeString(mMediaType);
        dest.writeString(mMediaUrl);
        dest.writeString(mMediaUrlBig);
    }
}
