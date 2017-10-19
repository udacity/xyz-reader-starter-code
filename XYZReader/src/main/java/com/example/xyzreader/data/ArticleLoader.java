package com.example.xyzreader.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.Html;
import android.text.Spanned;

import static com.example.xyzreader.data.ArticleLoader.Query.ARTICLE_LIST_PROJECTION;
import static com.example.xyzreader.data.ArticleLoader.Query.SINGLE_ARTICLE_PROJECTION;

/**
 * Helper for loading a list of articles information or a single article.
 */
public class ArticleLoader extends CursorLoader {
    public static ArticleLoader newAllArticlesInstance(Context context) {
        return new ArticleLoader(context, ItemsContract.Items.buildDirUri(), SINGLE_ARTICLE_PROJECTION);
    }

    public static ArticleLoader newAllArticlesInfoInstance(Context context) {
        return new ArticleLoader(context, ItemsContract.Items.buildDirUri(), ARTICLE_LIST_PROJECTION);
    }

    public static ArticleLoader newInstanceForItemId(Context context, long itemId) {
        return new ArticleLoader(context, ItemsContract.Items.buildItemUri(itemId), SINGLE_ARTICLE_PROJECTION);
    }

    private ArticleLoader(Context context, Uri uri, String[] Projection) {
        super(context, uri, Projection, null, null, ItemsContract.Items.DEFAULT_SORT);
    }

    public interface Query {
        String[] SINGLE_ARTICLE_PROJECTION = {
                ItemsContract.Items._ID,
                ItemsContract.Items.TITLE,
                ItemsContract.Items.PUBLISHED_DATE,
                ItemsContract.Items.AUTHOR,
                ItemsContract.Items.THUMB_URL,
                ItemsContract.Items.PHOTO_URL,
                ItemsContract.Items.ASPECT_RATIO,
                ItemsContract.Items.BODY,
        };

        String[] ARTICLE_LIST_PROJECTION = {
                ItemsContract.Items._ID,
                ItemsContract.Items.TITLE,
                ItemsContract.Items.PUBLISHED_DATE,
                ItemsContract.Items.AUTHOR,
                ItemsContract.Items.THUMB_URL,
                ItemsContract.Items.PHOTO_URL,
                ItemsContract.Items.ASPECT_RATIO,
                // Remove Body for more efficient projection when the body of text is not shown
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

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
