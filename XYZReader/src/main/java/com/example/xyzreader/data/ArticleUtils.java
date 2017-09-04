package com.example.xyzreader.data;

import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.xyzreader.pojo.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Package Name:   com.example.xyzreader.data
 * Project:        xyz-reader-starter-code
 * Created by davis, on 9/4/17
 */

public class ArticleUtils {

    // Log tag
    private static String TAG = ArticleUtils.class.getSimpleName();

    // Date formats
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);
    // Most time functions can only handle 1902 - 2037
    private static Date START_OF_EPOCH = new GregorianCalendar(2,1,1).getTime();

    public static ArrayList<Article> generateArticlesFromCursor(Cursor cursor) {
        int initialPosition = cursor.getPosition();

        cursor.moveToFirst();
        ArrayList<Article> articles = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            // id
            long id = cursor.getLong(ArticleLoader.Query._ID);
            // author
            String author = cursor.getString(ArticleLoader.Query.AUTHOR);
            // title
            String title = cursor.getString(ArticleLoader.Query.TITLE);
            // body
            // TODO: Format body correctly
//            String body = cursor.getString(ArticleLoader.Query.BODY);
            String body = "test bod";
            // date
            String date;
            Date publishedDate = parsePublishedDate(cursor);
            if (!publishedDate.before(START_OF_EPOCH)) {
                date = DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(),
                        System.currentTimeMillis(),
                        DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL
                ).toString();
            } else {
                // If date is before 1902, just show the string
                date = dateFormat.format(publishedDate);
            }
            // thumbnailUrl
            String thumbnailUrl = cursor.getString(ArticleLoader.Query.THUMB_URL);
            // photoUrl
            String photoUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);

            Article article = new Article(id, title, date, author, body, thumbnailUrl, photoUrl);
            articles.add(article);

            cursor.moveToNext();
        }

        // return cursor to initial position
        cursor.moveToPosition(initialPosition);

        return articles;
    }

    private static Date parsePublishedDate(Cursor cursor) {
        try {
            String date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }
}
