package com.example.xyzreader.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mike on 12/12/17.
 */

public class Item implements Parcelable {

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int i) {
            return new Item[i];
        }

    };
    private String id;
    private String title;
    private String author;
    private String body;
    private String thumb_url;
    private String photo_url;
    private String published_date;
    private Double aspect_ratio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    public Double getAspect_ratio() {
        return aspect_ratio;
    }

    public void setAspect_ratio(Double aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public Item() {
    }

    public Item(String id, String title, String author, String body, String thumb_url, String photo_url,
                String published_date, Double aspect_ratio) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.body = body;
        this.thumb_url = thumb_url;
        this.photo_url = photo_url;
        this.published_date = published_date;
        this.aspect_ratio = aspect_ratio;
    }

    private Item(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.readString();
        body = in.readString();
        thumb_url = in.readString();
        photo_url = in.readString();
        published_date = in.readString();
        aspect_ratio = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(body);
        parcel.writeString(thumb_url);
        parcel.writeString(photo_url);
        parcel.writeString(published_date);
        parcel.writeDouble(aspect_ratio);
    }
}
