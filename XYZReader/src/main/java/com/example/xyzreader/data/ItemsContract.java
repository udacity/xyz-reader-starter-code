package com.example.xyzreader.data;

import android.net.Uri;

public class ItemsContract {
	static final String CONTENT_AUTHORITY = "com.example.xyzreader";
	private static final Uri BASE_URI = Uri.parse("content://com.example.xyzreader");

	interface ItemsColumns {
		/** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
		String _ID = "_id";
		/** Type: TEXT */
		String SERVER_ID = "server_id";
		/** Type: TEXT NOT NULL */
		String TITLE = "title";
		/** Type: TEXT NOT NULL */
		String AUTHOR = "author";
		/** Type: TEXT NOT NULL */
		String BODY = "body";
        /** Type: TEXT NOT NULL */
        String THUMB_URL = "thumb_url";
		/** Type: TEXT NOT NULL */
		String PHOTO_URL = "photo_url";
		/** Type: REAL NOT NULL DEFAULT 1.5 */
		String ASPECT_RATIO = "aspect_ratio";
		/** Type: INTEGER NOT NULL DEFAULT 0 */
		String PUBLISHED_DATE = "published_date";
	}

	public static class Items implements ItemsColumns {
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.xyzreader.items";
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.xyzreader.items";

		static final String DEFAULT_SORT = PUBLISHED_DATE + " DESC";

		/** Matches: /items/ */
		static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath("items").build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long _id) {
			return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
		}

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
	}

	private ItemsContract() {
	}
}
