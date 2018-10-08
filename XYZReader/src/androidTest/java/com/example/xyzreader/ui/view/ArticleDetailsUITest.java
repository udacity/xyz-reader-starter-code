package com.example.xyzreader.ui.view;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.xyzreader.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onIdle;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ArticleDetailsUITest {

	@Rule
	public IntentsTestRule<ArticleDetailActivity> intentMain = new IntentsTestRule<>(ArticleDetailActivity.class, true, false); // Nao inicia para criar intent com params

	@Test
	public void showTitleBodyArticle_onStart() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
				1, click()
		));
		onView(withId(R.id.article_title)).check(matches(isDisplayed()));
		onView(withId(R.id.article_body)).check(matches(isDisplayed()));
	}

	@Test
	public void showShareActivity_onSelectShare() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
				1, click()
		));
		onView(withId(R.id.article_body)).check(matches(isDisplayed()));
		onView(withId(R.id.share_fab)).perform(click());
		// TODO: 08/10/18 Add teste de activity de shar, Descobrir algum id dela ou se testa por passagem de intent
	}

	@Test
	public void showProgress_onSelectArticle() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
				1, click()
		));
		onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
	}
}
