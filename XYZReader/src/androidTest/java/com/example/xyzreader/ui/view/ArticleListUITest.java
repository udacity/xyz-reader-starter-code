package com.example.xyzreader.ui.view;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.xyzreader.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onIdle;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ArticleListUITest {

	@Rule
	public IntentsTestRule<ArticleListActivity> intentMain = new IntentsTestRule<>(ArticleListActivity.class, true, true);

	@Test
	public void showList_onStart() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(hasDescendant(withId(R.id.article_title))));
	}

	@Test
	public void showArticleDetailsView_onSelectArticle() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
				1, ViewActions.click()
		));
		onView(withId(R.id.pager)).check(matches(isDisplayed()));
	}
}
