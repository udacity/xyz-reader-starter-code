package com.example.xyzreader.ui.presenter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.ui.view.BaseView;

public interface ArticleDetailContract {

	interface View extends BaseView {

		void showArticleDetailsView(Article article);

		void setArticleListPositionTo(int position);

		void createAdapter(Cursor cursor);
	}

	interface Presenter {

		void restoreSavedState(Bundle savedInstanceState);

		void onLoaderFinish(Loader<Cursor> cursorLoader, Cursor cursor);

		void savePositionState(Bundle outState, int verticalScrollbarPosition);
	}
}
