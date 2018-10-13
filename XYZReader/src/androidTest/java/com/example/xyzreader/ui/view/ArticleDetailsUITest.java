package com.example.xyzreader.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import org.junit.Before;
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

	private final long fakeItemId = 1L;
	@Rule
	public IntentsTestRule<ArticleDetailActivity> intentMain = new IntentsTestRule<>(ArticleDetailActivity.class, true, false); // Nao inicia para criar intent com params
	private final Context targetContext = InstrumentationRegistry.getTargetContext();

	@Before
	public void setUp() {
		Intent startIntent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(fakeItemId));
		intentMain.launchActivity(startIntent);
	}

	@Test
	public void showTitleBodyArticle_onStart() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.vp_article)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.tv_article_title)).check(matches(isDisplayed()));
		onView(withId(R.id.tv_article_body)).check(matches(isDisplayed()));
	}

	@Test
	public void showShareActivity_onSelectShare() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.vp_article)).check(matches(isDisplayed()));
		onIdle();
		onView(withId(R.id.fab_share)).perform(click());
		onIdle();
		// TODO: 08/10/18 Add teste de activity de shar, Descobrir algum id dela ou se testa por passagem de intent
	}

	@Test
	public void showProgress_onLoadArticle() {
		onView(isRoot()).check(matches(isDisplayed()));
		onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
		onIdle();
	}
}
