package com.example.xyzreader.ui.adapter;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.view.ArticleListActivity;
import com.example.xyzreader.ui.view.helper.ImageLoaderHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
	private static final String TAG = ArticleAdapter.class.toString();

	private ArticleListActivity activity;
	private Cursor mCursor;
	private AdapterListener listener;
	private DateFormat dateFormat;
	private DateFormat outputFormat;

	// Most time functions can only handle 1902 - 2037
	private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

	public ArticleAdapter(ArticleListActivity activity, Cursor cursor, AdapterListener listener) {
		this.activity = activity;
		mCursor = cursor;
		this.listener = listener;

		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
		outputFormat = SimpleDateFormat.getDateTimeInstance();
	}

	@Override
	public long getItemId(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getLong(ArticleLoader.Query._ID);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = activity.getLayoutInflater().inflate(R.layout.list_item_article, parent, false);

		return new ViewHolder(view);
	}

	private Date parsePublishedDate() {
		try {
			String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			Log.e(TAG, ex.getMessage());
			Log.i(TAG, "passing today's date");
			return new Date();
		}
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
		mCursor.moveToPosition(position);
		holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
		Date publishedDate = parsePublishedDate();
		if (!publishedDate.before(START_OF_EPOCH.getTime())) {

			holder.subtitleView.setText(Html.fromHtml(
					DateUtils.getRelativeTimeSpanString(
							publishedDate.getTime(),
							System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
							DateUtils.FORMAT_ABBREV_ALL).toString()
							+ "<br/>" + " by "
							+ mCursor.getString(ArticleLoader.Query.AUTHOR)));
		} else {
			holder.subtitleView.setText(Html.fromHtml(
					outputFormat.format(publishedDate)
							+ "<br/>" + " by "
							+ mCursor.getString(ArticleLoader.Query.AUTHOR)));
		}
		String imageURI = mCursor.getString(ArticleLoader.Query.THUMB_URL);
		ImageLoaderHelper.getInstance(activity).getImageLoader()
				.get(imageURI, new ImageLoader.ImageListener() {
					@Override
					public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
						Bitmap bitmap = imageContainer.getBitmap();
						if (bitmap != null) {
							holder.thumbnailView.setImageBitmap(bitmap);
						}
					}

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						holder.thumbnailView.setImageDrawable(activity.getResources().getDrawable(R.drawable.empty_detail));
					}
				});
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.selectArticle(holder.getAdapterPosition(), getItemId(holder.getAdapterPosition()));
			}
		});
	}

	@Override
	public int getItemCount() {
		return mCursor.getCount();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView thumbnailView;
		TextView titleView;
		TextView subtitleView;

		ViewHolder(View view) {
			super(view);
			thumbnailView = view.findViewById(R.id.iv_thumbnail);
			titleView = view.findViewById(R.id.tv_article_title);
			subtitleView = view.findViewById(R.id.tv_article_subtitle);
		}
	}

	public interface AdapterListener {
		void selectArticle(int position, Long id);
	}
}
