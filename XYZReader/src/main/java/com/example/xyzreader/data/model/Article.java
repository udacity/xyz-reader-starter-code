package com.example.xyzreader.data.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Article implements BaseModel {

	private Long id;
	private String serverId;
	private String title;
	private String author;
	private String body;
	private String thumbUrl;
	private String photoUrl;
	private AspectRatio aspectRatio;
	private Date publishedDate;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
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

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public AspectRatio getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(AspectRatio aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Article article = (Article) o;
		return Objects.equals(id, article.id) &&
				Objects.equals(serverId, article.serverId) &&
				Objects.equals(title, article.title) &&
				Objects.equals(author, article.author) &&
				Objects.equals(body, article.body) &&
				Objects.equals(thumbUrl, article.thumbUrl) &&
				Objects.equals(photoUrl, article.photoUrl) &&
				aspectRatio == article.aspectRatio &&
				Objects.equals(publishedDate, article.publishedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, serverId, title, author, body, thumbUrl, photoUrl, aspectRatio, publishedDate);
	}

	@NonNull
	@Override
	public String toString() {
		return "Article{" +
				"id=" + id +
				", serverId='" + serverId + '\'' +
				", title='" + title + '\'' +
				", author='" + author + '\'' +
				", body='" + body + '\'' +
				", thumbUrl='" + thumbUrl + '\'' +
				", photoUrl='" + photoUrl + '\'' +
				", aspectRatio=" + aspectRatio +
				", publishedDate=" + publishedDate +
				'}';
	}
}
