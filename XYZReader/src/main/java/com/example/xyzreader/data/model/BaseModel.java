package com.example.xyzreader.data.model;

import android.support.annotation.NonNull;

public interface BaseModel {

	Long getId();

	String getServerId();

	@NonNull
	String toString();

	int hashCode();

	boolean equals(Object other);

}
