package com.example.xyzreader.ui.view.helper;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.example.xyzreader.R;

public final class ActivityHelper {

	public static void configureActionBar(AppCompatActivity activity, Toolbar toobar) {
		activity.setSupportActionBar(toobar);
		ActionBar supportActionBar = activity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayShowTitleEnabled(false);
			supportActionBar.setElevation(activity.getResources().getDimension(R.dimen.ab_elevation));
		}
	}

	public static void showErrorMsgWithSnack(View rootView, String msg) {
		Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG);
		snackbar.setActionTextColor(rootView.getResources().getColor(R.color.red));

		snackbar.show();
	}

	public static void showSucessMsgWithSnack(View rootView, String msg) {
		Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT);
		snackbar.setActionTextColor(rootView.getResources().getColor(R.color.blue));

		snackbar.show();
	}

	public static void configureHomeButton(AppCompatActivity activity) {
//		Objects.requireNonNull(activity.getSupportActionBar()).setHomeButtonEnabled(true);
		assert activity.getSupportActionBar() != null;
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
		activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
		activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
		activity.getSupportActionBar().setHomeAsUpIndicator(activity.getResources().getDrawable(R.drawable.ic_arrow_back));
	}
}
