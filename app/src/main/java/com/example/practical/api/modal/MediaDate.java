
package com.example.practical.api.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MediaDate implements Parcelable {

    @SerializedName("dateString")
    private String mDateString;
    @SerializedName("year")
    private String mYear;

    protected MediaDate(Parcel in) {
        mDateString = in.readString();
        mYear = in.readString();
    }

    public static final Creator<MediaDate> CREATOR = new Creator<MediaDate>() {
        @Override
        public MediaDate createFromParcel(Parcel in) {
            return new MediaDate(in);
        }

        @Override
        public MediaDate[] newArray(int size) {
            return new MediaDate[size];
        }
    };

    public String getDateString() {
        return mDateString;
    }

    public void setDateString(String dateString) {
        mDateString = dateString;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDateString);
        dest.writeString(mYear);
    }
}
