package com.robotemplates.cookbook.pojo;

import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;

import com.robotemplates.cookbook.logging.L;

/**
 * Created by willy on 17/07/15.
 */
public class SubReddit  implements Parcelable {

    public static final Parcelable.Creator<SubReddit> CREATOR
            = new Parcelable.Creator<SubReddit>() {
        public SubReddit createFromParcel(Parcel in) {
            L.m("create from parcel :SubReddit");
            return new SubReddit(in);
        }

        public SubReddit[] newArray(int size) {
            return new SubReddit[size];
        }
    };

    private long id;
    private String serverId;
    private String name;
    private String urlImage;

    public SubReddit() {

    }

    public SubReddit(Parcel input) {
        id = input.readLong();
        serverId = input.readString();
        name = input.readString();
        urlImage = input.readString();


    }

    public SubReddit(long id,
                 String name,
                 String urlImage) {
        this.id = id;
        this.name = name;
        this.urlImage = urlImage;

    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }


    @Override
    public String toString() {
        return "\nserverId: " + serverId +
                "\nName " + name +
                "\nurlImage " + urlImage +
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(serverId);
        dest.writeString(name);
        dest.writeString(urlImage);
    }
}
