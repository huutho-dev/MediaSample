package com.htn.samplefragment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class MediaEntity implements Parcelable{
    public String mType; /*audio, video, image*/
    public String mUrlContent;
    public String mUrlThumbnail;

    public MediaEntity(String type, String urlContent, String urlThumbnail) {
        mType = type;
        mUrlContent = urlContent;
        mUrlThumbnail = urlThumbnail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mType);
        dest.writeString(this.mUrlContent);
        dest.writeString(this.mUrlThumbnail);
    }

    protected MediaEntity(Parcel in) {
        this.mType = in.readString();
        this.mUrlContent = in.readString();
        this.mUrlThumbnail = in.readString();
    }

    public static final Creator<MediaEntity> CREATOR = new Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel source) {
            return new MediaEntity(source);
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[size];
        }
    };
}
