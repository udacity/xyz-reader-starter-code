package com.example.xyzreader.data.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * Helper for loading a list of articles or a single article.
 */
public class ArticleLoader extends CursorLoader {
	private final LoaderListeners listeners;

	private ArticleLoader(Context context, Uri uri, LoaderListeners listeners) {
		super(context, uri, Query.PROJECTION, null, null, ItemsContract.Items.DEFAULT_SORT);
		this.listeners = listeners;
	}

	public static ArticleLoader newAllArticlesInstance(Context context, LoaderListeners listeners) {
		return new ArticleLoader(context, ItemsContract.Items.buildDirUri(), listeners);
	}

	public static ArticleLoader newInstanceForItemId(Context context, long itemId, LoaderListeners listeners) {
		return new ArticleLoader(context, ItemsContract.Items.buildItemUri(itemId), listeners);
	}

	@Override
	public Cursor loadInBackground() {
//		try {
//			Thread.sleep(5000); // TODO ${DATE} - Degub - Don commit or push this
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		return super.loadInBackground();
	}

	@Override
	protected void onStartLoading() {
		listeners.onStartLoad();
		super.onStartLoading();
	}

	@Override
	public void deliverResult(Cursor cursor) {
		listeners.onFinishLoad();
		super.deliverResult(cursor);
	}

	public interface Query {
		String[] PROJECTION = {
				ItemsContract.Items._ID,
				ItemsContract.Items.TITLE,
				ItemsContract.Items.PUBLISHED_DATE,
				ItemsContract.Items.AUTHOR,
				ItemsContract.Items.THUMB_URL,
				ItemsContract.Items.PHOTO_URL,
				ItemsContract.Items.ASPECT_RATIO,
				ItemsContract.Items.BODY,
		};

		int _ID = 0;
		int TITLE = 1;
		int PUBLISHED_DATE = 2;
		int AUTHOR = 3;
		int THUMB_URL = 4;
		int PHOTO_URL = 5;
		int ASPECT_RATIO = 6;
		int BODY = 7;
	}

	public interface LoaderListeners {
		void onStartLoad();

		void onFinishLoad();
	}
}
