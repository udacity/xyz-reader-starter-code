package com.example.xyzreader.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Package Name:   com.example.xyzreader.pojo
 * Project:        xyz-reader-starter-code
 * Created by davis, on 9/4/17
 */

public class Article implements Parcelable {
    private long id;
    private String title;
    private String date;
    private String author;
    private String body;
    private String thumbnailUrl;
    private String photoUrl;

    public Article(long id, String title, String date, String author, String body, String thumbnailUrl, String photoUrl) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.author = author;
        this.body = body;
        this.thumbnailUrl = thumbnailUrl;
        this.photoUrl = photoUrl;
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getAuthor() {
        return author;
    }
    public String getBody() {
        return body;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    protected Article(Parcel in) {
        id = in.readLong();
        title = in.readString();
        date = in.readString();
        author = in.readString();
        body = in.readString();
        thumbnailUrl = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(author);
        dest.writeString(body);
        dest.writeString(thumbnailUrl);
        dest.writeString(photoUrl);
    }
}
